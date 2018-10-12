package com.example.beket.firebasequiz;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
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
    @BindView(R.id.count_down_progress_bar)
    ProgressBar countDownProgressBar;
    @BindView(R.id.second_count_down_progress_bar)
    ProgressBar secondCountDownProgressBar;
    @BindView(R.id.count_down_text)
    TextView countDownText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fragment = new StartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.start_fragment_container, fragment).commit();

        final Animation userImageAnimation = AnimationUtils.loadAnimation(this, R.anim.user_image_anim);

        mainPresenter = new MainPresenter(this);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startGame.getText().equals(getResources().getString(R.string.button_start))) {
                    mainPresenter.testRoom();
                    //startAnimation();
                    userImage.startAnimation(userImageAnimation);
                    startTranslateAnimation();
                    startGame.setText(R.string.button_cancel);
                } else {
                    startGame.setText(R.string.button_start);
                    //endAnimation();
                    mainPresenter.cancelSearching();
                }
            }
        });
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
        //mainPresenter.inAppTimeCount();
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
                .load(oppUserImage)
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
        startFragmentContainer.setVisibility(View.VISIBLE);
        //oppFoundAnimation();
        endTranslateAnimation();
    }

    @Override
    public void gameStart() {
        GameFragment gameFragment = new GameFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.start_fragment_container, gameFragment).commit();
    }

    @Override
    public void countDownTimer(int countDown) {
        int progressBarMax = countDownProgressBar.getMax();
        int remainingTime = progressBarMax - countDown;
        countDownProgressBar.setProgress(countDown);
        secondCountDownProgressBar.setProgress(countDown);
        countDownText.setText(String.valueOf(countDown));
        if(countDown <= 10) secondCountDownProgressBar.setProgress(10);
    }

    private void endAnimation() {

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


    private void oppFoundAnimation() {
        ObjectAnimator oppImageAnimatorX = ObjectAnimator.ofFloat(oppUserImage, "x", 1500f);
        ObjectAnimator oppImageAnimatorY = ObjectAnimator.ofFloat(oppUserImage, "y", 16f);
        ObjectAnimator oppNameAnimatorX = ObjectAnimator.ofFloat(oppUserName, "x", 1300f);
        ObjectAnimator oppNameAnimatorY = ObjectAnimator.ofFloat(oppUserName, "y", 40f);

        ObjectAnimator imageAnimatorX = ObjectAnimator.ofFloat(userImage, "x", 22f);
        ObjectAnimator imageAnimatorY = ObjectAnimator.ofFloat(userImage, "y", 16f);
        ObjectAnimator nameAnimatorX = ObjectAnimator.ofFloat(userName, "x", 180f);
        ObjectAnimator nameAnimatorY = ObjectAnimator.ofFloat(userName, "y", 40f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(oppImageAnimatorX, oppImageAnimatorY, oppNameAnimatorX, oppNameAnimatorY
                , imageAnimatorX, imageAnimatorY, nameAnimatorX, nameAnimatorY);
        animatorSet.start();
    }

    private void startTranslateAnimation() {
        int userNameCenterX = userName.getWidth() / 2;
        TranslateAnimation translateAnimation = new TranslateAnimation(8, 280 - userNameCenterX, 1.8f, 400);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        userName.startAnimation(translateAnimation);
    }

    private void endTranslateAnimation() {

        Animation userImageAnimation = AnimationUtils.loadAnimation(this, R.anim.user_image_end_anim);
        Animation oppUserImageAnimation = AnimationUtils.loadAnimation(this, R.anim.opp_user_image_anim);

        int userNameCenterX = userName.getWidth() / 2;
        TranslateAnimation userNameTranslateAnimation = new TranslateAnimation(280 - userNameCenterX, 8, 400, 1.8f);
        userNameTranslateAnimation.setDuration(1000);
        userNameTranslateAnimation.setFillAfter(true);

        int oppUserNameWidth = oppUserName.getWidth();

        TranslateAnimation oppUserNameTranslateAnimation = new TranslateAnimation(100, 300 - oppUserNameWidth, 0, -455);
        oppUserNameTranslateAnimation.setDuration(1000);
        oppUserNameTranslateAnimation.setFillAfter(true);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        oppUserName.startAnimation(oppUserNameTranslateAnimation);
        oppUserImage.startAnimation(oppUserImageAnimation);
        userName.startAnimation(userNameTranslateAnimation);
        userImage.startAnimation(userImageAnimation);
    }
}
