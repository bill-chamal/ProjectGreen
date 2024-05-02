package com.example.projectgreen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Material implements Serializable {



    private String matName;
    private double value;

    // Firestore: Each custom class must have a public constructor that takes no arguments. In addition, the class must include a public getter for each property
    public Material(){}
    public Material(String name, double v){


        matName = name;
        value = v;
    }



    // GETTERS AND SETTERS //
    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
