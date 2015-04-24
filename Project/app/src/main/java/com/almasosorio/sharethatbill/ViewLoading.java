package com.almasosorio.sharethatbill;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by osorio.bruno on 23/04/2015.
 */
public class ViewLoading extends RelativeLayout {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private View mAfterLoadView;
    private boolean mIsLoading, mFailed, mFading;

    private static final int FADE_DURATION = 620;

    public ViewLoading(Context context) {
        super(context);
        onInit(context);
    }

    public ViewLoading(Context context, AttributeSet attr) {
        super(context, attr);
        onInit(context);
    }

    public ViewLoading(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        onInit(context);
    }

    public void onInit(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_viewloader, this, true);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mTextView = (TextView)findViewById(R.id.textView);

        mFading = false;
        setLoading();
    }

    public void setLoadedView(View v) {

        if (mAfterLoadView != null)
            removeView(mAfterLoadView);

        mAfterLoadView = v;

        if (v != null) {
            addView(v);
            if (mIsLoading || mFailed) {
                mAfterLoadView.setVisibility(INVISIBLE);
                mAfterLoadView.animate().alpha(0f).setDuration(0).setListener(null);
            } else {
                mAfterLoadView.setVisibility(VISIBLE);
                mAfterLoadView.animate().alpha(1f).setDuration(0).setListener(null);
            }
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean hasFailed() {
        return !mIsLoading && mFailed;
    }

    private void setLoading() {

        mFading = false;
        mTextView.setText("LOADING...");
        mTextView.setTextColor(Color.WHITE);
        mTextView.clearAnimation();
        mTextView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0).setListener(null);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.animate().alpha(1f).setDuration(0).setListener(null);

        if (mAfterLoadView != null) {
            mAfterLoadView.setVisibility(INVISIBLE);
            mAfterLoadView.animate().alpha(0f).setDuration(0).setListener(null);
        }

        mIsLoading = true;
        mFailed = false;
    }

    private void fadeOutLoad(Animator.AnimatorListener listener) {

        Log.d("ViewLoading.java - Fading", "FADE OUT LOAD - " + this.hashCode());

        mFading = true;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();
        if (mAfterLoadView != null) {
            mAfterLoadView.clearAnimation();
        }

        mProgressBar.animate().alpha(0f).setDuration(FADE_DURATION)
                .setListener(null);
        mTextView.animate().alpha(0f).setDuration(FADE_DURATION).setListener(listener);

    }

    private void fadeOutFail(Animator.AnimatorListener listener) {

        mFading = true;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();
        if (mAfterLoadView != null)
            mAfterLoadView.clearAnimation();

        mTextView.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(FADE_DURATION)
                .setListener(listener);
    }

    private void fadeOutView(final Animator.AnimatorListener listener) {

        mFading = true;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();

        Log.d("ViewLoading.java - Fading", "FADE OUT VIEW - " + this.hashCode());

        if (mAfterLoadView != null) {
            mAfterLoadView.clearAnimation();
            mAfterLoadView.animate().alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAfterLoadView.setVisibility(INVISIBLE);
                            listener.onAnimationEnd(animation);
                        }
                    });
        }
    }

    private void fadeInLoad() {

        Log.d("ViewLoading.java - Fading", "FADE IN LOAD - " + this.hashCode());

        mIsLoading = true;
        mFailed = false;
        mFading = false;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();
        if (mAfterLoadView != null)
            mAfterLoadView.clearAnimation();

        mTextView.setText("LOADING...");
        mTextView.setTextColor(Color.WHITE);
        mTextView.animate().alpha(1f).setDuration(FADE_DURATION).setListener(null);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.animate().alpha(1f).setDuration(FADE_DURATION).setListener(null);

        if (mAfterLoadView != null)
            mAfterLoadView.setVisibility(INVISIBLE);

        mIsLoading = true;
        mFailed = false;

    }

    private void fadeInFail() {

        mIsLoading = false;
        mFailed = true;
        mFading = false;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();

        if (mAfterLoadView != null)
            mAfterLoadView.clearAnimation();

        mProgressBar.setVisibility(GONE);
        mTextView.setText("FAILED TO LOAD\nTouch to retry");
        mTextView.setTextColor(Color.rgb(180, 180, 195));
        mTextView.animate().alpha(1f).scaleX(1.4f).scaleY(1.4f)
                .setDuration(FADE_DURATION).setListener(null);
    }

    private void fadeInView() {

        Log.d("ViewLoading.java - Fading", "FADE IN VIEW - " + this.hashCode());

        mIsLoading = false;
        mFailed = false;
        mFading = false;
        mTextView.clearAnimation();
        mProgressBar.clearAnimation();
        if (mAfterLoadView != null) {
            mAfterLoadView.clearAnimation();
            mAfterLoadView.setVisibility(VISIBLE);
            mAfterLoadView.animate().alpha(1f).setDuration(FADE_DURATION).setListener(null);
        }
    }

    public void setState(boolean loading, boolean failed) {

        Log.d("ViewLoading.java - setState", Boolean.valueOf(loading) + "/" + Boolean.valueOf(failed));

        if (mFading)
            return;

        if (loading) {

            if (mIsLoading)
                return;

            if (mFailed) {
                fadeOutFail(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInLoad();
                    }
                });
            } else {
                fadeOutView(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInLoad();
                    }
                });
            }

        } else if (failed) {

            if (mFailed)
                return;

            if (mIsLoading) {
                fadeOutLoad(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInFail();
                    }
                });
            } else {
                fadeOutView(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInFail();
                    }
                });
            }
        }
        else {
            if (mFailed) {
                fadeOutFail(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInView();
                    }
                });
            } else if (mIsLoading) {
                fadeOutLoad(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInView();
                    }
                });
            }
        }
    }
}
