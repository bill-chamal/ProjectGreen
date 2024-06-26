package com.example.projectgreen;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    // User fields
    private static final String FIELD_RECYCLE = "recycle";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_IS_ADMIN = "isAdmin";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_BALANCE = "balance";
    private static final String FIELD_POINTS = "points";
    private static final String FIELD_APPROVED_MATQ = "approved_material_quantity";
    private static final String FIELD_BONUS_VALUE = "bonus_value";
    private static final String FIELD_STOP_BONUS = "stop_bonus";
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
    private double balance;
    private int points;
    private double score;
    private int apprMatQ;
    public int bonusValue;
    private boolean stopBonus = false;
    private ArrayList<Recycled> recycledList;
    private transient FirebaseFirestore db = FirebaseFirestore.getInstance();
    private transient Timestamp t;


    public User(String name, String gmail, boolean isAdmin, @NonNull ArrayList<Recycled> re) {
        this.userName = name;
        this.email = gmail;
        this.isAdmin = isAdmin;
        this.recycledList = re;
    }

    public User(String name, String gmail, boolean isAdmin, @NonNull ArrayList<Recycled> re, double balance, int points, double score) {
        this.userName = name;
        this.email = gmail;
        this.isAdmin = isAdmin;
        this.recycledList = re;
        this.balance = (int) (balance * 100 + 0.5) / 100.0;
        this.points = points;
        this.score = score;
    }

    public User() {
        // Empty constructor
    }

    public void populate(@NonNull Map<String, Object> m) {
        // Fetch data and check if new fields don't exists in the database
        if (!m.containsKey(FIELD_RECYCLE))
            m.put(FIELD_RECYCLE, new HashMap<String, Object>());

        if (!m.containsKey(FIELD_APPROVED_MATQ))
            m.put(FIELD_APPROVED_MATQ, 0);

        if (!m.containsKey(FIELD_BONUS_VALUE))
            m.put(FIELD_BONUS_VALUE, 0);

        if (!m.containsKey(FIELD_BALANCE)) {
            m.put(FIELD_BALANCE, 0);
            m.put(FIELD_POINTS, 0);
            m.put(FIELD_SCORE, 0);
        }

        if (!m.containsKey(FIELD_STOP_BONUS)){
            if(points >= 100)
                m.put(FIELD_STOP_BONUS, true);
            else
                m.put(FIELD_STOP_BONUS, false);
        }

        convertMapToUser(m);
    }

    public void sendUser() {
        // send user data to cloud!!!
        // New user => user creation
        db.collection("user").document(this.email).set(convertUserToMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User: " + userName + " data uploaded successful. Email: " + email);
                } else {
                    Log.d(TAG, "Error while uploading User: " + email + " data");
                }
            }
        });
    }
    // When new mat is registered by user, update, add rec, upload and notify adapter
    public void refreshUserAddRec(Recycled rec, UserRegisterFragment userRegisterFragment){
        db.collection("user").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> doc = null;

                    for (DocumentSnapshot document : task.getResult())
                        doc = document.getData();

                    if (doc != null)
                        populate(doc);
                    // user fetched
                    addRecycle(rec);
                    //  upload user data
                    sendUser();

                    // Check if the user upload finished in time and there is not difference
                    // between adapter list and user own list, in case user changed fragment
                    // and and the adapter has the old data
                    // If user didn't switch fragment it's Context will be empty
                    if (userRegisterFragment.getContext() != null)
                        userRegisterFragment.keepListUpdated(rec);

                    Log.i("USER", "User document downloaded: " + doc.get(FIELD_EMAIL));

                } else
                    Log.e("USER", "User document failed to download: " + email);
            }
        });
    }

    // Refresh the User stat screen with updated data from cloud
    public void refresh(UserViewScreen userViewScreen) {
        db.collection("user").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> doc = null;

                    for (DocumentSnapshot document : task.getResult())
                        doc = document.getData();

                    if (doc != null)
                        populate(doc);

                    userViewScreen.replaceStatFragmentAndNotify();

                    Log.i("USER", "User document downloaded: " + doc.get(FIELD_EMAIL));

                } else
                    Log.e("USER", "User document failed to download: " + email);
            }
        });
    }

    private Map<String, Object> convertUserToMap() {
        // Convert user to readable type for Google Firestore
        Map<String, Object> recycle_list = new HashMap<String, Object>();
        // First the recycle list
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
        // Then use the recycle list and add it as field to user Map
        Map<String, Object> usermap = new HashMap<String, Object>();
        usermap.put(FIELD_NAME, userName);
        usermap.put(FIELD_EMAIL, email);
        usermap.put(FIELD_BALANCE, balance);
        usermap.put(FIELD_SCORE, score);
        usermap.put(FIELD_POINTS, points);
        usermap.put(FIELD_RECYCLE, recycle_list);
        usermap.put(FIELD_IS_ADMIN, isAdmin);
        usermap.put(FIELD_APPROVED_MATQ, apprMatQ);
        usermap.put(FIELD_BONUS_VALUE, bonusValue);
        usermap.put(FIELD_STOP_BONUS, stopBonus);

        return usermap;
    }

    private void convertMapToUser(Map<String, Object> m) {
        // Download data type Map<> from Firestore and fetch to its class variables
        this.userName = (String) m.get(FIELD_NAME);
        this.email = (String) m.get(FIELD_EMAIL);
        this.isAdmin = (boolean) m.get(FIELD_IS_ADMIN);
        this.balance = (double) m.get(FIELD_BALANCE);
        this.score = (double) m.get(FIELD_SCORE);
        this.points = Integer.parseInt(m.get(FIELD_POINTS).toString());
        this.apprMatQ = Integer.parseInt(m.get(FIELD_APPROVED_MATQ).toString());
        this.bonusValue = Integer.parseInt(m.get(FIELD_BONUS_VALUE).toString());
        this.stopBonus = (boolean) m.get(FIELD_STOP_BONUS);

        recycledList = new ArrayList<>();

        // FIELD_RECYCLE is Map that will be converted to ArrayList
        Map<String, Object> reclist = (Map<String, Object>) m.get(FIELD_RECYCLE);

        if (!reclist.isEmpty())
            for (Map.Entry<String, Object> entry : reclist.entrySet()) {

                Map<String, Object> rec = (Map<String, Object>) entry.getValue();

                Map<String, Object> mat = (Map<String, Object>) rec.get(FIELD_MATERIAL);

                Material material = new Material((String) mat.get(FIELD_MAT_NAME), (double) mat.get(FIELD_MAT_VALUE));

                int p = Integer.parseInt(rec.get(FIELD_PIECES).toString());
                t = (Timestamp) rec.get(FIELD_TIMESTAMP);
                int aprv = Integer.parseInt(rec.get(FIELD_APPROVED).toString());

                Recycled recycled = new Recycled(material, p, t, aprv);

                recycledList.add(recycled);
            }
    }

    public void addRecycle(Recycled recycled) {
        recycledList.add(recycled);
    }

    public int getTotalPieceOfMaterial(String mat_name) {
        int sum = 0;
        for (Recycled r : recycledList) {
            if (r.getMat().getMatName().equals(mat_name) && r.isApproved() == Recycled.APPROVED)
                sum += r.getPieces();
        }
        return sum;
    }

    public int getValueOfTotalPieceOfMaterial(String mat_name) {
        int sumvalue = 0;
        for (Recycled r : recycledList) {
            if (r.getMat().getMatName().equals(mat_name) && r.isApproved() == Recycled.APPROVED)
                sumvalue += r.getPieces() * r.getMat().getValue();
        }
        return sumvalue;
    }

    public int getTotalPieceOfUnapprovedMaterial() {
        int sum = 0;
        for (Recycled r : recycledList) {
            if (r.isApproved() == Recycled.NOT_APPROVED)
                sum += r.getPieces();
        }
        return sum;
    }

    public int countOfUnapprovedMaterial() {
        int count = 0;
        for (Recycled r : recycledList) {
            if (r.isApproved() == Recycled.NOT_APPROVED)
                count++;
        }
        return count;
    }

    public ArrayList<Recycled> getUnapprovedMat() {
        ArrayList<Recycled> recUn = new ArrayList<>();
        for (Recycled r :
                recycledList) {
            if (r.isApproved() == Recycled.NOT_APPROVED)
                recUn.add(r);
        }
        return recUn;
    }

    public void approveRecycleRequest(Recycled r) {
        r.setApproved(Recycled.APPROVED);
        balance += r.getPieces() * r.getMat().getValue();

        points += 1; //basic point reward for the recycle request
        apprMatQ += r.getPieces();

        switch (r.getMat().getMatName()) { //custom point reward based on the quantity and type of the materials recycled
            case MaterialType.matn1:
                points += Math.floor(r.getPieces() * 0.5); //Plastic
                break;
            case MaterialType.matn2:
                points += Math.floor(r.getPieces() * 0.3); //Paper
                break;
            case MaterialType.matn3:
                points += Math.floor(r.getPieces() * 0.6); //Glass
                break;
            case MaterialType.matn4:
                points += Math.floor(r.getPieces() * 0.8); //Metal
                break;
            default:
                Log.w("User.java approveRecycleRequest", "Not material type found to give points");
        }

        if (!stopBonus) { //Give bonus reward only if the cash in threshold hasn't been reached.
            //bonus will get updated for every approval before 100 points (the point value where the cash in progress bar is full)
            bonusValue = (int) Math.ceil(apprMatQ / MaterialType.getBonus());

            if (points >= 100)
                stopBonus = true; //in the last approval that will fill the progress bar, turn the controller boolean stopBonus to true
        }
    }

    public void setNewScore(double newScore) {
        // User clicks CASH IN and the progress score is reset
        score += newScore;
        balance = 0;
        points = 0;
        apprMatQ = 0;
        bonusValue = 0;
        // Upload user to database
        sendUser();
    }

    // = = =    GETTERS AND SETTERS     = = = //
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

    public double getBalance() {
        return balance;
    }

    public int getPoints() {
        return points;
    }

    public double getTotalCashback() {
        return score;
    }

    public int getApprMatQ() {
        return apprMatQ;
    }

    public int getBonusValue() {
        return bonusValue;
    }
}

class UserComparator implements Comparator<User>, Serializable {
    // Compare and sort users by cash
    @Override
    public int compare(User o1, User o2) {
        if (o1.getTotalCashback() == o2.getTotalCashback())
            return 0;
        else if (o1.getTotalCashback() > o2.getTotalCashback())
            return 1;
        else
            return -1;
    }
}
