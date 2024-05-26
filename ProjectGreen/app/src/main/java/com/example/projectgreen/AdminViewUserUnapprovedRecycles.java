package com.example.projectgreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminViewUserUnapprovedRecycles extends Fragment {

    private User user;
    private RecycleListAdapter recycleListAdapter;
    private ArrayList<Recycled> recycledArrayList = new ArrayList<>();
    private ListView listView;
    private TextView txtviewUserName;
    private ImageView btnBack;
    AdminViewMaterialApprove adminViewMaterialApprove;
    public AdminViewUserUnapprovedRecycles(User user, AdminViewMaterialApprove adminViewMaterialApprove){
        this.user = user;
        this.adminViewMaterialApprove = adminViewMaterialApprove;
    }
//    public AdminViewUserUnapprovedRecycles(User user){
//        this.user = user;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_user_unapproved_recycles, container, false);

        for (Recycled r : user.getRecycledList()) {
            if (r.isApproved() == Recycled.NOT_APPROVED)
                recycledArrayList.add(r);
        }

        // Sort the list of Recycled objects by timestamp
        Collections.sort(recycledArrayList, (r1, r2) -> {
            return r2.getTimestamp().compareTo(r1.getTimestamp()); // Descending order
        });

        // Create an adapter with the sorted list
        recycleListAdapter = new RecycleListAdapter(getContext(), recycledArrayList);

        listView = view.findViewById(R.id.listUnapprovedMaterialsView);
        listView.setAdapter(recycleListAdapter);
        listView.setClickable(true);

        txtviewUserName = view.findViewById(R.id.txtViewUserName);
        txtviewUserName.setText(user.getUserName());
        btnBack = view.findViewById(R.id.btnViewAdminBackToUsersList);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    replaceFragment(adminViewMaterialApprove);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                replaceFragment(new AdminApproveScreen(user, recycledArrayList.get(i), adminViewMaterialApprove));
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