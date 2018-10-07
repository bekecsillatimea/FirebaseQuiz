package com.example.beket.firebasequiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.beket.firebasequiz.learderboard.LeaderboardFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartFragment extends Fragment {

    @BindView(R.id.leaderboard_image)
    ImageView leaderboardImage;
    @BindView(R.id.stats_image)
    ImageView statsImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.start_fragment, container, false);
        ButterKnife.bind(this, root);
        final Fragment leaderboardFragment = new LeaderboardFragment();
        final Fragment statsFragment = new StatsFragment();

        leaderboardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.start_fragment_container, leaderboardFragment).commit();
                }
            }
        });

        statsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.start_fragment_container, statsFragment).commit();
                }
            }
        });

        return root;
    }
}
