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
    private boolean mIsLoading, mFailed;

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
        onInit(context);;
    }

    public void onInit(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_viewloader, this, true);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mTextView = (TextView)findViewById(R.id.textView);

        setLoading();
    }

    public void setLoadedView(View v) {

        if (mAfterLoadView != null)
            removeView(mAfterLoadView);

        mAfterLoadView = v;

        if (v != null) {
            addView(v);
            v.setVisibility((mIsLoading || mFailed) ? INVISIBLE : VISIBLE);
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean hasFailed() {
        return !mIsLoading && mFailed;
    }

    private ViewPropertyAnimator fadeOut(View v, long duration) {
        ViewPropertyAnimator ret =  v.animate().alpha(0f).setDuration(duration);
        if (v == mProgressBar)
            ret.setListener( new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(GONE);
                }
            });
        return ret;
    }

    private ViewPropertyAnimator fadeIn(View v, long duration) {
        if (v == mProgressBar)
            mProgressBar.setVisibility(VISIBLE);
        return v.animate().alpha(1f).setDuration(duration).setListener(null);
    }

    private void setLoading() {
        mTextView.setText("LOADING...");
        mTextView.setTextColor(Color.WHITE);
        mTextView.clearAnimation();
        mTextView.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTextView.animate().alpha(1f).
                                setDuration(FADE_DURATION).
                                setListener(null);
                    }
                });
        mProgressBar.setVisibility(INVISIBLE);
        mProgressBar.animate().alpha(0f).setDuration(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(VISIBLE);
                mProgressBar.animate().alpha(1f).setDuration(FADE_DURATION)
                        .setListener(null);
            }
        });
        if (mAfterLoadView != null)
            mAfterLoadView.setVisibility(INVISIBLE);
        mIsLoading = true;
        mFailed = false;
    }

    private void toLoading() {
        if (mIsLoading)
            return;
        else if (mFailed) {
            mTextView.clearAnimation();
            mTextView.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setLoading();
                        }
                    });
        } else if (mAfterLoadView != null) {
            mAfterLoadView.clearAnimation();
            mAfterLoadView.animate().alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setLoading();
                        }
                    });
        }
        mIsLoading = true;
        mFailed = false;
    }

    private void setFailed() {
        mProgressBar.setVisibility(GONE);
        mTextView.setText("FAILED TO LOAD\nTouch to retry");
        mTextView.setTextColor(Color.rgb(180, 180, 195));
        mTextView.clearAnimation();
        mTextView.animate().alpha(0f).scaleX(1f).scaleY(1f).setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTextView.animate().alpha(1f).scaleX(1.4f).scaleY(1.4f).setDuration(FADE_DURATION)
                                .setListener(null);
                    }
                });

        if (mAfterLoadView != null)
            mAfterLoadView.setVisibility(INVISIBLE);

        mIsLoading = false;
        mFailed = true;
    }

    private void toFailed() {
        if (mIsLoading) {
            mTextView.clearAnimation();
            mTextView.animate().alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setFailed();
                        }
                    });
            mProgressBar.clearAnimation();
            mProgressBar.animate().alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.setVisibility(GONE);
                        }
                    });
        }

        mIsLoading = false;
        mFailed = true;
    }

    private void toView() {

        if (mIsLoading) {

            mTextView.clearAnimation();
            mTextView.animate().alpha(0f).setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAfterLoadView.animate().alpha(0f).setDuration(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            mAfterLoadView.setVisibility(VISIBLE);
                                            mAfterLoadView.animate().alpha(1f).setDuration(FADE_DURATION)
                                                    .setListener(null);
                                        }
                                    });
                        }
                    });
            mProgressBar.clearAnimation();
            mProgressBar.animate().alpha(0f).setDuration(FADE_DURATION).setListener(null);
            mAfterLoadView.setVisibility(INVISIBLE);
            mAfterLoadView.clearAnimation();

        } else if (mFailed) {

        }

        mIsLoading = false;
        mFailed = false;
    }

    public void setState(boolean loading, boolean failed) {

        if (loading) {
            if (!mIsLoading)
                toLoading();
        }
        else if (failed) {
            if (!mFailed)
                toFailed();
        }
        else
            toView();

        invalidate();
        requestLayout();
    }
}
