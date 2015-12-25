package com.kakaxicm.geekming.utils;

public enum ResEnum {
    LAYOUT("layout"), ID("id"), ANIMATOR("animator"), STRING("string"), COLOR("color"), STYLEABLE("styleable"), ARRAY(
            "array"), DRAWABLE("drawable");

    // 成员变量
    private String name;

    // 构造方法
    private ResEnum(String name) {
        this.name = name;
    }

    // 普通方法
    public String getName() {
        return name;
    }
}
