package com.example.beket.firebasequiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    DailyData dailyData;
    List<BarEntry> entriesGroup1 = new ArrayList<>();
    BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.stats_fragment, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        barChart = root.findViewById(R.id.chart);
        DatabaseReference playersReference = FirebaseDatabase.getInstance().getReference().child("players");
        DatabaseReference dailyDataReference = playersReference.child(userId).child("dailyData");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        final String strDate = dateFormat.format(calendar.getTime());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dailyData = dataSnapshot.getValue(DailyData.class);
                setUpStats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dailyDataReference.child(strDate).addListenerForSingleValueEvent(valueEventListener);
        return root;
    }

    void setUpStats() {
        entriesGroup1.add(new BarEntry(1f, dailyData.getGamesPlayed()));
        entriesGroup1.add(new BarEntry(2f, dailyData.getGamesWon()));
        entriesGroup1.add(new BarEntry(3f, dailyData.getCorrectAnswers()));
        entriesGroup1.add(new BarEntry(4f, dailyData.getAverageTime()));
        entriesGroup1.add(new BarEntry(5f, dailyData.getPlayedTime()));

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Group 1");
        set1.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(set1);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.setDrawValueAboveBar(true);
        barChart.setDoubleTapToZoomEnabled(false);

        final String[] xAxisValues = {"", "Played", "Won", "Correct Answers", "Response Time(s)", "Playing Time(m)"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueToInt = (int) value;
                return xAxisValues[valueToInt];
            }
        });
    }
}
