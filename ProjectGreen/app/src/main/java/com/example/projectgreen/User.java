package com.example.projectgreen;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private boolean isAdmin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference alovelaceDocumentRef = db.collection("users").document("alovelace");
    private Map<String, Object> usermap ;

    public User (String name, String gmail, boolean isAdmin){
        this.name = name;
        this.email = gmail;
        this.isAdmin = isAdmin;

        // Set name fields
        usermap = new HashMap<>();
        usermap.put("name", name);
        usermap.put("isAdmin", isAdmin);
        usermap.put("email", email);
    }

    public User (Map<String, Object> m){
        populate(m);
    }

    public User (){

    }

    public void populate(@NonNull Map<String, Object> m){
        usermap = m;
        this.name = (String)m.get("name");
        this.email = (String)m.get("email");
        this.isAdmin = (boolean)m.get("isAdmin") ;
    }
    public void sendUser(){
        // send him to cloud!!!
        db.collection("user").document(email).set(usermap);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Map<String, Object> getUsermap() {
        return usermap;
    }

    public void setUsermap(Map<String, Object> usermap) {
        this.usermap = usermap;
    }
}
