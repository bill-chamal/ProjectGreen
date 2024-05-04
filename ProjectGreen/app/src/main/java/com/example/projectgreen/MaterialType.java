package com.example.projectgreen;

public class MaterialType {

    public static final String matn1 = "plastic";
    public static final String matn2 = "paper" ;
    public static final String matn3 = "glass" ;
    public static final String matn4 = "metal" ;

    private static final Material mat1 = new Material(matn1, 0.45);
    private static final Material mat2 = new Material(matn2, 0.05);
    private static final Material mat3 = new Material(matn3, 1.10);
    private static final Material mat4 = new Material(matn4, 1.35);

    // class of predefined materials

    public static Material PLASTIC() {
        return mat1;
    }

    public static Material PAPER() {
        return mat2;
    }

    public static Material GLASS() {
        return mat3;
    }

    public static Material METAL() {
        return mat4;
    }
}
