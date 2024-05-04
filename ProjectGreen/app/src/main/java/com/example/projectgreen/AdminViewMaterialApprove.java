package com.example.projectgreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AdminViewMaterialApprove extends Fragment {

    public AdminViewMaterialApprove(){}

    ListView listView;
    ListAdapter listAdapter;
    ArrayList<Recycled> recycledArrayList = new ArrayList<>();
    Recycled recycled;
    UserListAdapter userListAdapter;
    ArrayList<User> userList = new ArrayList<User>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_material_approve, container, false);

        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> doc = null;
                    // Foreach document collection get every user
                    for (DocumentSnapshot document : task.getResult()) {
                        doc = document.getData();
                        User user1 = new User();
                        user1.populate(doc);
                        if (!user1.isAdmin()) {
                            if (user1.countOfUnapprovedMaterial() > 0)
                                userList.add(user1);
                        }
                    }

                    userListAdapter = new UserListAdapter(getContext(), userList);
                    listView = view.findViewById(R.id.listUsersView);
                    listView.setAdapter(userListAdapter);
                    listView.setClickable(true);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                            replaceFragment(new AdminViewUserUnapprovedRecycles(userList.get(i)));
                        }
                    });

                    Log.i("ADMIN", "Successfully load of users data! - AdminViewMaterialApprove.java");
                } else {
                    Toast.makeText(getContext(), "Error while transferring user data ", Toast.LENGTH_SHORT).show();
                    Log.e("ADMIN", "Error while transferring user data - AdminViewMaterialApprove.java");
                }
            }
        });
        return view;
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_admin, fragment);
        fragmentTransaction.commit();
    }
}