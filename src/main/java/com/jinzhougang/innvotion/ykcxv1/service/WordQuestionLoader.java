package com.jinzhougang.innvotion.ykcxv1.service;

import com.jinzhougang.innvotion.ykcxv1.entity.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WordQuestionLoader {

    @Value("${quiz.word-file-path}")
    private String wordFilePath;

    // 分节标题（支持中英文冒号、全角空格等）
    private static final Pattern SECTION_SINGLE = Pattern.compile("(?m)^\\s*一、\\s*单选题[（(].*?[)）]?.*$");
    private static final Pattern SECTION_MULTI  = Pattern.compile("(?m)^\\s*二、\\s*多选题[（(].*?[)）]?.*$");
    private static final Pattern SECTION_JUDGE  = Pattern.compile("(?m)^\\s*三、\\s*判断题[（(].*?[)）]?.*$");

    // 题块：编号. 开头，到下一个编号. 或分节末
    private static final Pattern QUESTION_BLOCK = Pattern.compile("(?ms)^\\s*\\d+\\.(.+?)(?=^\\s*\\d+\\.|\\z)");

    // 选项行：前导空格 + A|B|C|D + .|、 + 文本
    private static final Pattern OPTION_LINE = Pattern.compile("(?m)^\\s*([A-D])\\s*(?:[\\.|。．、:：\\)|）\\-])?\\s*(.+?)\\s*$");

    // 答案行：答案：XXXX
    private static final Pattern ANSWER_LINE = Pattern.compile("(?mi)^\\s*答\\s*案\\s*[：:]\\s*(.+?)\\s*$");

    // 缓存（按文件修改时间刷新）
    private List<Question> allQuestionsCache = null;
    private long lastModified = 0;

    public List<Question> loadRandomQuestions() throws IOException {
        File file = new File(wordFilePath);
        if (!file.exists() || !file.canRead()) {
            throw new IOException("题库文件不存在或不可读: " + wordFilePath);
        }
        if (allQuestionsCache == null || file.lastModified() != lastModified) {
            allQuestionsCache = loadAllQuestionsInternal(file);
            lastModified = file.lastModified();
        }

        List<Question> singles = new ArrayList<>();
        List<Question> multis  = new ArrayList<>();
        List<Question> judges  = new ArrayList<>();
        for (Question q : allQuestionsCache) {
            if (q.getType() == 1) {
                singles.add(q);
            } else if (q.getType() == 2) {
                multis.add(q);
            } else if (q.getType() == 3) {
                judges.add(q);
            }
        }

        Collections.shuffle(singles);
        Collections.shuffle(multis);
        Collections.shuffle(judges);

        List<Question> selected = new ArrayList<>();
        int s = Math.min(45, singles.size());
        int m = Math.min(20, multis.size());
        int j = Math.min(20, judges.size());
        selected.addAll(singles.subList(0, s));
        selected.addAll(multis.subList(0, m));
        selected.addAll(judges.subList(0, j));

        if (selected.size() < 85) {
            List<Question> rest = new ArrayList<>();
            if (singles.size() > s) {
                rest.addAll(singles.subList(s, singles.size()));
            }
            if (multis.size() > m) {
                rest.addAll(multis.subList(m, multis.size()));
            }
            if (judges.size() > j) {
                rest.addAll(judges.subList(j, judges.size()));
            }
            Collections.shuffle(rest);
            int need = 85 - selected.size();
            selected.addAll(rest.subList(0, Math.min(need, rest.size())));
        }

        for (int i = 0; i < selected.size(); i++) {
            selected.get(i).setId(i + 1);
        }
        return selected;
    }

    public Map<String, Object> getQuestionStats() throws IOException {
        File file = new File(wordFilePath);
        if (allQuestionsCache == null || file.lastModified() != lastModified) {
            allQuestionsCache = loadAllQuestionsInternal(file);
            lastModified = file.lastModified();
        }
        int s = 0, m = 0, j = 0;
        for (Question q : allQuestionsCache) {
            if (q.getType() == 1) {
                s++;
            } else if (q.getType() == 2) {
                m++;
            } else if (q.getType() == 3) {
                j++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", allQuestionsCache.size());
        map.put("singleChoice", s);
        map.put("multipleChoice", m);
        map.put("trueFalse", j);
        map.put("filePath", wordFilePath);
        map.put("lastModified", file.exists() ? file.lastModified() : 0);
        return map;
    }

    public boolean isFileExists() {
        File f = new File(wordFilePath);
        return f.exists() && f.canRead();
    }

    public Map<String, Object> getFileInfo() {
        File f = new File(wordFilePath);
        Map<String, Object> m = new HashMap<>();
        m.put("exists", f.exists());
        m.put("canRead", f.canRead());
        m.put("path", wordFilePath);
        m.put("size", f.exists() ? f.length() : 0);
        m.put("lastModified", f.exists() ? f.lastModified() : 0);
        return m;
    }

    // ============== 内部 ==============

    private List<Question> loadAllQuestionsInternal(File file) throws IOException {
        String allText;
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument doc = new XWPFDocument(fis)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(normalizeSpaces(p.getText())).append("\n");
            }
            allText = sb.toString();
        }

        // 按分节切片
        Map<String, Integer> anchors = new LinkedHashMap<>();
        findSectionAnchor(allText, SECTION_SINGLE, "SINGLE", anchors);
        findSectionAnchor(allText, SECTION_MULTI,  "MULTI",  anchors);
        findSectionAnchor(allText, SECTION_JUDGE,  "JUDGE",  anchors);

        List<Question> result = new ArrayList<>();
        List<Map.Entry<String, Integer>> idx = new ArrayList<>(anchors.entrySet());
        idx.sort(Comparator.comparingInt(Map.Entry::getValue));

        for (int i = 0; i < idx.size(); i++) {
            String key = idx.get(i).getKey();
            int start = idx.get(i).getValue();
            int end = (i + 1 < idx.size()) ? idx.get(i + 1).getValue() : allText.length();
            String sectionText = allText.substring(start, end);

            int type = key.equals("SINGLE") ? 1 : key.equals("MULTI") ? 2 : 3;
            result.addAll(parseSection(sectionText, type));
        }
        return result;
    }

    private static void findSectionAnchor(String text, Pattern pat, String name, Map<String, Integer> anchors) {
        Matcher m = pat.matcher(text);
        if (m.find()) {
            anchors.put(name, m.start());
        }
    }

    private List<Question> parseSection(String section, int type) {
        List<Question> questions = new ArrayList<>();
        Matcher qm = QUESTION_BLOCK.matcher(section);
        while (qm.find()) {
            String block = qm.group(0); // 含编号
            Question q = parseBlock(block, type);
            if (q != null) {
                questions.add(q);
            }
        }
        return questions;
    }

    private Question parseBlock(String block, int type) {
        // 提取答案
        List<String> answers = extractAnswers(block, type);
        // 提取题干（去掉选项与答案行）
        String stem = stripOptionsAndAnswer(block);

        Question q = new Question();
        q.setType(type);
        q.setContent(cleanStem(stem));
        q.setScore(type == 2 ? 2 : 1);

        // 选项
        if (type == 3) {
            // 判断题固定选项
            List<Option> opts = new ArrayList<>();
            opts.add(new Option("A", "正确"));
            opts.add(new Option("B", "错误"));
            q.setOptions(opts);
            // 判断题答案映射为 A/B
            if (!answers.isEmpty()) {
                String a = answers.get(0);
                String ab = mapJudgeToAB(a);
                q.setCorrectAnswers(Collections.singletonList(ab));
            } else {
                q.setCorrectAnswers(Collections.singletonList("A"));
            }
        } else {
            List<Option> opts = extractOptions(block);
            q.setOptions(opts);
            // 选择题答案保持字母（如 B、BC）
            if (answers.isEmpty()) {
                q.setCorrectAnswers(Collections.emptyList());
            } else {
                q.setCorrectAnswers(answers);
            }
        }
        return q;
    }

    private static String cleanStem(String s) {
        // 去掉起始编号与多余空白
        String t = s.replaceFirst("(?ms)^\\s*\\d+\\.", "").trim();
        // 去掉尾部残留“答案：...”部分
        t = t.replaceAll("(?mi)答\\s*案\\s*[：:].+$", "").trim();
        return t;
    }

    private static String stripOptionsAndAnswer(String block) {
        // 移除选项与答案行，仅保留题干文本
        String withoutOptions = block.replaceAll(OPTION_LINE.pattern(), "");
        return withoutOptions.replaceAll(ANSWER_LINE.pattern(), "");
    }

    private static List<Option> extractOptions(String block) {
        List<Option> list = new ArrayList<>();
        Matcher m = OPTION_LINE.matcher(block);
        while (m.find()) {
            String key = m.group(1).trim();
            String text = m.group(2).trim();
            list.add(new Option(key, text));
        }
        // 去重并按 A-D 排序
        Map<String, Option> map = new TreeMap<>();
        for (Option o : list) {
            map.put(o.getKey(), o);
        }
        return new ArrayList<>(map.values());
    }

    private static List<String> extractAnswers(String block, int type) {
        Matcher m = ANSWER_LINE.matcher(block);
        if (!m.find()) {
            return Collections.emptyList();
        }
        String raw = m.group(1).trim();
        raw = raw.replaceAll("\\s+", "");
        // 判断题中文 -> 规范标记
        if (type == 3) {
            String norm = normalizeJudge(raw);
            return norm.isEmpty() ? Collections.emptyList() : Collections.singletonList(norm);
        }
        // 选择题：提取连续字母，如 BC
        List<String> ans = new ArrayList<>();
        for (char c : raw.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                ans.add(String.valueOf(c));
            }
        }
        return ans;
    }

    private static String normalizeJudge(String s) {
        // 支持：正确/对/是/YES/True -> 正确； 错误/错/否/NO/False -> 错误
        String v = s.toLowerCase(Locale.ROOT);
        if (v.contains("正") || v.contains("对") || v.equals("是") || v.equals("yes") || v.equals("true")) {
            return "正确";
        }
        if (v.contains("错") || v.contains("误") || v.equals("否") || v.equals("no") || v.equals("false")) {
            return "错误";
        }
        return s;
    }

    private static String mapJudgeToAB(String norm) {
        return (norm.contains("正") || norm.contains("对")) ? "A" : "B";
    }

    private static String normalizeSpaces(String s) {
        if (s == null) {
            return "";
        }
        // 统一全角空格与多空格
        return s.replace('\u3000', ' ').replaceAll("[ \\t\\x0B\\f\\r]+", " ").trim();
    }
}