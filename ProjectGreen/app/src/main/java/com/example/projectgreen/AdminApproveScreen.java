package com.example.projectgreen;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.checkerframework.checker.units.qual.A;

public class AdminApproveScreen extends Fragment {

    private TextView txtUserName, txtUserEmail, txtReqTime, txtMat, txtMatPieces, txtMatValue;
    private Button btnApprove, btnReject;
    private User user;
    private Recycled rec;
    private AdminViewMaterialApprove adminViewMaterialApprove;
    private ImageView btnBack;
    public AdminApproveScreen(User user, Recycled recycled, AdminViewMaterialApprove adminViewMaterialApprove){
        this.user = user;
        this.rec = recycled;
        this.adminViewMaterialApprove = adminViewMaterialApprove;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_approve_screen, container, false);

        // get components from fragment
        txtUserName = view.findViewById(R.id.textUserName);
        txtUserEmail = view.findViewById(R.id.textUserEmail);
        txtReqTime = view.findViewById(R.id.textReqTime);
        txtMat = view.findViewById(R.id.textMatType);
        txtMatPieces = view.findViewById(R.id.textMatPiece);
        txtMatValue = view.findViewById(R.id.textMatTotalVule);
        btnApprove = view.findViewById(R.id.btnApprove);
        btnReject = view.findViewById(R.id.btnReject);
        btnBack = view.findViewById(R.id.btnMatApprScreenToBack);
        // Initialize components
        txtUserName.setText(user.getUserName());
        txtUserEmail.setText(user.getEmail());
        txtReqTime.setText(rec.getTimestamp().toDate().toString());
        txtMat.setText(rec.getMat().getMatName());
        txtMatPieces.setText(String.valueOf(rec.getPieces()) + " pieces");

        double totalMatValue = rec.getMat().getValue()*rec.getPieces();
        String formattedMatValue = String.format("%.2f", totalMatValue);
        txtMatValue.setText(rec.getMat().getValue() + "$ per piece. In total: " + formattedMatValue + "$");

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.approveRecycleRequest(rec);
                user.sendUser();
                if (user.getTotalPieceOfUnapprovedMaterial() == 0)
                    gotoAdminFragmentRmUser();
                else
                    replaceFragment(new AdminViewUserUnapprovedRecycles(user,adminViewMaterialApprove));
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rec.setApproved(Recycled.REJECTED);
                user.sendUser();
                if (user.getTotalPieceOfUnapprovedMaterial() == 0)
                    gotoAdminFragmentRmUser();
                else
                    replaceFragment(new AdminViewUserUnapprovedRecycles(user,adminViewMaterialApprove));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AdminViewUserUnapprovedRecycles(user,adminViewMaterialApprove));
            }
        });

        return view;
    }

    private void gotoAdminFragmentRmUser(){
        // User has not other requests
        // Remove it from the list and notify adapter
        adminViewMaterialApprove.removeUser(user);
        replaceFragment(adminViewMaterialApprove);
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_admin, fragment);
        fragmentTransaction.commit();
    }
}