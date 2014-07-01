package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

public class NewsItem {

    public String date;
    public String title;
    public String description;

    public NewsItem() {

    }

    public NewsItem(String date, String title, String description) {
        super();
        this.date = date;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
