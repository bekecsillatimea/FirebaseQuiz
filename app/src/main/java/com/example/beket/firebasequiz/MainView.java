package com.example.beket.firebasequiz;

import android.net.Uri;

import com.firebase.ui.auth.AuthUI;

import java.util.List;

public interface MainView {

    void userName(String userName);
    void authState(List<AuthUI.IdpConfig> providers);
    void currentScore(int currentScore);
    void gamesPlayed(int gamesPlayed);
    void gamesWon(int gamesWon);
    void oppUserName(String oppUserName);
    void oppUserImage(Uri oppUserImage);
    void oppCurrentScore(int oppCurrentScore);
    void oppGamesPlayed(int oppGamesPlayed);
    void oppGamesWon(int oppGamesWon);
    void userImage(Uri userImage);
    void startGameLayout();
    void gameLayout();
    void oppSearching();
    void oppSearchingCancelled();
    void oppFound();
}
