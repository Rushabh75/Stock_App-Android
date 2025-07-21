package com.example.hw4;

public class NewsItem {
    private String headline;
    private String image;
    private String summary;
    private String source;
    private String datetime;
    private String url;

    // Constructor
    public NewsItem(String headline, String image, String summary, String source, String datetime, String url) {
        this.headline = headline;
        this.image = image;
        this.summary = summary;
        this.source = source;
        this.datetime = datetime;
        this.url = url;
    }

    // Getter and setter for headline
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    // Getter and setter for image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Getter and setter for summary
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // Getter and setter for source
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Getter and setter for datetime
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    // Getter and setter for URL
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
