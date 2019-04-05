package com.hzstore.mapproject.models;

public class Review {
    private int id;
    private float rate;
    private String review;
  private int user;
  private User reviewer;

    public int getId() {
        return id;
    }

    public float getRate() {
        return rate;
    }

    public String getReview() {
        return review;
    }

    public int getUser() {
        return user;
    }

    public User getReviewer() {
        return reviewer;
    }
}
