package com.example.karimall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class sellhelper  {
    String img;
    String name;
    String size;
    String cost;
    String item;

    public sellhelper() {
    }

    public sellhelper(String img, String name, String size, String cost, String item) {
        this.img = img;
        this.item = item;
        this.name = name;
        this.size = size;
        this.cost = cost;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
