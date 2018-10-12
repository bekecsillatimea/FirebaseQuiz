package com.example.beket.firebasequiz.learderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beket.firebasequiz.Player;
import com.example.beket.firebasequiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    RecyclerView recyclerView;
    final ArrayList<Player> players = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("players");
    ArrayList<Player> orderedPlayers = new ArrayList<>();
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            players.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Player player = snapshot.child("player").getValue(Player.class);
                players.add(player);
            }
            setUpRecyclerView(recyclerView);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void setUpLeaderboardOrder() {
        List<Integer> maxGamesWon = new ArrayList<>();
        int maxNumber = 0;
        for(Player player: players){
            maxGamesWon.add(player.getGamesWon());
        }
        int maxGamesWonSize = maxGamesWon.size();
        while(maxGamesWonSize != 0){
            for(int i = 0; i < maxGamesWon.size() - 1; i++){
                int currentNumber = maxGamesWon.get(i);
                if(currentNumber > maxNumber) maxNumber = currentNumber;
            }

            int index = maxGamesWon.indexOf(maxNumber);
            orderedPlayers.add(players.get(index));
            maxGamesWon.remove(index);
            players.remove(index);
            maxGamesWonSize--;
            maxNumber = 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.leaderboard_fragment, container, false);
        recyclerView = root.findViewById(R.id.leaderboard_recycler_view);
        databaseReference.addListenerForSingleValueEvent(listener);
        return root;
    }

    void setUpRecyclerView(RecyclerView recyclerView){
        setUpLeaderboardOrder();
        LeaderboardAdapter adapter = new LeaderboardAdapter(orderedPlayers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
