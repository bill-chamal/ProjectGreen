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

public class RecycleListAdapter extends ArrayAdapter<Recycled> {

    public RecycleListAdapter(@NonNull Context context, ArrayList<Recycled> recycledArrayList) {
        super(context, R.layout.list_user_item, recycledArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Recycled recycled = getItem(position);
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_user_item, parent, false);

        ImageView listImage    = view.findViewById(R.id.listImage);
        TextView  txtMatName   = view.findViewById(R.id.listMatName);
        TextView  txtMatPieces = view.findViewById(R.id.listUserName);

        // IMAGE SET
        listImage.setImageResource(R.drawable.ic_recycling);
        txtMatName.setText(recycled.getMat().getMatName());
        txtMatPieces.setText(String.valueOf(recycled.getPieces()) + " pieces");
        return view;
    }
}
