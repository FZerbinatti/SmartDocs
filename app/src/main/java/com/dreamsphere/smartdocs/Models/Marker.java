package com.dreamsphere.smartdocs.Models;

import android.graphics.Bitmap;

import java.io.File;

public class Marker {

    String description;
    File file;

    public Marker(String description, File file) {
        this.description = description;
        this.file = file;
    }

    public Marker() {

    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
