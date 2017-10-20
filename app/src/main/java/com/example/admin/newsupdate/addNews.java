package com.example.admin.newsupdate;

/**
 * Created by admin on 9/12/2017.
 */

public class addNews  {

    String description_guj;
    String description_eng;
    String img_url;
    String tag;
    String title_guj;
    String title_eng;
    String user;

    public addNews() {

    }

    public addNews(String description_guj, String description_eng, String img_url, String tag, String title_guj, String title_eng, String user)
    {
        this.title_guj = title_guj;
        this.title_eng = title_eng;
        this.description_guj = description_guj;
        this.description_eng = description_eng;
        this.img_url = img_url;
        this.tag = tag;
        this.user = user;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUser() {
        return user;
    }

    public String getDescription_guj() {
        return description_guj;
    }

    public void setDescription_guj(String description_guj) {
        this.description_guj = description_guj;
    }

    public String getDescription_eng() {
        return description_eng;
    }

    public void setDescription_eng(String description_eng) {
        this.description_eng = description_eng;
    }

    public String getTitle_guj() {
        return title_guj;
    }

    public void setTitle_guj(String title_guj) {
        this.title_guj = title_guj;
    }

    public String getTitle_eng() {
        return title_eng;
    }

    public void setTitle_eng(String title_eng) {
        this.title_eng = title_eng;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
