package com.tolaotesanya.healthpad.modellayer.model;

public class Posts {

    private String name;
    private long timestamp;
    private String post_id;
    private String body;
    private String user_type;
    private String post_image;
    private String thumb_image;
    private String title;
    private String user_id;
    private String likes;
    private String post_type;

    public Posts(){
    }

    public Posts(String name, long date, String post_id,
                 String body, String user_type, String post_image, String thumb_image,
                 String caption, String user_id, String likes, String post_type) {
        this.name = name;
        this.timestamp = date;
        this.post_id = post_id;
        this.body = body;
        this.user_type = user_type;
        this.post_image = post_image;
        this.thumb_image = thumb_image;
        this.title = caption;
        this.user_id = user_id;
        this.likes = likes;
        this.post_type = post_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }
}
