package com.example.beket.firebasequiz;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author beket
 */
public class MainActivity extends AppCompatActivity implements MainView {

    private static final int RC_SIGN_IN = 1;
    private static final String MY_PREF = "My_Prefs";
    Fragment fragment;

    @BindView(R.id.start_button)
    Button startGame;
    @BindView(R.id.user_name_text_view)
    TextView userName;
    @BindView(R.id.opp_user_name_text_view)
    TextView oppUserName;
    @BindView(R.id.my_player_image_view)
    ImageView userImage;
    @BindView(R.id.opp_progress_bar)
    ProgressBar oppProgressBar;
    @BindView(R.id.opp_player_image_view)
    ImageView oppUserImage;
    MainPresenter mainPresenter;
    @BindView(R.id.start_fragment_container)
    FrameLayout startFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fragment = new StartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.start_fragment_container, fragment).commit();

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);

        mainPresenter = new MainPresenter(this, sharedPreferences);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startGame.getText().equals(getResources().getString(R.string.button_start))) {
                    mainPresenter.testRoom();
                    startAnimation();
                    startGame.setText(R.string.button_cancel);
                } else {
                    startGame.setText(R.string.button_start);
                    endAnimation();
                    mainPresenter.cancelSearching();
                }
            }
        });

    }

    private void endAnimation() {

        userImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                userImage.getLocationOnScreen(location);
                float x = location[0];
                float y = location[1];
                Log.d("TEST", "onGlobalLayout: x -> " + x + " y ->" + y);
            }
        });

        ObjectAnimator imageAnimatorX = ObjectAnimator.ofFloat(userImage, "x", 22f);
        ObjectAnimator imageAnimatorY = ObjectAnimator.ofFloat(userImage, "y", 16f);
        ObjectAnimator nameAnimatorX = ObjectAnimator.ofFloat(userName, "x", 180f);
        ObjectAnimator nameAnimatorY = ObjectAnimator.ofFloat(userName, "y", 40f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(imageAnimatorX, imageAnimatorY, nameAnimatorX, nameAnimatorY);
        animatorSet.start();
    }

    private void startAnimation() {
        ObjectAnimator imageAnimatorX = ObjectAnimator.ofFloat(userImage, "x", 300f);
        ObjectAnimator imageAnimatorY = ObjectAnimator.ofFloat(userImage, "y", 250f);
        ObjectAnimator nameAnimatorX = ObjectAnimator.ofFloat(userName, "x", 240f);
        ObjectAnimator nameAnimatorY = ObjectAnimator.ofFloat(userName, "y", 400f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(imageAnimatorX, imageAnimatorY, nameAnimatorX, nameAnimatorY);
        animatorSet.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.addAuthStateListener();
        //mainPresenter.executorTest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.detachAuthStateListener();
        mainPresenter.detachDatabaseReadListener();
        //mainPresenter.stopExecutor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void userName(String userName) {
        this.userName.setText(userName);
    }

    @Override
    public void authState(List<AuthUI.IdpConfig> providers) {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(), RC_SIGN_IN);
    }

    @Override
    public void currentScore(int currentScore) {
        //int to String
    }

    @Override
    public void gamesPlayed(int gamesPlayed) {
    }

    @Override
    public void gamesWon(int gamesWon) {
    }

    @Override
    public void oppUserName(String oppUserName) {
        this.oppUserName.setText(oppUserName);
    }

    @Override
    public void oppUserImage(Uri oppUserImage) {
        Glide.with(this)
                .load(userImage)
                .apply(RequestOptions.circleCropTransform())
                .into(this.oppUserImage);

    }

    @Override
    public void oppCurrentScore(int oppCurrentScore) {
    }

    @Override
    public void oppGamesPlayed(int oppGamesPlayed) {
    }

    @Override
    public void oppGamesWon(int oppGamesWon) {
    }

    @Override
    public void userImage(Uri userImage) {
        Glide.with(getApplicationContext())
                .load(userImage)
                .apply(RequestOptions.circleCropTransform())
                .into(this.userImage);
    }

    @Override
    public void startGameLayout() {
        startFragmentContainer.setVisibility(View.VISIBLE);
        oppProgressBar.setVisibility(View.GONE);
        oppUserImage.setVisibility(View.GONE);
        oppUserName.setVisibility(View.GONE);
    }

    @Override
    public void gameLayout() {
    }

    @Override
    public void oppSearching() {
        startFragmentContainer.setVisibility(View.GONE);
        oppProgressBar.setVisibility(View.VISIBLE);
        oppUserImage.setVisibility(View.GONE);
        oppUserName.setVisibility(View.GONE);
    }

    @Override
    public void oppSearchingCancelled() {
        startFragmentContainer.setVisibility(View.VISIBLE);
        oppProgressBar.setVisibility(View.GONE);
        oppUserImage.setVisibility(View.GONE);
        oppUserName.setVisibility(View.GONE);
    }

    @Override
    public void oppFound() {
        oppProgressBar.setVisibility(View.GONE);
        oppUserImage.setVisibility(View.VISIBLE);
        oppUserName.setVisibility(View.VISIBLE);
        oppFoundAnimation();
    }

    private void oppFoundAnimation() {
        ObjectAnimator imageAnimatorX = ObjectAnimator.ofFloat(oppUserImage, "x", 1500f);
        ObjectAnimator imageAnimatorY = ObjectAnimator.ofFloat(oppUserImage, "y", 16f);
        ObjectAnimator nameAnimatorX = ObjectAnimator.ofFloat(oppUserName, "x", 1300f);
        ObjectAnimator nameAnimatorY = ObjectAnimator.ofFloat(oppUserName, "y", 40f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(imageAnimatorX, imageAnimatorY, nameAnimatorX, nameAnimatorY);
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.start_fragment_container, fragment)
                .commit();
    }
}
