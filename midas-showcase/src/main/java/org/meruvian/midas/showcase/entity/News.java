package org.meruvian.midas.showcase.entity;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class News extends DefaultEntity {
    private String title;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
