package com.example.beket.firebasequiz;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

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
    TextView dateText;
    DatabaseReference dailyDataReference;
    SimpleDateFormat dateFormat;
    Calendar calendar;
    String strDate;
    DatePickerDialog.OnDateSetListener onDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.stats_fragment, container, false);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        barChart = root.findViewById(R.id.chart);
        dateText = root.findViewById(R.id.date_text_view);
        DatabaseReference playersReference = FirebaseDatabase.getInstance().getReference().child("players");
        dailyDataReference = playersReference.child(userId).child("dailyData");
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        strDate = dateFormat.format(calendar.getTime());
        dateText.setText(strDate);
        dateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (getContext() != null) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(),
                            android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                            onDateSetListener,
                            year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                if (dayOfMonth < 10) strDate = year + "-" + month + "-0" + dayOfMonth;
                else strDate = year + "-" + month + "-" + dayOfMonth;
                dateText.setText(strDate);
                getDateData();
            }
        };
        getDateData();
        return root;
    }

    void setUpStats() {

        BarDataSet set1;
        BarData data;
        if (dailyData != null) {
            entriesGroup1.add(new BarEntry(1f, dailyData.getGamesPlayed()));
            entriesGroup1.add(new BarEntry(2f, dailyData.getGamesWon()));
            entriesGroup1.add(new BarEntry(3f, dailyData.getCorrectAnswers()));
            entriesGroup1.add(new BarEntry(4f, dailyData.getAverageTime()));
            entriesGroup1.add(new BarEntry(5f, dailyData.getPlayedTime()));
            set1 = new BarDataSet(entriesGroup1, "Group 1");
            set1.setColors(ColorTemplate.COLORFUL_COLORS);
            data = new BarData(set1);
        } else {
            barChart.setNoDataText("No data for this date!");
            data = null;
        }
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

    void getDateData() {
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
    }
}
