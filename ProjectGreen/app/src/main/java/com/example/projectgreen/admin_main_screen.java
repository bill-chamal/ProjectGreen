package com.example.projectgreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class admin_main_screen extends Fragment {

    private User user;
    private NavigationView naview;
    private DrawerLayout drawerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_main_screen, container, false);

        user = admin_main_screenArgs.fromBundle(getArguments()).getAdminData();

        // LEFT SLIDE MENU
        drawerLayout = view.findViewById(R.id.drawer_layout_admin);
        Toolbar toolbar = view.findViewById(R.id.toolbar_admin);
        naview = (NavigationView)view.findViewById(R.id.nav_view_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set username to the slide menu bar
        ((TextView)naview.getHeaderView(0).findViewById(R.id.lblSlideMenuName)).setText("Hi, " + user.getUserName());

        drawerLayout.openDrawer(GravityCompat.START);
        // Not working!!
        naview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home){
                    Toast.makeText(getActivity(), "Home clicked", Toast.LENGTH_LONG).show();
                } else if(item.getItemId() == R.id.nav_about){
                    Toast.makeText(getActivity(), "about  clicked", Toast.LENGTH_LONG).show();
                } else if (item.getItemId() == R.id.nav_future) {
                    Toast.makeText(getActivity(), "future clicked", Toast.LENGTH_LONG).show();
                } else if (item.getItemId() == R.id.nav_logout) {
                    Toast.makeText(getActivity(), "logout clicked", Toast.LENGTH_LONG).show();
                }
                drawerLayout.closeDrawer((GravityCompat.START));
                //drawerLayout.close();
                return true;
            }
        });

        // Slide between fragments
        replaceFragment(new AdminViewMaterialApprove());

        ((BottomNavigationView) view.findViewById(R.id.bottomUserNavView_admin)).setOnItemSelectedListener(item -> {
            // From file "bottom_user_menu.xml" id name
            if (item.getItemId() == R.id.adminRequestMenu)
                replaceFragment(new AdminViewMaterialApprove());
            else if (item.getItemId() == R.id.adminLeaderboardMenu)
                replaceFragment(new AdminViewLeaderboard());
            else if (item.getItemId() == R.id.adminTuneRewardsMenu) {
                
            }
            return true;
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