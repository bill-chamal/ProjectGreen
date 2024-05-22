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

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderboardListAdapter extends ArrayAdapter<User> implements Serializable {
    public LeaderboardListAdapter(@NonNull Context context, ArrayList<User> userArrayList) {
        super(context, R.layout.list_user_item , userArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        User user = getItem(position);
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_user_item, parent, false);

        ImageView listImage           = view.findViewById(R.id.listImage);
        TextView txtUserName         = view.findViewById(R.id.listMatName);
        TextView  txtScore  = view.findViewById(R.id.listUserName);

        // IMAGE SET
        listImage.setImageResource(R.drawable.ic_rewarded);

        txtUserName.setText(user.getUserName());
        txtScore.setText(String.format("%.2f", user.getTotalCashback()) + " $");
        txtScore.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        return view;
    }
}
