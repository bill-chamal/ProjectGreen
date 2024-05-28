package com.example.projectgreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateAccountScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountScreen newInstance(String param1, String param2) {
        CreateAccountScreen fragment = new CreateAccountScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // the shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // When initializing your Activity, check to see if the user is currently signed in:
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account_screen, container, false);

        view.findViewById(R.id.btnBack_to_EnterScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_createAccountScreen_to_enterScreen);
            }
        });

        view.findViewById(R.id.btnLoginAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_createAccountScreen_to_userLoginScreen);
            }
        });

        view.findViewById(R.id.btnCreateNewAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                String name = String.valueOf(((EditText) view.findViewById(R.id.txtbox_UserEmail)).getText());
                String email = String.valueOf(((EditText) view.findViewById(R.id.txtbox_email)).getText());
                String password = String.valueOf(((EditText) view.findViewById(R.id.txtbox_password)).getText());

                if (name.length() == 0 || email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getContext(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
                if (name.isEmpty()) {
                    showError(((EditText) view.findViewById(R.id.txtbox_UserEmail)), "User name can't be empty");
                    flag = false;
                }
                if (!email.contains("@") || email.contains(" ") || !email.contains(".")) {
                    showError(((EditText) view.findViewById(R.id.txtbox_email)), "Email is not valid");
                    flag = false;
                }
                if (password.length() < 6) {
                    showError(((EditText) view.findViewById(R.id.txtbox_password)), "Password must be 7 to 30 in length");
                    flag = false;
                }

                if (flag) {
                    // CREATE ACCOUNT
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getContext(), "Authentication Successfully. Account created:" + user.getEmail(), Toast.LENGTH_SHORT).show();

                                User user1 = new User(name, email, false, new ArrayList<>(), 0, 0, 0);
                                user1.sendUser();

                                // updateUI(userData); NEXT FRAGMENT
                                NavDirections action = CreateAccountScreenDirections.actionCreateAccountScreenToUserViewScreen(user1);
                                Navigation.findNavController(view).navigate(action);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                // updateUI(null);
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private void showError(EditText t, String e) {
        t.setError(e);
        t.requestFocus();
    }

}