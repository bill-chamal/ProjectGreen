package com.example.projectgreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;

import java.util.NavigableMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnterScreen() {
        // Required empty public constructor
        System.out.println("Hola\n");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterScreen newInstance(String param1, String param2) {
        EnterScreen fragment = new EnterScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_screen, container, false);

        // Load material (worthy) values from Firestore
        MaterialType.inflateMatValues();

        YoYo.with(Techniques.SlideInDown).duration(1000).repeat(0)
                .playOn(view.findViewById(R.id.textView));

        YoYo.with(Techniques.SlideInDown).duration(5000).repeat(0)
                .playOn(view.findViewById(R.id.textView2));

        YoYo.with(Techniques.Wobble).delay(4000).duration(1000).repeat(2)
                .playOn(view.findViewById(R.id.btnSignin));


        view.findViewById(R.id.btnSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_enterScreen_to_userLoginScreen);

            }
        });

        view.findViewById(R.id.btnCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_enterScreen_to_createAccountScreen);

            }
        });

        return view;
    }

}