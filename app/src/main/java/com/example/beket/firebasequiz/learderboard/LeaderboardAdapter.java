package com.example.beket.firebasequiz.learderboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beket.firebasequiz.Player;
import com.example.beket.firebasequiz.R;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter {

    private ArrayList<Player> players;

    LeaderboardAdapter(ArrayList<Player> players){
        this.players = players;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Player currentPlayer = players.get(position);
        ((Holder) holder).numberText.setText(String.valueOf(position + 1));
        ((Holder) holder).userNameText.setText(currentPlayer.getUserName());
        ((Holder) holder).topText.setText(String.valueOf(currentPlayer.getGamesWon()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView numberText, userNameText, topText;

        Holder(View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.number_text_view);
            userNameText = itemView.findViewById(R.id.user_name_text_view);
            topText = itemView.findViewById(R.id.top_text_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // start UserFragment
        }
    }

}
