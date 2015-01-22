package org.meruvian.midas.showcase.entity;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class News extends DefaultEntity {
    private String title = "";
    private String content = "";
    private Category category = new Category();

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
