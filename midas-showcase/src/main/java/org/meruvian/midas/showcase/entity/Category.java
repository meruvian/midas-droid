package org.meruvian.midas.showcase.entity;

/**
 * Created by ludviantoovandi on 17/01/15.
 */
public class Category extends DefaultEntity {
    private String name = "";
    private String description = "";
    private String parentCategory = "";

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
