package com.example.projectgreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;


public class UserRegisterFragment extends Fragment {

    private User user;
    private ListView listView;
    private AllRecycleListAdapter allRecycleListAdapter;
    public UserRegisterFragment(User user){
        this.user = user;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_register, container, false);
        ArrayList<Recycled> recTemp = user.getRecycledList();
        allRecycleListAdapter = new AllRecycleListAdapter(getContext(), recTemp);
        listView = view.findViewById(R.id.listUsersAllMatView);
        listView.setAdapter(allRecycleListAdapter);
        listView.setClickable(true);

        return view;
    }
}