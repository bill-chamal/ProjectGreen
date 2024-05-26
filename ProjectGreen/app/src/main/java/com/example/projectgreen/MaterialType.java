package com.example.projectgreen;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MaterialType implements Serializable {
    // class of predefined materials
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String matn1 = "plastic";
    public static final String matn2 = "paper" ;
    public static final String matn3 = "glass" ;
    public static final String matn4 = "metal" ;

    public static final double MATV1 = 0.04;
    public static final double MATV2 = 0.03;
    public static final double MATV3 = 0.06;
    public static final double MATV4 = 0.08;
    public static final int BONUS = 30;

    private static Material mat1 = new Material(matn1, MATV1);
    private static Material mat2 = new Material(matn2, MATV2);
    private static Material mat3 = new Material(matn3, MATV3);
    private static Material mat4 = new Material(matn4, MATV4);

    private static int bonusLimit = 30;

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

    public static void inflateMatValues(){
        downloadMatValue(mat1);
        downloadMatValue(mat2);
        downloadMatValue(mat3);
        downloadMatValue(mat4);
        downloadBonusLimit();
    }

    private static void downloadMatValue(Material m){
        db.collection("material").document(m.getMatName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    String val = ds.get("value").toString();
                    m.setValue(Double.valueOf(val));
                    Log.i("MTVALUE", "Material " + m.getMatName() + " loaded from Firestore with value " + String.valueOf(m.getValue()));
                }
                else
                    Log.e("MTVALUE", "Could not load Material " + m.getMatName() + " from Firestore");
            }
        });
    }

    public static void uploadMatValue(Material m){
        Map<String, Object> hash = new HashMap<String, Object>();
        hash.put("value", m.getValue());

        if (m.getMatName().equals(matn1))
            mat1 = m;
        else if (m.getMatName().equals(matn2))
            mat2 = m;
        else if (m.getMatName().equals(matn3))
            mat3 = m;
        else
            mat4 = m;

        db.collection("material").document(m.getMatName()).set(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.i("MTVALUE", "Material " + m.getMatName() + " uploaded to Firestore with value " + String.valueOf(m.getValue()));
                else
                    Log.e("MTVALUE", "Could not upload Material " + m.getMatName() + " from Firestore with value " + String.valueOf(m.getValue()));
            }
        });
    }

    private static void downloadBonusLimit(){
        db.collection("bonus_quantity_limit").document("bonus").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    String val = ds.get("bonus").toString();
                    bonusLimit = Integer.parseInt(val);
                    Log.i("MTVALUE", "Bonus limit loaded from Firestore with value " + bonusLimit);
                }
                else
                    Log.e("MTVALUE", "Could not load Bonus limit from Firestore");
            }
        });
    }

    public static void uploadBonusLimit(int newBonus){
        Map<String, Object> hash = new HashMap<String, Object>();
        bonusLimit = newBonus;
        hash.put("bonus", bonusLimit);
        db.collection("bonus_quantity_limit").document("bonus").set(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.i("MTVALUE", "Bonus limit uploaded to Firestore with value " + bonusLimit);
                else
                    Log.e("MTVALUE", "Could not upload Bonus limit to Firestore");
            }
        });
    }

    public static int getBonus()
    {
        return bonusLimit;
    }
}
