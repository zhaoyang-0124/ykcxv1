package com.jinzhougang.innvotion.ykcxv1.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/images")
@CrossOrigin(origins = "*")
public class ImageController {
    @Value("${image.base-dir}")
    private String baseDir;

    @RequestMapping("/getImages")
    public ResponseEntity<Resource> getImage(@RequestParam("filename") String filename) throws Exception {
        if (!StringUtils.hasText(filename) || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }
        Path base = Paths.get(baseDir).toAbsolutePath().normalize();
        Path target = base.resolve(filename).normalize();
        if (!target.startsWith(base) || !Files.exists(target) || !Files.isReadable(target)) {
            return ResponseEntity.notFound().build();
        }
        Resource file = new UrlResource(target.toUri());
        String contentType = Files.probeContentType(target);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}