package com.example.magisterka;

import android.widget.ImageView;

public class Label {
    private String name;
    private ImageView image;
    private boolean toDelete;

    public Label(String name, ImageView image, boolean toDelete) {
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

    public boolean getToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }
}
