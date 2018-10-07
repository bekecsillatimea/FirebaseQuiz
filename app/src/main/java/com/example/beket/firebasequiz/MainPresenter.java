package com.example.beket.firebasequiz;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MainPresenter {

    FirebaseAuth mFirebaseAuth;
    private ScheduledExecutorService exec;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mPlayersDatabaseReference, mRoomDatabaseReference,
            mMyPlayerDatabaseReference, mMyDailyDataDatabaseReference, mMyGameDatabaseReference;
    private SharedPreferences sharedPreferences;
    private ValueEventListener mValueEventListener, oppPlayerValueEventListener, oppGameValueEventListener;
    private MainView mainView;
    private String uniqueKey;
    private static final String UNIQUE_KEY = "Unique_key";
    private Player myPlayer, opponentPlayer;
    private Game myGame, opponentGame;
    private DailyData dailyData;
    private String roomKey = "";
    int previousPoints = 0;
    private long x = 0;

    MainPresenter(final MainView mainView, final SharedPreferences sharedPreferences) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mPlayersDatabaseReference = mFirebaseDatabase.getReference().child("players");
        mRoomDatabaseReference = mFirebaseDatabase.getReference().child("room");
        this.sharedPreferences = sharedPreferences;
        this.mainView = mainView;

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    onSignedInInitialize(firebaseUser);
                } else {
                    onSignedOutCleanup();
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.EmailBuilder().build());
                    mainView.authState(providers);
                }
            }
        };
    }

    private void onSignedInInitialize(final FirebaseUser firebaseUser) {
        uniqueKey = firebaseUser.getUid();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(uniqueKey).exists()) {
                    Player player = new Player(firebaseUser.getDisplayName(), uniqueKey, firebaseUser.getPhotoUrl().toString(), true, 0);
                    mPlayersDatabaseReference.child(uniqueKey).child("player").setValue(player);
                }
                setUpStartLayout(firebaseUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPlayersDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setUpStartLayout(FirebaseUser firebaseUser) {
        mainView.startGameLayout();
        mainView.userName(firebaseUser.getDisplayName());
        mainView.userImage(Uri.parse(firebaseUser.getPhotoUrl().toString()));
        mMyPlayerDatabaseReference = mPlayersDatabaseReference.child(uniqueKey).child("player");
        mMyDailyDataDatabaseReference = mPlayersDatabaseReference.child(uniqueKey).child("dailyData");
        mMyGameDatabaseReference = mPlayersDatabaseReference.child(uniqueKey).child("game");
        setDailyData();
        attachMyPlayerDatabaseListener();
    }


    private void attachMyPlayerDatabaseListener() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPlayer = dataSnapshot.getValue(Player.class);
                mMyPlayerDatabaseReference.child("online").setValue(true);
                mMyPlayerDatabaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mMyPlayerDatabaseReference.addValueEventListener(valueEventListener);
    }

    private void setDailyData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        final String strDate = dateFormat.format(calendar.getTime());
        final DailyData dailyData = new DailyData(0, 0, 0, 0f, 0f);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(strDate).exists()) {
                    mMyDailyDataDatabaseReference.child(strDate).setValue(dailyData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mMyDailyDataDatabaseReference.addValueEventListener(valueEventListener);
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }

    void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mPlayersDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    void testRoom() {
        mainView.oppSearching();
        roomKey = mRoomDatabaseReference.push().getKey();
        mRoomDatabaseReference.child(roomKey).setValue(myPlayer);
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    opponentPlayer = snapshot.getValue(Player.class);
                    String opponentKey = opponentPlayer.getUniqueKey();
                    if (!opponentKey.equals(uniqueKey)) {
                        mRoomDatabaseReference.child(roomKey).removeValue();
                        mainView.oppFound();
                        Game game = new Game(0, 0, 0f, 0f);
                        mMyGameDatabaseReference.setValue(game);
                        setOpponentPlayerListener();
                        setOpponentGameListener();
                        mRoomDatabaseReference.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRoomDatabaseReference.addValueEventListener(listener);
    }

    private void setOpponentGameListener() {
        oppGameValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opponentGame = dataSnapshot.getValue(Game.class);
                if (opponentGame != null) {
                    while (opponentGame.getPoints() > previousPoints)
                        mainView.oppCurrentScore(previousPoints++);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPlayersDatabaseReference.child(opponentPlayer.getUniqueKey()).child("game").addValueEventListener(oppGameValueEventListener);
    }

    private void setOpponentPlayerListener() {
        oppPlayerValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mainView.gameLayout();
                opponentPlayer = dataSnapshot.getValue(Player.class);
                mainView.oppUserName(opponentPlayer.getUserName());
                mainView.oppUserImage(Uri.parse(opponentPlayer.getUserImageUri()));
                mPlayersDatabaseReference.child(opponentPlayer.getUniqueKey()).removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPlayersDatabaseReference.child(opponentPlayer.getUniqueKey()).child("player").addValueEventListener(oppPlayerValueEventListener);
    }

    void addPoints() {
        int currentScore = myGame.getPoints();
        currentScore++;
        mMyPlayerDatabaseReference.child("currentPoints").setValue(currentScore);
    }

    void endGame() {
        if (myGame.getPoints() > opponentGame.getPoints()) {
            int gamesWon = dailyData.getGamesWon();
            gamesWon++;
            mMyPlayerDatabaseReference.child("gamesWon").setValue(gamesWon);
        }
        int gamesPlayed = dailyData.getGamesPlayed();
        gamesPlayed++;
        mMyPlayerDatabaseReference.child("gamesPlayed").setValue(gamesPlayed);
        mMyPlayerDatabaseReference.child("currentScore").setValue(0);
        mPlayersDatabaseReference.child(opponentPlayer.getUniqueKey()).removeEventListener(oppPlayerValueEventListener);
    }

    void cancelSearching() {
        mRoomDatabaseReference.child(roomKey).removeValue();
        mainView.oppSearchingCancelled();
    }

    void addAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    void detachAuthStateListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    void executorTest() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                x++;
                Log.d("test", "" + x);
            }
        };
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        exec.shutdown();
    }

    void stopExecutor() {
        exec.shutdown();
    }

    void startAnimation() {

    }
}
