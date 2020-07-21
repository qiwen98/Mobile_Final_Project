package com.example.TimeSuler;

public class Receiver {

    public String name, image, email;
    private boolean isSelected = false;

    public Receiver()
    {

    }

    public Receiver(String name, String image, String email) {
        this.name = name;
        this.image = image;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}

