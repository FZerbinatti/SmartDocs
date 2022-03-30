package com.dreamsphere.smartdocs.Models;

import android.graphics.Bitmap;

public class Marker {

    String description;
    Bitmap marker_image;

    public Marker(String description, Bitmap marker_image) {
        this.description = description;
        this.marker_image = marker_image;
    }

    public Marker() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getMarker_image() {
        return marker_image;
    }

    public void setMarker_image(Bitmap marker_image) {
        this.marker_image = marker_image;
    }
}
