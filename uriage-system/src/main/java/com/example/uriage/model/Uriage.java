package com.example.uriage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Uriage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shohinName;
    private int quantity;
    private int price;
    private String date;

    public Uriage() {}

    public Uriage(String shohinName, int quantity, int price, String date) {
        this.shohinName = shohinName;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShohinName() { return shohinName; }
    public void setShohinName(String shohinName) { this.shohinName = shohinName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTotal() { return price * quantity; }
}
