package com.example.projectgreen;

public class MaterialType {
    private static final Material mat1 = new Material("plastic", 1.04);
    private static final Material mat2 = new Material("paper", 1.00);
    private static final Material mat3 = new Material("glass", 1.08);
    private static final Material mat4 = new Material("metal", 1.15);

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
