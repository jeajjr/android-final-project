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

    private static final int FADE_DURATION = 500;

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

        mIsLoading = false;;
        mFailed = false;

        setState(true, false);
    }

    public void setLoadedView(View v) {

        if (mAfterLoadView != null)
            removeView(mAfterLoadView);

        mAfterLoadView = v;

        if (v != null) {
            addView(v);
            v.setVisibility(VISIBLE);
        }

        setState(mIsLoading, mFailed);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean hasFailed() {
        return !mIsLoading && mFailed;
    }

    public void setState(boolean loading, boolean failed) {

        if (!(mIsLoading || mFailed) && mAfterLoadView != null) {
            mAfterLoadView.setVisibility(INVISIBLE);
        }

        if (loading) {

            AnimatorListenerAdapter after = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mTextView.setVisibility(VISIBLE);
                    mProgressBar.setVisibility(VISIBLE);
                    mProgressBar.animate().alpha(1f).setDuration(FADE_DURATION).setListener(null);

                    mTextView.setText("LOADING...");
                    mTextView.setTextColor(Color.WHITE);
                    mTextView.animate().scaleX(1f).scaleY(1f).setDuration(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTextView.animate().alpha(1f).setDuration(FADE_DURATION);
                        }
                    });
                }
            };

            if (mFailed) {
                mTextView.animate().alpha(0f).setDuration(FADE_DURATION).setListener(after);
            } else if (!mIsLoading) {
                mTextView.animate().alpha(0f).setDuration(0).setListener(null);
                mProgressBar.animate().alpha(0f).setDuration(0).setListener(null);

                if (mAfterLoadView != null) {
                    mAfterLoadView.animate().alpha(0f).setDuration(FADE_DURATION).setListener(after);
                } else {
                    after.onAnimationEnd(null);
                }
            } else if (mAfterLoadView != null)
                mAfterLoadView.setVisibility(INVISIBLE);

        } else if (failed) {

            mTextView.animate()
                    .alpha(0f)
                    .setDuration(FADE_DURATION)
                    .setListener(null);

            mProgressBar.animate()
                    .alpha(0f)
                    .setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.setVisibility(GONE);
                            mTextView.setText("FAILED TO LOAD\nTouch to retry");
                            mTextView.setTextColor(Color.rgb(180, 180, 195));

                            float size = 1.5f;

                            mTextView.animate()
                                    .scaleX(size).scaleY(size)
                                    .alpha(1f)
                                    .setDuration(FADE_DURATION);
                        }
                    });

            if (mAfterLoadView != null)
                mAfterLoadView.setVisibility(INVISIBLE);

        } else {

            mTextView.animate()
                    .alpha(0f)
                    .setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTextView.setVisibility(GONE);
                        }
                    });

            mProgressBar.animate()
                    .alpha(0f)
                    .setDuration(FADE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.setVisibility(GONE);
                        }
                    });

            if (mAfterLoadView != null) {
                mAfterLoadView.setVisibility(VISIBLE);
                mAfterLoadView.setAlpha(0f);
                mAfterLoadView.animate()
                        .alpha(1f)
                        .setDuration(FADE_DURATION)
                        .setListener(null);
            }
        }

        mIsLoading = loading;
        mFailed = failed;

        invalidate();
        requestLayout();
    }
}
