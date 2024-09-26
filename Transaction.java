package com.example.points.model;

import java.time.LocalDateTime;

public class Transaction {
    private String payer;
    private int points;
    private LocalDateTime timestamp;

    // Getters and setters
    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
