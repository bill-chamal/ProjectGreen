package com.example.projectgreen;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class UserStatisticsFragment extends Fragment {

    private User user;
    private PieChart piechart;
    private TextView lblScore;
    private ProgressBar progressBar;
    private int bonusValue;
    public UserStatisticsFragment(User user){
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
        piechart.animateXY(900,900);
        piechart.setDrawRoundedSlices(false);

        piechart.setCenterTextSize(34f);
        piechart.setCenterTextColor(R.color.font_dark_green);
        piechart.setDrawCenterText(true);

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> pValues = new ArrayList<>();

        int total_plastic = user.getTotalPieceOfMaterial(MaterialType.matn1);
        int total_paper   = user.getTotalPieceOfMaterial(MaterialType.matn2);
        int total_glass   = user.getTotalPieceOfMaterial(MaterialType.matn3);
        int total_metal   = user.getTotalPieceOfMaterial(MaterialType.matn4);

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

        PieDataSet dataSet = new PieDataSet(pValues, "Materials Percentage (%)");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.GREEN);

        piechart.setData(data);

        // Set to labels total piece by material
        ((TextView)view.findViewById(R.id.lblPlastic)).setText( String.valueOf(total_plastic));
        ((TextView)view.findViewById(R.id.lblPaper)).setText(   String.valueOf(total_paper  ));
        ((TextView)view.findViewById(R.id.lblGlass)).setText(   String.valueOf(total_glass  ));
        ((TextView)view.findViewById(R.id.lblMetal)).setText(   String.valueOf(total_metal  ));
        ((TextView)view.findViewById(R.id.lblTotal)).setText(   String.valueOf(total_plastic + total_glass + total_paper + total_metal)  );

        setScoreView(view);
        ((Button)view.findViewById(R.id.btnTakeCash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double score = user.getBalance() + bonusValue;
                user.setNewScore(score);
                setScoreView(view);
            }
        });

        return view;
    }

    private void setScoreView(View view){
        lblScore = view.findViewById(R.id.lblLevel);
        lblScore.setText(String.valueOf(user.getTotalCashback()));

        progressBar = view.findViewById(R.id.progressBar);
//        progressBar.setProgress( (int)Math.abs((user.getTotalCashback()-(int)user.getTotalCashback())*100));
        progressBar.setProgress( user.getPoints() );

        if(user.getPoints() < 100)
            ((Button)view.findViewById(R.id.btnTakeCash)).setEnabled(false);


        piechart.setCenterText(String.valueOf(user.getPoints()) + "\npoints" );

        //specify bonus
        ((TextView)view.findViewById(R.id.apprQty)).setText("Approved material\nquantity bonus\n(1$ per " + MaterialType.getBonus() + " pieces)");

        // Set label score view
        String formattedCashback = String.format("%.2f", user.getTotalCashback());
        ((TextView)view.findViewById(R.id.lblLevel)).setText(formattedCashback + " $");

        String formattedBalance = String.format("%.2f", user.getBalance());
        ((TextView)view.findViewById(R.id.lblbalance)).setText(formattedBalance + " $");

        bonusValue = (int) Math.ceil(user.getApprMatQ() / MaterialType.getBonus()); // ALLAGH OTAN GINEI TO USER BALANCE

        ((TextView)view.findViewById(R.id.apprQtyVl)).setText(String.valueOf(bonusValue)+ "$  from\n" + user.getApprMatQ() + " pieces");

        // Set total balance for cashback (basic material cashback value and bonus depending on quantity of materials)
        String formattedtotal = String.format("%.2f", user.getBalance() + bonusValue);
        ((TextView)view.findViewById(R.id.totalBalance)).setText(formattedtotal +" $");

    }
}