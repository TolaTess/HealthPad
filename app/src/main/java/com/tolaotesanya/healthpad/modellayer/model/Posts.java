package com.tolaotesanya.healthpad.modellayer.model;

 /*postMap.put("date", currentDate);
         postMap.put("name", "user's name");
         postMap.put("user_type", "user");
         postMap.put("image", "default");
         postMap.put("caption", "I love this app");
         postMap.put("likes", "3");
         postMap.put("post_type", "tips");*/

public class Posts {

    private String name;
    private long timestamp;
    private String post_id;
    private String user_type;
    private String post_image;
    private String thumb_image;
    private String caption;
    private String user_id;
    private String likes;
    private String post_type;

    public Posts(){
    }

    public Posts(String name, long date, String post_id,
                 String user_type, String post_image, String thumb_image,
                 String caption, String user_id, String likes, String post_type) {
        this.name = name;
        this.timestamp = date;
        this.post_id = post_id;
        this.user_type = user_type;
        this.post_image = post_image;
        this.thumb_image = thumb_image;
        this.caption = caption;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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
