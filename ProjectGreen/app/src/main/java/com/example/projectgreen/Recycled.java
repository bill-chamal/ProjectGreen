package com.example.projectgreen;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Comparator;

public class Recycled implements Serializable {
    private transient Timestamp timestamp;
    private int pieces;
    private Material mat;

    public final static int NOT_APPROVED = 0;
    public final static int APPROVED = 1;
    public final static int REJECTED = 2;
    private int approved;

    public Recycled() {
    }

    public Recycled(Material mat, int pieces, Timestamp tt, int approved) {
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

    public int isApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }
}

class RecycledComperater implements Comparator<Recycled>, Serializable {

    @Override
    public int compare(Recycled o1, Recycled o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}