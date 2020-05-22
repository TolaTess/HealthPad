package com.tolaotesanya.healthpad.model;

public class Doctors {
    private String first_name;
    private String last_name;
    private String speciality;
    private String location;
    private String status;
    private String image;
    private String thumb_image;

    public Doctors(){}

    public Doctors(String first_name, String last_name, String speciality, String location, String status, String image, String thumb_image) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.speciality = speciality;
        this.location = location;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
