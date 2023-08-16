package com.project.chatgpt.Model;

public class ImageData {
    private String url;
    private Integer drawb;

    public ImageData(String url, Integer drawb) {
        this.url = url;
        this.drawb = drawb;
    }

    public String getUrl() {
        return url;
    }

    public Integer getDrawb() {
        return drawb;
    }
}
