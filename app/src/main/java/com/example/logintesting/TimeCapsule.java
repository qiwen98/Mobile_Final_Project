package com.example.logintesting;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.List;

public class TimeCapsule {
    private String title;
    private String description;
    private String ImageDownloadURL;
    private String VideoDownloadURL;
    private int priority;
    private String CapsuleType;
    private List<String> Receiver;
    private List<String> favouritebyUser;
    private String SceneformKey;
    private LatLng GoogleMapLocation;
    private Timestamp ValidTimeStampForOpen;
    private Double GoogleMapLocation_latitude;
    private Double GoogleMapLocation_longitude;
    private List<String> OpenedbyUser;

    public TimeCapsule(){

    }




    public void setImageDownloadURL(String imageDownloadURL) {
        ImageDownloadURL = imageDownloadURL;
    }



    public TimeCapsule(String title, String description, int priority, String ImageDownloadURL, String VideoDownloadURL, String CapsuleType, List<String>Receiver, List<String> favouritebyUser, List<String> OpenedbyUser,String SceneformKey, Double GoogleMapLocation_latitude, Double GoogleMapLocation_longitude, Timestamp ValidTimeStampForOpen) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.ImageDownloadURL= ImageDownloadURL;
        this.VideoDownloadURL= VideoDownloadURL;
        this.CapsuleType=CapsuleType;
        this.Receiver=Receiver;
        this.favouritebyUser=favouritebyUser;
        this.OpenedbyUser=OpenedbyUser;
        this.SceneformKey=SceneformKey;
      //  this.GoogleMapLocation=GoogleMapLocation;
        this.GoogleMapLocation_latitude = GoogleMapLocation_latitude;
        this.GoogleMapLocation_longitude = GoogleMapLocation_longitude;
        this.ValidTimeStampForOpen=ValidTimeStampForOpen;

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

    public String getCapsuleType() {
        return CapsuleType;
    }

    public List<String> getReceiver() {
        return Receiver;
    }

    public List<String> getFavouritebyUser() {
        return favouritebyUser;
    }

    public String getSceneformKey() {
        return SceneformKey;
    }





    public Timestamp getValidTimeStampForOpen() {
        return ValidTimeStampForOpen;
    }

    public void setCapsuleType(String capsuleType) {
        CapsuleType = capsuleType;
    }



    public void setSceneformKey(String sceneformKey) {
        SceneformKey = sceneformKey;
    }

    public void setValidTimeStampForOpen(Timestamp validTimeStampForOpen) {
        ValidTimeStampForOpen = validTimeStampForOpen;
    }

    public void setFavouritebyUser(List<String> favouritebyUser) {
        this.favouritebyUser = favouritebyUser;
    }



    public void setReceiver(List<String> receiver) {
        Receiver = receiver;
    }

    public Double getGoogleMapLocation_latitude() {
        return GoogleMapLocation_latitude;
    }

    public void setGoogleMapLocation_latitude(Double googleMapLocation_latitude) {
        GoogleMapLocation_latitude = googleMapLocation_latitude;
    }

    public Double getGoogleMapLocation_longitude() {
        return GoogleMapLocation_longitude;
    }

    public void setGoogleMapLocation_longitude(Double googleMapLocation_longitude) {
        GoogleMapLocation_longitude = googleMapLocation_longitude;
    }

    public List<String> getOpenedbyUser() {
        return OpenedbyUser;
    }

    public void setOpenedbyUser(List<String> openedbyUser) {
        OpenedbyUser = openedbyUser;
    }
}
