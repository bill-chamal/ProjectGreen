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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminViewLeaderboard extends Fragment {

    private LeaderboardListAdapter leaderboardListAdapter;
    private ArrayList<User> userList = new ArrayList<>();
    private ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BarChart barChart;
    private BarChart valueBarChart;
    private List<String> xValues = Arrays.asList(MaterialType.matn1, MaterialType.matn2, MaterialType.matn3, MaterialType.matn4);


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static AdminViewLeaderboard newInstance(String param1, String param2) {
        AdminViewLeaderboard fragment = new AdminViewLeaderboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
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
        View view = inflater.inflate(R.layout.fragment_admin_view_leaderboard, container, false);
        barChart = view.findViewById(R.id.adminBarChart);
        valueBarChart = view.findViewById(R.id.adminValueBarChart);

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
                    Collections.reverse(userList);
                    ArrayList<User> tempList = userList;

                    if (userList.size() > 5)
                        tempList = new ArrayList<>(userList.subList(0,5));

                    leaderboardListAdapter = new LeaderboardListAdapter(getContext(), tempList );
                    listView = view.findViewById(R.id.listTopUsersView);
                    listView.setAdapter(leaderboardListAdapter);
                    listView.setClickable(true);

                    int total_plastic = 0;
                    int total_paper = 0;
                    int total_glass = 0;
                    int total_metal = 0;
                    int totalValue_plastic = 0;
                    int totalValue_paper   = 0;
                    int totalValue_glass   = 0;
                    int totalValue_metal   = 0;

                    // Get total recycled pieces by material
                    for (User u : userList) {
                        total_plastic +=    u.getTotalPieceOfMaterial(MaterialType.matn1);
                        total_paper +=      u.getTotalPieceOfMaterial(MaterialType.matn2);
                        total_glass +=      u.getTotalPieceOfMaterial(MaterialType.matn3);
                        total_metal +=      u.getTotalPieceOfMaterial(MaterialType.matn4);
                    }

                    // Get total recycle value by material
                    for (User u : userList) {
                        totalValue_plastic +=    u.getValueOfTotalPieceOfMaterial(MaterialType.matn1);
                        totalValue_paper +=      u.getValueOfTotalPieceOfMaterial(MaterialType.matn2);
                        totalValue_glass +=      u.getValueOfTotalPieceOfMaterial(MaterialType.matn3);
                        totalValue_metal +=      u.getValueOfTotalPieceOfMaterial(MaterialType.matn4);
                    }

                    // Bar Chart
                    barChart.getAxisRight().setEnabled(false);
                    valueBarChart.getAxisRight().setEnabled(false);
                    // 0~3 index from xValues names
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0, total_plastic));
                    entries.add(new BarEntry(1, total_paper));
                    entries.add(new BarEntry(2, total_glass));
                    entries.add(new BarEntry(3, total_metal));

                    ArrayList<BarEntry> valueEntries = new ArrayList<>();
                    valueEntries.add(new BarEntry(0, totalValue_plastic));
                    valueEntries.add(new BarEntry(1, totalValue_paper));
                    valueEntries.add(new BarEntry(2, totalValue_glass));
                    valueEntries.add(new BarEntry(3, totalValue_metal));

                    YAxis yAxis = barChart.getAxisLeft();
                    yAxis.setAxisMinimum(0f);
                    yAxis.setAxisLineWidth(2f);
                    yAxis.setAxisLineColor(Color.GRAY);
                    yAxis.setLabelCount(10);

                    YAxis valueyAxis = valueBarChart.getAxisLeft();
                    valueyAxis.setAxisMinimum(0f);
                    valueyAxis.setAxisLineWidth(2f);
                    valueyAxis.setAxisLineColor(Color.GRAY);
                    valueyAxis.setLabelCount(10);

                    BarDataSet dataSet = new BarDataSet(entries, "Materials");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                    BarDataSet valueDataSet = new BarDataSet(valueEntries, "Materials");
                    valueDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    BarData valueBarData = new BarData(valueDataSet);
                    valueBarChart.setData(valueBarData);

                    barChart.getDescription().setEnabled(false);
                    barChart.invalidate();
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setGranularityEnabled(true);

                    barData.setValueTextSize(16f);

                    valueBarChart.getDescription().setEnabled(false);
                    valueBarChart.invalidate();
                    valueBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
                    valueBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    valueBarChart.getXAxis().setGranularity(1f);
                    valueBarChart.getXAxis().setGranularityEnabled(true);

                    barChart.getLegend().setEnabled(true);

                    valueBarData.setValueTextSize(16f);

                    Log.i("ADMIN_LEADERBOARD", "Successfully load of leaderboard! - AdminViewLeaderboard.java");
                } else {
                    Log.e("ADMIN_LEADERBOARD", "Error while transferring user data - AdminViewLeaderboard.java");
                }
            }
        });

        return view;
    }
}