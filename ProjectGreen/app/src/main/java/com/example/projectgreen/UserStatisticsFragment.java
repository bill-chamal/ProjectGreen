package com.example.projectgreen;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class UserStatisticsFragment extends Fragment {

    private User user;
    private PieChart piechart;

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

        piechart.setCenterText( String.valueOf(user.getPoints()) );
        piechart.setCenterTextSize(34f);
        piechart.setCenterTextColor(R.color.font_dark_green);
        piechart.setDrawCenterText(true);

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> pValues = new ArrayList<>();

        pValues.add(new PieEntry(user.getTotalPieceOfMaterial(MaterialType.matn1), "Plastic"));
        pValues.add(new PieEntry(user.getTotalPieceOfMaterial(MaterialType.matn2), "Paper"));
        pValues.add(new PieEntry(user.getTotalPieceOfMaterial(MaterialType.matn3), "Glass"));
        pValues.add(new PieEntry(user.getTotalPieceOfMaterial(MaterialType.matn4), "Metal"));

        PieDataSet dataSet = new PieDataSet(pValues, "Materials");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.GREEN);

        piechart.setData(data);

        return view;
    }
}