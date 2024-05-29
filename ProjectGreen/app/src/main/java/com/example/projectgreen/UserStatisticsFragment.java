package com.example.projectgreen;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Locale;


public class UserStatisticsFragment extends Fragment {

    private User user;
    private PieChart piechart;
    private TextView lblScore, txtProgress;
    private ProgressBar progressBar;
    private int bonusValue;

    public UserStatisticsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);

        // PIE CHART
        piechart = (PieChart) view.findViewById(R.id.piechartuser);
        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);
        piechart.setDragDecelerationFrictionCoef(0.95f);
        piechart.animateXY(900, 900);
        piechart.setDrawRoundedSlices(false);

        piechart.setCenterTextSize(34f);
        piechart.setCenterTextColor(R.color.font_dark_green);
        piechart.setDrawCenterText(true);

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> pValues = new ArrayList<>();

        int total_plastic = user.getTotalPieceOfMaterial(MaterialType.matn1);
        int total_paper = user.getTotalPieceOfMaterial(MaterialType.matn2);
        int total_glass = user.getTotalPieceOfMaterial(MaterialType.matn3);
        int total_metal = user.getTotalPieceOfMaterial(MaterialType.matn4);

        if (total_plastic > 0) {
            pValues.add(new PieEntry(total_plastic, "Plastic"));
        }
        if (total_paper > 0) {
            pValues.add(new PieEntry(total_paper, "Paper"));
        }
        if (total_glass > 0) {
            pValues.add(new PieEntry(total_glass, "Glass"));
        }
        if (total_metal > 0) {
            pValues.add(new PieEntry(total_metal, "Metal"));
        }

        PieDataSet dataSet = new PieDataSet(pValues, "Total recycled materials (rounded percentage %)");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.GREEN);

        piechart.setData(data);

        // Set to labels total piece by material
        ((TextView) view.findViewById(R.id.lblPlastic)).setText(String.valueOf(total_plastic));
        ((TextView) view.findViewById(R.id.lblPaper)).setText(String.valueOf(total_paper));
        ((TextView) view.findViewById(R.id.lblGlass)).setText(String.valueOf(total_glass));
        ((TextView) view.findViewById(R.id.lblMetal)).setText(String.valueOf(total_metal));
        ((TextView) view.findViewById(R.id.lblTotal)).setText(String.valueOf(total_plastic + total_glass + total_paper + total_metal));

        setScoreView(view);

        txtProgress = view.findViewById(R.id.progressComment);

        if (user.getPoints() < 100)
            txtProgress.setText("Great progress! Keep it up to cash in your available balance");
        else
            txtProgress.setText("Your balance is available to cash in! Scroll down for more details");

        generativeModelGemini(txtProgress);

        ((Button) view.findViewById(R.id.btnTakeCash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double score = user.getBalance() + bonusValue;
                user.setNewScore(score);
                setScoreView(view);
            }
        });

        return view;
    }

    private void setScoreView(View view) {
        lblScore = view.findViewById(R.id.lblLevel);
        lblScore.setText(String.valueOf(user.getTotalCashback()));
        progressBar = view.findViewById(R.id.progressBar);
        // progress bar has maximum of 100
        progressBar.setProgress(Math.min(user.getPoints(), 100));

        if (user.getPoints() < 100)
            ((Button) view.findViewById(R.id.btnTakeCash)).setEnabled(false);


        piechart.setCenterText(String.valueOf(user.getPoints()) + "\npoints");

        //provide info about the current bonus
        ((TextView) view.findViewById(R.id.bonusInfo)).setText("1$ per " + MaterialType.getBonus() + " pieces\nuntil progress bar full");

        //specify bonus
        ((TextView) view.findViewById(R.id.apprQty)).setText("Approved material quantity bonus");

        // Set label score view
        String formattedCashback = String.format("%.2f", user.getTotalCashback());
        ((TextView) view.findViewById(R.id.lblLevel)).setText(formattedCashback + " $");

        String formattedBalance = String.format("%.2f", user.getBalance());
        ((TextView) view.findViewById(R.id.lblbalance)).setText(formattedBalance + " $");

        //calculate the bonus value and stop giving bonus if the user has already reached the cash in threshold to incentivise fast cash ins.
        bonusValue = user.getBonusValue();

        ((TextView) view.findViewById(R.id.apprQtyVl)).setText(String.valueOf(bonusValue) + "$  from " + bonusValue * 30 + "/" + user.getApprMatQ() + "\napproved pieces");

        // Set total balance for cashback (basic material cashback value and bonus depending on quantity of materials)
        String formattedtotal = String.format("%.2f", user.getBalance() + bonusValue);
        ((TextView) view.findViewById(R.id.totalBalance)).setText(formattedtotal + " $");

    }

    private void generativeModelGemini(TextView geminiView) {
        /*
        ! READ IMPORTANT NOTICE !
        This is the AI Model from Google AI called Gemini.
        Gemini requires API Level 21 and higher.
        The usage of Gemini does not violate any Google’s Policy listed here https://policies.google.com/terms/generative-ai/use-policy https://ai.google.dev/gemini-api/terms#use-restrictions
        Gemini is not free in EU. Requires a linked billing account. Provides 300$ free for new accounts, for a specific period of time. https://ai.google.dev/gemini-api/docs/available-regions#unpaid-tier-unavailable
        The linked billing account will be removed in order to avoid any unwilling charges.
         */
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
                /* apiKey */ "AIzaSyDGq-JkuIHRm8DzpF0LTRkLSzf3Cf_2lQU");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String textInput = String.format(Locale.US,
                "You are in a Gamification android app that rewards users for doing progress by recycling. Your purpose is to encourage the user to start recycling when the points are close to 0. Encourage the user to continue recycling when the points are above 0. Encourage the user and reward the user. When the points are 100 or above, the user finished his progress. Reward the user when the points are above or equal to 100 and suggest him to cash in to get rewarded. Be nice to the user, be creative and friendly. Use of emojis are always nice. For example, you could say: Great progress! Keep it up to cash in your available balance. For example, when the points are 100 or above you could say: Your balance is available to cash in! Scroll down for more details. The user’s name is %s. The user now has %d points, what should you say to him. Max words 7-15."
                , user.getUserName(), user.getPoints());

        Content content = new Content.Builder()
                .addText(textInput)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    System.out.println(resultText);
                    geminiView.setText(resultText);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Log.e("GeminiAI", t.toString());
                }
            }, this.getActivity().getMainExecutor());
        } // Else : don't do anything
    }
}