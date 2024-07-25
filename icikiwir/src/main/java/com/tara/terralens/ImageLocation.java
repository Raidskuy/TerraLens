package com.tara.terralens;

public class ImageLocation {
    private String uri;
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String country;

    public ImageLocation() {
        this.uri = uri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
