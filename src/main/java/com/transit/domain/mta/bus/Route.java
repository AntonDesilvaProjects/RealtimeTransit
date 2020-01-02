package com.transit.domain.mta.bus;

public class Route {
    private String id;
    private String description;
    private String longName;
    private String shortName;
    private String textColor;
    private int type;
    private String url;
    private String color;

    public String getId() {
        return id;
    }

    public Route setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Route setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLongName() {
        return longName;
    }

    public Route setLongName(String longName) {
        this.longName = longName;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public Route setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getTextColor() {
        return textColor;
    }

    public Route setTextColor(String textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getType() {
        return type;
    }

    public Route setType(int type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Route setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Route setColor(String color) {
        this.color = color;
        return this;
    }
}
