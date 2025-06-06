package com.github.damianjester.nclient.legacy.api.enums;

public enum ImageExt {
    JPG("jpg"), PNG("png"), GIF("gif"), WEBP("webp");

    private final char firstLetter;
    private final String name;

    ImageExt(String name) {
        this.name = name;
        this.firstLetter = name.charAt(0);
    }

    public char getFirstLetter() {
        return firstLetter;
    }

    public String getName() {
        return name;
    }
}
