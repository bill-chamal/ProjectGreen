package com.example.projectgreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserLoginScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLoginScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private EditText txtUserEmail, txtUserPasswd;
    private Button btnSignIn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserLoginScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserLoginScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static UserLoginScreen newInstance(String param1, String param2) {
        UserLoginScreen fragment = new UserLoginScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

     // When initializing your Activity, check to see if the user is currently signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(getContext(), "Successfully auto sign in " + currentUser.getDisplayName() + ", email:" + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            //reload();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_login_screen, container, false);

        txtUserEmail = view.findViewById(R.id.txtbox_UserEmail);
        txtUserPasswd = view.findViewById(R.id.txtbox_password);
        btnSignIn = view.findViewById(R.id.btnCreateNewAccount);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(txtUserEmail.getText());
                String password = String.valueOf(txtUserPasswd.getText());

                if (email.length() == 0 || password.length() == 0)
                    Toast.makeText(getContext(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                else {
                    // doc https://firebase.google.com/docs/firestore/query-data/queries#java
                    // Sign in method - when completed, do the followings
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        User userm = new User();
                                        // Search the user from Firestore db to find his name and admin rights
                                        Task<QuerySnapshot> taskk = db.collection("user").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        Map<String, Object> doc = null;
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                doc = document.getData();
                                                            }
                                                            userm.populate(doc);
                                                        } else {
                                                            Toast.makeText(getContext(), "Error reading documents from the server.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        Toast.makeText(getContext(), "Welcome " + userm.getName() + ", email:" + user.getEmail(), Toast.LENGTH_SHORT).show();

                                        // NEXT FRAGMENT
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });


        view.findViewById(R.id.btnBack_to_EnterScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_userLoginScreen_to_enterScreen);
            }
        });

        view.findViewById(R.id.btnLoginAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_userLoginScreen_to_createAccountScreen);

            }
        });

        return view;
    }


}