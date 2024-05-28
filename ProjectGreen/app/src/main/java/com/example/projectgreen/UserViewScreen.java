package com.example.projectgreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;


public class UserViewScreen extends Fragment {
    private User user;
    private NavigationView naview;
    private DrawerLayout drawerLayout;
    private Dialog dialog;
    private Button btnRequest;
    private EditText editTextPieces;
    private Spinner spinnerMat;
    private UserStatisticsFragment userStatisticsFragment;
    private UserRegisterFragment userRegisterFragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private BottomNavigationView bottomNavigationView;

    public UserViewScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_view_screen, container, false);

        user = UserViewScreenArgs.fromBundle(getArguments()).getUserData();

        userStatisticsFragment = new UserStatisticsFragment(user);

        // LEFT SLIDE MENU
        drawerLayout = view.findViewById(R.id.drawer_layout);
        userRegisterFragment = new UserRegisterFragment(user, this);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        naview = (NavigationView) view.findViewById(R.id.nav_view);
        toolbar.setTitle(R.string.app_name);
        // Set username to the slide menu bar
        ((TextView) naview.getHeaderView(0).findViewById(R.id.lblSlideMenuName)).setText("Hi, " + user.getUserName());
        ((TextView) naview.getHeaderView(0).findViewById(R.id.lblSlideMenu)).setText(user.getEmail());

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
                }
                if (item.getItemId() == R.id.nav_logout) {
                    mAuth.signOut();
                    Intent gotoMainActivity = new Intent(getContext(), MainActivity.class);
                    gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gotoMainActivity);
                }
                drawerLayout.closeDrawer((GravityCompat.START));
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
                // Find the selected material
                if (matName.equalsIgnoreCase(MaterialType.matn1))
                    mat1 = new Material(MaterialType.matn1, MaterialType.PLASTIC().getValue());
                else if (matName.equalsIgnoreCase(MaterialType.matn2))
                    mat1 = new Material(MaterialType.matn2, MaterialType.PAPER().getValue());
                else if (matName.equalsIgnoreCase(MaterialType.matn3))
                    mat1 = new Material(MaterialType.matn3, MaterialType.GLASS().getValue());
                else // Aluminium
                    mat1 = new Material(MaterialType.matn4, MaterialType.METAL().getValue());

                // Get the input from editTextPieces
                String input = editTextPieces.getText().toString();

                // Check if the input is not empty
                if (!input.isEmpty()) {
                    // Regular expression to match only positive integers
                    String regex = "^[1-9]\\d*$";

                    // Check if the input matches the regular expression
                    if (input.matches(regex)) {
                        // Convert the input to an integer
                        int quantity = Integer.parseInt(input);

                        // Check if the quantity is greater than zero
                        if (quantity > 0) {
                            // Create and add the Recycled object
                            Recycled rec = new Recycled(mat1, quantity, Timestamp.now(), Recycled.NOT_APPROVED);
                            // must refresh the user before add new data to avoid overwrite
                            user.refreshUserAddRec(rec, userRegisterFragment);
                            dialog.dismiss();
                            // If user didn't open this fragment then has not context for the list-adapter
                            if (userRegisterFragment.getContext() != null)
                                userRegisterFragment.addNewRec(rec);

                            Toast.makeText(getContext(), "Material " + mat1.getMatName() + " request completed", Toast.LENGTH_SHORT).show();
                            Log.i("NEW_MAT_REQ", "New material request from " + user.getEmail() + ", mat:" + mat1.toString());
                        } else {
                            // Show error message for zero or negative quantity
                            Toast.makeText(getContext(), "Enter a non-zero positive number for quantity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Show error message for non-numeric input
                        Toast.makeText(getContext(), "Enter a valid positive number for quantity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show error message for empty input
                    Toast.makeText(getContext(), "Quantity field cannot be empty", Toast.LENGTH_SHORT).show();
                    Log.i("NaN", "Tried to convert empty string to number. UserViewScreen - Material Register");
                }
            }
        });
        // Set default fragment
        replaceFragment(new UserStatisticsFragment(user));
        // Navigate between fragments Stats<->Register
        bottomNavigationView =  view.findViewById(R.id.bottomUserNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // From file "bottom_user_menu.xml" id name
            if (item.getItemId() == R.id.points) {
                // Keep the user stat updated and sych from cloud
                user.refresh(this);
            } else if (item.getItemId() == R.id.reg)
                replaceFragment(userRegisterFragment);

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

    // Keep the user data updated
    public void replaceStatFragmentAndNotify() {
        // In case of any changes, notify the adapter
        if (userRegisterFragment.getContext() != null)
            userRegisterFragment.notifyAdapter();
        replaceFragment(userStatisticsFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}