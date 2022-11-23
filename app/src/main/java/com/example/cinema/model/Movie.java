package com.example.cinema.model;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {

    private long id;
    private String name;
    private String description;
    private int price;
    private String date;
    private String image;
    private String url;
    private List<RoomFirebase> rooms;

    public Movie() {
    }

    public Movie(long id, String name, String description, int price, String date,
                 String image, String url, List<RoomFirebase> rooms) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.date = date;
        this.image = image;
        this.url = url;
        this.rooms = rooms;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<RoomFirebase> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomFirebase> rooms) {
        this.rooms = rooms;
    }
}
