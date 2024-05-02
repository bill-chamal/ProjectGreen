
package com.example.projectgreen;

import static android.content.ContentValues.TAG;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    // User fields
    private static final String FIELD_RECYCLE = "recycle";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_IS_ADMIN = "isAdmin";
    private static final String FIELD_EMAIL = "email";
    // Recycle fields
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_PIECES = "pieces";
    private static final String FIELD_MATERIAL = "material";
    private static final String FIELD_APPROVED = "approved";
    // Material fields
    private static final String FIELD_MAT_NAME = "material_name";
    private static final String FIELD_MAT_VALUE = "value";

    private String userName;
    private boolean isAdmin;
    private String email;
    private ArrayList<Recycled> recycledList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public User (String name, String gmail, boolean isAdmin, @NonNull ArrayList<Recycled> re){
        this.userName = name;
        this.email = gmail;
        this.isAdmin = isAdmin;
        this.recycledList = re;

    }

//    public User (Map<String, Object> m){
//        populate(m);
//    }

    public User (){
        // Empty constructor
    }

    public void populate(@NonNull Map<String, Object> m){
        if (!m.containsKey(FIELD_RECYCLE)){
            m.put(FIELD_RECYCLE, new HashMap<String, Object>());
        }
        convertMapToUser(m);
    }

    public void sendUser(){
        // send him to cloud!!!
        // New user => user creation
        db.collection("user").document( this.email ).set( convertUserToMap() ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User: " + userName + " data uploaded successful. Email: " + email);
                }
                else {
                    Log.d(TAG, "Error while uploading User: " + email + " data");
                }
            }
        });
    }

    private Map<String, Object> convertUserToMap(){

        Map<String, Object> recycle_list = new HashMap<String, Object>();

        for (Recycled r : recycledList) {
            // Get material type
            Map<String, Object> mat = new HashMap<String, Object>();
            mat.put(FIELD_MAT_NAME, r.getMat().getMatName());
            mat.put(FIELD_MAT_VALUE, r.getMat().getValue());
            // Get recycle information
            Map<String, Object> recycle = new HashMap<String, Object>();
            recycle.put(FIELD_PIECES, r.getPieces());
            recycle.put(FIELD_APPROVED, r.isApproved());
            recycle.put(FIELD_TIMESTAMP, r.getTimestamp());
            recycle.put(FIELD_MATERIAL, mat);
            // Put it to array
            recycle_list.put(String.valueOf(r.getTimestamp().hashCode()), recycle);
        }

        Map<String, Object> usermap = new HashMap<String, Object>();
        usermap.put(FIELD_NAME, userName);
        usermap.put(FIELD_EMAIL, email);
        usermap.put(FIELD_RECYCLE, recycle_list );
        usermap.put(FIELD_IS_ADMIN, isAdmin);

        return usermap;
    }

    private void convertMapToUser(Map<String, Object> m) {
        this.userName = (String) m.get(FIELD_NAME);
        this.email = (String) m.get(FIELD_EMAIL);
        this.isAdmin = (boolean) m.get(FIELD_IS_ADMIN);

        recycledList = new ArrayList<>();

        // FIELD_RECYCLE is Map that will be converted to ArrayList
        Map<String, Object> reclist = (Map<String, Object>) m.get(FIELD_RECYCLE);

        if (!reclist.isEmpty())
            for (Map.Entry<String, Object> entry : reclist.entrySet()) {

                Map<String, Object> rec = (Map<String, Object>) entry.getValue();

                Map<String, Object> mat = (Map<String, Object>) rec.get(FIELD_MATERIAL);

                Material material = new Material((String) mat.get(FIELD_MAT_NAME), (double) mat.get(FIELD_MAT_VALUE));

                int p = Integer.parseInt(rec.get(FIELD_PIECES).toString());
                Timestamp t = (Timestamp) rec.get(FIELD_TIMESTAMP);
                boolean aprv = (boolean) rec.get(FIELD_APPROVED);

                Recycled recycled = new Recycled(material, p, t , aprv );

                recycledList.add(recycled);
            }
    }

    public int getTotalPieceOfMaterial(String mat_name){
        int sum = 0;
        for (Recycled r : recycledList){
            if (r.getMat().getMatName().equals(mat_name))
                sum += r.getPieces();
        }
        return sum;
    }

    // GETTERS AND SETTERS //
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Recycled> getRecycledList() {
        return recycledList;
    }

    public void setRecycledList(ArrayList<Recycled> recycledList) {
        this.recycledList = recycledList;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }
}
