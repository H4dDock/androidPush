package com.github.vectorway.vectorway;

public class News {
    public String title;
    public String info;
    public String link;

    public News(String title, String info, String link) {
        this.title = title;
        this.info = info;
        this.link = link;
    }

    public News() {
        title = "void";
        info = "void";
        link = "void";
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
