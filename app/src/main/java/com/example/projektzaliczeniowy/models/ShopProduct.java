package com.example.projektzaliczeniowy.models;

public class ShopProduct {
    private String description;
    private int price, image;

    public ShopProduct(String description, int price, int image) {
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }
}
