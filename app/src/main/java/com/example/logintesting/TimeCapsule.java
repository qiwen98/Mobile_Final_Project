package com.example.logintesting;

import android.net.Uri;

import com.facebook.internal.ImageDownloader;

public class TimeCapsule {
    private String title;
    private String description;
    private String ImageDownloadURL;
    private String VideoDownloadURL;
    private int priority;

    public TimeCapsule(){

    }

    public void setImageDownloadURL(String imageDownloadURL) {
        ImageDownloadURL = imageDownloadURL;
    }

    public TimeCapsule(String title, String description, int priority, String ImageDownloadURL,String VideoDownloadURL) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.ImageDownloadURL= ImageDownloadURL;
        this.VideoDownloadURL= VideoDownloadURL;
    }

    public String getVideoDownloadURL() {
        return VideoDownloadURL;
    }

    public void setVideoDownloadURL(String videoDownloadURL) {
        VideoDownloadURL = videoDownloadURL;
    }

    public String getImageDownloadURL() {
        return ImageDownloadURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
