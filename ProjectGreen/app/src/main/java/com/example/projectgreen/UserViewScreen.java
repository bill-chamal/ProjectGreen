package com.example.projectgreen;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;


public class UserViewScreen extends Fragment {
    public UserViewScreen() {
        // Required empty public constructor
    }

    private User user;
    private NavigationView naview;
    private DrawerLayout drawerLayout;
    private Dialog dialog;
    private Button btnRequest;
    private EditText editTextPieces;
    private Spinner spinnerMat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_view_screen, container, false);

        user = UserViewScreenArgs.fromBundle(getArguments()).getUserData();

        // LEFT SLIDE MENU
        drawerLayout = view.findViewById(R.id.drawer_layout);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        naview = (NavigationView)view.findViewById(R.id.nav_view);

        // Set username to the slide menu bar
        ((TextView)naview.getHeaderView(0).findViewById(R.id.lblSlideMenuName)).setText("Hi, " + user.getUserName());

        // Not working!!
        naview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("TAG", "Menu onNavigationItemSelected clicked!");
                if(item.getItemId() == R.id.nav_home){
                    Toast.makeText(getActivity(), "Home clicked", Toast.LENGTH_LONG).show();
                }
                if(item.getItemId() == R.id.nav_about){
                    Toast.makeText(getActivity(), "about  clicked", Toast.LENGTH_LONG).show();

                }
                if (item.getItemId() == R.id.nav_future) {
                    Toast.makeText(getActivity(), "futu clicked", Toast.LENGTH_LONG).show();

                }
                if (item.getItemId() == R.id.nav_logout) {
                    Toast.makeText(getActivity(), "logout clicked", Toast.LENGTH_LONG).show();
                }
                drawerLayout.closeDrawer((GravityCompat.START));
                //drawerLayout.close();
                return true;
            }
        });

        // FAB BUTTON // REGISTER NEW RECYCLE MATERIAL DOCUMENT FIELD
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_user_material_selector_layout);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Spinner materialSpinner = dialog.findViewById(R.id.material_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(dialog.getContext(),
                R.array.materials, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(adapter);
        view.findViewById(R.id.userFabBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When fab button clicked show slide menu
                dialog.show();
            }
        });

        editTextPieces = dialog.findViewById(R.id.editTextNumberSigned);
        spinnerMat = dialog.findViewById(R.id.material_spinner);
        btnRequest = dialog.findViewById(R.id.btnRequestNewRecycle);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matName = spinnerMat.getSelectedItem().toString();
                Material mat1;

                if (matName.equalsIgnoreCase(MaterialType.matn1))
                    mat1 = MaterialType.PLASTIC();
                else if (matName.equalsIgnoreCase(MaterialType.matn2))
                    mat1 = MaterialType.PAPER();
                else if (matName.equalsIgnoreCase(MaterialType.matn3))
                    mat1 = MaterialType.GLASS();
                else // Aluminium
                    mat1 = MaterialType.METAL();

                Recycled rec = new Recycled(mat1, Integer.parseInt(editTextPieces.getText().toString()), Timestamp.now(), Recycled.NOT_APPROVED);
                user.addRecycle(rec);
                user.sendUser();
                Toast.makeText(getContext(), "Material " + mat1.getMatName() +" request completed", Toast.LENGTH_SHORT).show();
                Log.i("NEW_MAT_REQ", "New material request from " + user.getEmail() + ", mat:" + mat1.toString());
            }
        });

        replaceFragment(new UserStatisticsFragment(user));

        ((BottomNavigationView) view.findViewById(R.id.bottomUserNavView)).setOnItemSelectedListener(item -> {
            // From file "bottom_user_menu.xml" id name
            if (item.getItemId() == R.id.points)
                replaceFragment(new UserStatisticsFragment(user));
             else if (item.getItemId() == R.id.reg)
                replaceFragment(new UserRegisterFragment(user));

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