package com.example.projectgreen;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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
    Bitmap bitmap1, bitmap2;
    admin_main_screen adminMainScreen;

    public AdminViewLeaderboard(admin_main_screen adminMainScreen) {
        this.adminMainScreen = adminMainScreen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_leaderboard, container, false);
        barChart = view.findViewById(R.id.adminBarChart);
        valueBarChart = view.findViewById(R.id.adminValueBarChart);
        TextView geminiView = view.findViewById(R.id.geminiTextView);
        TextView citeGem = view.findViewById(R.id.citeGemini);

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
                        tempList = new ArrayList<>(userList.subList(0, 5));

                    leaderboardListAdapter = new LeaderboardListAdapter(getContext(), tempList);
                    listView = view.findViewById(R.id.listTopUsersView);
                    listView.setAdapter(leaderboardListAdapter);
                    listView.setClickable(true);

                    int total_plastic = 0;
                    int total_paper = 0;
                    int total_glass = 0;
                    int total_metal = 0;
                    int totalValue_plastic = 0;
                    int totalValue_paper = 0;
                    int totalValue_glass = 0;
                    int totalValue_metal = 0;

                    // Get total recycled pieces by material
                    for (User u : userList) {
                        total_plastic += u.getTotalPieceOfMaterial(MaterialType.matn1);
                        total_paper += u.getTotalPieceOfMaterial(MaterialType.matn2);
                        total_glass += u.getTotalPieceOfMaterial(MaterialType.matn3);
                        total_metal += u.getTotalPieceOfMaterial(MaterialType.matn4);
                    }

                    // Get total recycle value by material
                    for (User u : userList) {
                        totalValue_plastic += u.getValueOfTotalPieceOfMaterial(MaterialType.matn1);
                        totalValue_paper += u.getValueOfTotalPieceOfMaterial(MaterialType.matn2);
                        totalValue_glass += u.getValueOfTotalPieceOfMaterial(MaterialType.matn3);
                        totalValue_metal += u.getValueOfTotalPieceOfMaterial(MaterialType.matn4);
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

                    bitmap1 = barChart.getChartBitmap();
                    bitmap2 = valueBarChart.getChartBitmap();

                    generativeModelGemini(geminiView, citeGem, bitmap1, bitmap2);

                    Log.i("ADMIN_LEADERBOARD", "Successfully load of leaderboard! - AdminViewLeaderboard.java");
                } else {
                    Log.e("ADMIN_LEADERBOARD", "Error while transferring user data - AdminViewLeaderboard.java");
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Clear history stack and goto enter screen
                if (adminMainScreen.getDrawerLayout().isDrawerOpen(GravityCompat.START))
                    adminMainScreen.getDrawerLayout().closeDrawer((GravityCompat.START));
                else
                    adminMainScreen.getBottomNavigationView().setSelectedItemId(R.id.adminRequestMenu);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    private void generativeModelGemini(TextView geminiView, TextView citeGemini, Bitmap bitmap1, Bitmap bitmap2) {
        /*
        ! READ IMPORTANT NOTICE !
        This is the AI Model from Google AI called Gemini.
        Gemini requires API Level 21 and higher.
        The usage of Gemini does not violate any Google’s Policy listed here https://policies.google.com/terms/generative-ai/use-policy https://ai.google.dev/gemini-api/terms#use-restrictions
        Gemini is not free in EU. Requires a linked billing account. Provides 300$ free for new accounts, for a specific period of time. https://ai.google.dev/gemini-api/docs/available-regions#unpaid-tier-unavailable
        The linked billing account will be removed in order to avoid any unwilling charges.
         */
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro-vision",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "AIzaSyDGq-JkuIHRm8DzpF0LTRkLSzf3Cf_2lQU");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String textInput = "You are the assistant of a data analyst and you received 2 bar chart images. Each chart has the same materials starting from plastic, paper, glass and metal. Each material is a recycled material that recycled by a user. When a recycled material request is approved by the administrator the user is rewarded by dollar. The first one has the title “Total approved quantity by material”. The first one shows, the total quantity that users requested for approval by the administrator AND the administrator approved it. The second chart has title “Total approved cashback by material”. The second one shows, the total value approved recycled material for all user by each material. You must answer the following questions. Explain both charts. What is being represented on the x and y axes of both charts? What is the most recyclable material? What is the most valuable material? What are the key takeaways or insights you can glean from the chart? (What are the biggest differences, trends, or patterns?) Based on the data, what materials might require more emphasis in recycling programs? (Can inform educational or infrastructure improvements) Keep your answer scientific and serious.";

        Content content = new Content.Builder()
                .addText(textInput)
                .addImage(bitmap1)
                .addImage(bitmap2)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    System.out.println(resultText);
                    citeGemini.setVisibility(View.VISIBLE);
                    geminiView.setText(resultText);
                }

                @Override
                public void onFailure(Throwable t) {
                    geminiView.setText("Due to regional restrictions, the AI data analysis is not available from your current location.");
                }
            }, this.getActivity().getMainExecutor());
        } else
            geminiView.setText("Gemini requires API level 21 and higher");
    }
}