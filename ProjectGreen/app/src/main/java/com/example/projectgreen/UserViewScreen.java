package com.example.projectgreen;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.projectgreen.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class UserViewScreen extends Fragment {
    ActivityMainBinding binding;
    public UserViewScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_view_screen, container, false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //getActivity().setContentView(binding.getRoot());

        // LEFT SLIDE MENU
        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // FAB BUTTON
        view.findViewById(R.id.userFabBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When fab button clicked show slide menu
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_user_material_selector_layout);

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                Spinner materialSpinner = dialog.findViewById(R.id.material_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(dialog.getContext(),
                        R.array.materials, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                materialSpinner.setAdapter(adapter);
            }
        });

        replaceFragment(new UserStatisticsFragment());

        ((BottomNavigationView) view.findViewById(R.id.bottomUserNavView)).setOnItemSelectedListener(item -> {
            // From file "bottom_user_menu.xml" id name
            if (item.getItemId() == R.id.points)
                replaceFragment(new UserStatisticsFragment());
             else if (item.getItemId() == R.id.reg)
                replaceFragment(new UserRegisterFragment());

            return true;
        });

        return view;
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}