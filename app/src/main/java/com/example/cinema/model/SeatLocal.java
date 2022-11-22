package com.example.cinema.model;

import java.io.Serializable;

public class SeatLocal implements Serializable {

    private int id;
    private String title;
    private boolean selected;
    private boolean checked;

    public SeatLocal() {
    }

    public SeatLocal(int id, String title, boolean selected) {
        this.id = id;
        this.title = title;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
