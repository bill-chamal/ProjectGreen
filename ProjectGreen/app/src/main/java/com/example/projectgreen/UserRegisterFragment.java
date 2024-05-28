package com.example.projectgreen;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;


public class UserRegisterFragment extends Fragment {

    private User user;
    private ListView listView;
    private AllRecycleListAdapter allRecycleListAdapter;
    private ArrayList<Recycled> sortedRec;
    private UserViewScreen userViewScreen;
    private TextView txtNotify;
    public UserRegisterFragment(User user, UserViewScreen userViewScreen){
        this.user = user;
        this.userViewScreen = userViewScreen;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_register, container, false);
        txtNotify = view.findViewById(R.id.txtViewNotifyUserEmptyList);
        // In case of slow internet, the user upload will not finish just in time that user changes fragment
        sortedRec = user.getRecycledList();
        sortedRec.sort(new RecycledComperater());
        Collections.reverse(sortedRec);
        allRecycleListAdapter = new AllRecycleListAdapter(getContext(), sortedRec);
        listView = view.findViewById(R.id.listUsersAllMatView);
        listView.setAdapter(allRecycleListAdapter);
        listView.setClickable(true);

        if (sortedRec.isEmpty())
            txtNotify.setVisibility(View.VISIBLE);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (userViewScreen.getDrawerLayout().isDrawerOpen(GravityCompat.START))
                    userViewScreen.getDrawerLayout().closeDrawer((GravityCompat.START));
                else
                    userViewScreen.getBottomNavigationView().setSelectedItemId(R.id.points);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }
    public void addNewRec(Recycled recycled){
        txtNotify.setVisibility(View.GONE);
        sortedRec.add(0, recycled);
        allRecycleListAdapter.notifyDataSetChanged();
    }

    public void keepListUpdated(Recycled rec){
        if (sortedRec.size() < user.getRecycledList().size()) {
            addNewRec(rec);
            Log.i("USER_ASY_LIST", "List updated with updated data");
        }
    }

    public void notifyAdapter(){
        allRecycleListAdapter.notifyDataSetChanged();
    }
}