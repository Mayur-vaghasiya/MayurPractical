package com.example.mayurpractical.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CountryModel implements Serializable {
    private final static long serialVersionUID = -8720416255089533838L;
    @SerializedName("RestResponse")
    @Expose
    private RestResponse restResponse;

    public RestResponse getRestResponse() {
        return restResponse;
    }

    public void setRestResponse(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    public class RestResponse implements Serializable {

        private final static long serialVersionUID = -6088073706495242947L;
        @SerializedName("messages")
        @Expose
        private ArrayList<String> messages = null;
        @SerializedName("result")
        @Expose
        private ArrayList<Result> result = null;

        public ArrayList<String> getMessages() {
            return messages;
        }

        public void setMessages(ArrayList<String> messages) {
            this.messages = messages;
        }

        public ArrayList<Result> getResult() {
            return result;
        }

        public void setResult(ArrayList<Result> result) {
            this.result = result;
        }
    }

    public static class Result implements Serializable {

        private final static long serialVersionUID = 7554979505516925523L;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alpha2_code")
        @Expose
        private String alpha2Code;
        @SerializedName("alpha3_code")
        @Expose
        private String alpha3Code;

        private String imgPath;

        private byte[] img;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlpha2Code() {
            return alpha2Code;
        }

        public void setAlpha2Code(String alpha2Code) {
            this.alpha2Code = alpha2Code;
        }

        public String getAlpha3Code() {
            return alpha3Code;
        }

        public void setAlpha3Code(String alpha3Code) {
            this.alpha3Code = alpha3Code;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public byte[] getImage() {
            return this.img;
        }

        public void setImage(byte[] b) {
            this.img = b;
        }
    }

}