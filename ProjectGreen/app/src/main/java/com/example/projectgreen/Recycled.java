package com.example.projectgreen;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class Recycled implements Serializable {
    private Timestamp timestamp;
    private int pieces;
    private Material mat;
    private boolean approved;

    public Recycled(){}

    public Recycled(Material mat, int pieces, Timestamp tt, boolean approved){
        this.mat = mat;
        this.pieces = pieces;
        this.timestamp = tt;
        this.approved = approved;
    }


    // GETTERS AND SETTERS //

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
