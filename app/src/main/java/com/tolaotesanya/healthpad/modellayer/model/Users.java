package com.tolaotesanya.healthpad.modellayer.model;

public class Users {

    private String display_name;
    private String image;
    private String thumb_image;

    public Users() {
    }

    public Users(String display_name, String image, String thumb_image) {
        this.display_name = display_name;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
