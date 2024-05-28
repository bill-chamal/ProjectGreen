package com.example.projectgreen;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class admin_main_screen extends Fragment {

    private User user;
    private NavigationView naview;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<User> userList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_main_screen, container, false);


        user = admin_main_screenArgs.fromBundle(getArguments()).getAdminData();

        // LEFT SLIDE MENU
        drawerLayout = view.findViewById(R.id.drawer_layout_admin);
        Toolbar toolbar = view.findViewById(R.id.toolbar_admin);
        naview = (NavigationView) view.findViewById(R.id.nav_view_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(R.string.app_name);
        // Set username to the slide menu bar
        ((TextView) naview.getHeaderView(0).findViewById(R.id.lblSlideMenuName)).setText("Hi, " + user.getUserName());

        // The profile view overlapping the menus
        naview.bringToFront();
        drawerLayout.requestLayout();
        naview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_about) {
                    Dialog aboutDialog = new Dialog(getContext());
                    aboutDialog.setContentView(R.layout.fragment_about_popup);
                    aboutDialog.show();
                } else if (item.getItemId() == R.id.nav_logout) {
                    mAuth.signOut();
                    Intent gotoMainActivity = new Intent(getContext(), MainActivity.class);
                    gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gotoMainActivity);
                }
                drawerLayout.closeDrawer((GravityCompat.START));
                return true;
            }
        });

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

                    // Move between fragments
                    replaceFragment(new AdminViewMaterialApprove(userList));
                    Log.i("ADMIN", "Successfully load of users data! - AdminViewMaterialApprove.java");
                } else {
                    Toast.makeText(getContext(), "Error while transferring user data ", Toast.LENGTH_SHORT).show();
                    Log.e("ADMIN", "Error while transferring user data - AdminViewMaterialApprove.java");
                }
            }
        });

        bottomNavigationView = ((BottomNavigationView) view.findViewById(R.id.bottomUserNavView_admin));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // From file "bottom_user_menu.xml" id name
            if (item.getItemId() == R.id.adminRequestMenu) {
                db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        userList.clear();
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
                            // Move between fragments
                            replaceFragment(new AdminViewMaterialApprove(userList));
                            Log.i("ADMIN", "Successfully load of users data! - AdminViewMaterialApprove.java");
                        } else {
                            Toast.makeText(getContext(), "Error while transferring user data ", Toast.LENGTH_SHORT).show();
                            Log.e("ADMIN", "Error while transferring user data - AdminViewMaterialApprove.java");
                        }
                    }
                });
            } else if (item.getItemId() == R.id.adminLeaderboardMenu)
                replaceFragment(new AdminViewLeaderboard(this));
            else if (item.getItemId() == R.id.adminTuneRewardsMenu) {
                replaceFragment(new AdminTuneRewards(this));
            }
            return true;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Clear history stack and goto enter screen
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer((GravityCompat.START));
                else {
                    Intent gotoMainActivity = new Intent(getContext(), MainActivity.class);
                    gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gotoMainActivity);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_admin, fragment);
        fragmentTransaction.commit();
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}

