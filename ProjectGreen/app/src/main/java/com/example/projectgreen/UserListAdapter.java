package com.example.projectgreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    public UserListAdapter(@NonNull Context context, ArrayList<User> userArrayList) {
        super(context, R.layout.list_user_item ,userArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        User user = getItem(position);
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_user_item, parent, false);

        ImageView listImage           = view.findViewById(R.id.listImage);
        TextView  txtUserName         = view.findViewById(R.id.listMatName);
        TextView  txtTotalUnapPieces  = view.findViewById(R.id.listUserName);

        // IMAGE SET
        listImage.setImageResource(R.drawable.ic_person);

        txtUserName.setText(user.getUserName());
        txtTotalUnapPieces.setText(String.valueOf(user.countOfUnapprovedMaterial()) + " requests");
        return view;
    }
}
