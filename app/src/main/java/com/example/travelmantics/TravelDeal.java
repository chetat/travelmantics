package com.example.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeal implements Parcelable {
    private String id;
    private String title;
    private String descriprion;
    private String price;
    private String imageUrl;
    private String imageName;

    TravelDeal(){

    }

    public TravelDeal( String title, String descriprion, String price, String imageUrl, String imageName) {
        this.setId(id);
        this.setTitle(title);
        this.setDescriprion(descriprion);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);


    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriprion() {
        return descriprion;
    }

    public void setDescriprion(String descriprion) {
        this.descriprion = descriprion;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected TravelDeal(Parcel in) {
        id = in.readString();
        title = in.readString();
        descriprion = in.readString();
        price = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(descriprion);
        dest.writeString(price);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TravelDeal> CREATOR = new Parcelable.Creator<TravelDeal>() {
        @Override
        public TravelDeal createFromParcel(Parcel in) {
            return new TravelDeal(in);
        }

        @Override
        public TravelDeal[] newArray(int size) {
            return new TravelDeal[size];
        }
    };

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}