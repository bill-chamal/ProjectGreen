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

public class AllRecycleListAdapter extends ArrayAdapter<Recycled> {
    public AllRecycleListAdapter(@NonNull Context context, ArrayList<Recycled> recycledArrayList) {
        super(context, R.layout.list_user_item, recycledArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Recycled recycled = getItem(position);
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_user_item, parent, false);

        ImageView listImage    = view.findViewById(R.id.listImage);
        TextView txtMatName   = view.findViewById(R.id.listMatName);
        TextView  txtApproveStatus = view.findViewById(R.id.listUserName);

        // IMAGE SET
        listImage.setImageResource(R.drawable.ic_request_approve);
        String appr;
        if (recycled.isApproved() == Recycled.NOT_APPROVED)
            appr = "Pending";
        else if (recycled.isApproved() == Recycled.APPROVED)
            appr = "Approved";
        else
            appr = "Rejected";

        txtMatName.setText(appr);
        txtApproveStatus.setText(recycled.getMat().getMatName()+" "+String.valueOf(recycled.getPieces()) + " pieces\n"+recycled.getTimestamp().toDate());

        ViewGroup.LayoutParams params = txtApproveStatus.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        ViewGroup.MarginLayoutParams mparams = (ViewGroup.MarginLayoutParams) txtApproveStatus.getLayoutParams();
        int marginInDp = 3;
        float scale = getContext().getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        mparams.topMargin = marginInPixels;

        txtApproveStatus.setLayoutParams(params);
        txtApproveStatus.setLayoutParams(mparams);

        return view;
    }
}
