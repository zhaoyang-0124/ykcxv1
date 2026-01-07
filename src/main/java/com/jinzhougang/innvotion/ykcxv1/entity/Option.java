package com.jinzhougang.innvotion.ykcxv1.entity;

import lombok.Data;

@Data
public class Option {
    private String key; // A, B, C, D
    private String text;

    public Option() {}

    public Option(String key, String text) {
        this.key = key;
        this.text = text;
    }
}