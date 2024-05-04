package com.example.projectgreen;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdminViewLeaderboard extends Fragment {

    private LeaderboardListAdapter leaderboardListAdapter;
    private ArrayList<User> userList = new ArrayList<>();
    private ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BarChart barChart;

    private List<String> xValues = Arrays.asList(MaterialType.matn1, MaterialType.matn2, MaterialType.matn3, MaterialType.matn4);



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_leaderboard, container, false);
        barChart = view.findViewById(R.id.adminBarChart);
        // Get all users collection from firestore
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> doc = null;
                    // Foreach document collection get every user
                    for (DocumentSnapshot document : task.getResult()) {
                        doc = document.getData();
                        User user1 = new User();
                        user1.populate(doc);
                        if (!user1.isAdmin()) {
                            userList.add(user1);
                        }
                    }

                    userList.sort(new UserComparator());
                    ArrayList<User> tempList = userList;

                    if (userList.size() > 5)
                        tempList = new ArrayList<>(userList.subList(0,5));

                    leaderboardListAdapter = new LeaderboardListAdapter(getContext(), tempList );
                    listView = view.findViewById(R.id.listTopUsersView);
                    listView.setAdapter(leaderboardListAdapter);
                    listView.setClickable(true);

                    // Get total recycle consumption by material
                    int total_plastic = 0;
                    int total_paper   = 0;
                    int total_glass   = 0;
                    int total_metal   = 0;

                    for (User u : userList) {
                        total_plastic += u.getValueOfTotalPieceOfMaterial(MaterialType.matn1);
                        total_paper += u.getValueOfTotalPieceOfMaterial(MaterialType.matn2);
                        total_glass += u.getValueOfTotalPieceOfMaterial(MaterialType.matn3);
                        total_metal += u.getValueOfTotalPieceOfMaterial(MaterialType.matn4);
                    }

                    // Bar Chart
                    barChart.getAxisRight().setDrawLabels(true);
                    // 0~3 index from xValues names
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0, total_plastic));
                    entries.add(new BarEntry(1, total_paper));
                    entries.add(new BarEntry(2, total_glass));
                    entries.add(new BarEntry(3, total_metal));

                    YAxis yAxis = barChart.getAxisLeft();
                    yAxis.setAxisMinimum(0f);
//                    yAxis.setAxisMaximum(100f);
                    yAxis.setAxisLineWidth(2f);
                    yAxis.setAxisLineColor(Color.GRAY);
                    yAxis.setLabelCount(10);

                    BarDataSet dataSet = new BarDataSet(entries, "Materials");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    barChart.getDescription().setEnabled(false);
                    barChart.invalidate();
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setGranularityEnabled(true);

                    Log.i("ADMIN_LEADERBOARD", "Successfully load of leaderboard! - AdminViewLeaderboard.java");
                } else {
                    Log.e("ADMIN_LEADERBOARD", "Error while transferring user data - AdminViewLeaderboard.java");
                }
            }
        });

        return view;
    }
}