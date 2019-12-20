package com.example.magisterka;

import android.widget.ImageView;

public class Label {
    private String name;
    private ImageView image;
    private int toDelete;

    public Label(String name, ImageView image, int toDelete) {
        this.name = name;
        this.image = image;
        this.toDelete = toDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public int getToDelete() {
        return toDelete;
    }

    public void setToDelete(int toDelete) {
        this.toDelete = toDelete;
    }
}
