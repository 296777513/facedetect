package com.facedetect.Bean;

import android.graphics.Bitmap;

import com.faceplusplus.api.FaceDetecter;


/**
 * Created by Android Studio. author: liyachao Date: 15/11/25 Time: 11:17
 */
public class EventBean {
    private String describe;
    private boolean isCorrect;
    private FaceDetecter.Face face;
    private String faceId;
    private int mode;
    private String image;
    private double similarity;



    public EventBean(String describe, boolean isCorrect, FaceDetecter.Face face, String faceId, int mode) {
        this.describe = describe;
        this.isCorrect = isCorrect;
        this.face = face;
        this.faceId = faceId;
        this.mode = mode;
    }

    public EventBean() {

    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public FaceDetecter.Face getFace() {
        return face;
    }

    public void setFace(FaceDetecter.Face face) {
        this.face = face;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "describe='" + describe + '\'' +
                ", isCorrect=" + isCorrect +
                ", face=" + face +
                ", faceId='" + faceId + '\'' +
                ", mode=" + mode +

                ", similarity=" + similarity +
                '}';
    }
}
