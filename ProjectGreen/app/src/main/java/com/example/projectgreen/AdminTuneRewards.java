package com.example.projectgreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AdminTuneRewards extends Fragment {

    private EditText editPlastic, editPaper, editGlass, editMetal, editBonus;
    private Button btnSave, btnRestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_tune_rewards, container, false);

        editPlastic = view.findViewById(R.id.editPlasticValue);
        editPaper = view.findViewById(R.id.editPaperValue);
        editGlass = view.findViewById(R.id.editGlassValue);
        editMetal = view.findViewById(R.id.editMetalValue);
        editBonus = view.findViewById(R.id.editBonusQuantity);
        btnSave = view.findViewById(R.id.btnSaveAndSetTuneFields);
        btnRestore = view.findViewById(R.id.btnRestoreDefaultTuneValues);

        editPlastic.setText(String.valueOf(MaterialType.PLASTIC().getValue()));
        editPaper.setText(String.valueOf(MaterialType.PAPER().getValue()));
        editGlass.setText(String.valueOf(MaterialType.GLASS().getValue()));
        editMetal.setText(String.valueOf(MaterialType.METAL().getValue()));
        editBonus.setText(String.valueOf(MaterialType.getBonus()));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialType.uploadMatValue(new Material(MaterialType.matn1, Double.parseDouble(editPlastic.getText().toString())));
                MaterialType.uploadMatValue(new Material(MaterialType.matn2, Double.parseDouble(editPaper.getText().toString())));
                MaterialType.uploadMatValue(new Material(MaterialType.matn3, Double.parseDouble(editGlass.getText().toString())));
                MaterialType.uploadMatValue(new Material(MaterialType.matn4, Double.parseDouble(editMetal.getText().toString())));

                MaterialType.uploadBonusLimit(Integer.parseInt(editBonus.getText().toString()));
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPlastic.setText (String.valueOf(MaterialType.MATV1));
                editPaper.setText   (String.valueOf(MaterialType.MATV2));
                editGlass.setText   (String.valueOf(MaterialType.MATV3));
                editMetal.setText   (String.valueOf(MaterialType.MATV4));
                editBonus.setText   (String.valueOf(MaterialType.BONUS));
            }
        });

        return view;
    }
}