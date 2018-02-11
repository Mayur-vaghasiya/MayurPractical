/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.mayurpractical.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.mayurpractical.R;


public class CustomProgressDialog {

    // To avoid redundant APIs, all HUD functions will be forward to
    // a custom dialog
    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;
    private boolean mCancellable;
    private Context mContext;
    private int mAnimateSpeed;
    private String mLabel;
    private String mDetailsLabel;
    private int mMaxProgress;
    private boolean mIsAutoDismiss;

    public CustomProgressDialog(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mDimAmount = 0;
        //noinspection deprecation
        mWindowColor = context.getResources().getColor(R.color.custom_progress_default_color);
        //mWindowColor = context.getResources().getColor(R.color.kprogresshud_default_color);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = true;

        setStyle(Style.SPIN_INDETERMINATE);
    }

    /**
     * Create a new HUD. Have the same effect as the constructor.
     * For convenient only.
     *
     * @param context Activity context that the HUD bound to
     * @return An unique HUD instance
     */
    public static CustomProgressDialog create(Context context) {
        return new CustomProgressDialog(context);
    }

    /**
     * Specify the HUD style (not needed if you use a custom view)
     *
     * @param style One of the CustomProgressDialog.Style values
     * @return Current HUD
     */
    public CustomProgressDialog setStyle(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;

            // No custom view style here, because view will be added later
        }
        mProgressDialog.setView(view);
        return this;
    }

    /**
     * Specify the dim area around the HUD, like in Dialog
     *
     * @param dimAmount May take value from 0 to 1.
     *                  0 means no dimming, 1 mean darkness
     * @return Current HUD
     */
    public CustomProgressDialog setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }

    /**
     * Specify the HUD background color
     *
     * @param color ARGB color
     * @return Current HUD
     */
    public CustomProgressDialog setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    /**
     * Specify corner radius of the HUD (default is 10)
     *
     * @param radius Corner radius in dp
     * @return Current HUD
     */
    public CustomProgressDialog setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }

    /**
     * Change animate speed relative to default. Only have effect when use with indeterminate style
     *
     * @param scale 1 is default, 2 means double speed, 0.5 means half speed..etc.
     * @return Current HUD
     */
    public CustomProgressDialog setAnimationSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }

    /**
     * Optional label to be displayed on the HUD
     *
     * @return Current HUD
     */
    public CustomProgressDialog setLabel(String label) {
        mLabel = label;
        return this;
    }

    /**
     * Optional detail description to be displayed on the HUD
     *
     * @return Current HUD
     */
    public CustomProgressDialog setDetailsLabel(String detailsLabel) {
        mDetailsLabel = detailsLabel;
        return this;
    }

    /**
     * Max value for use in one of the determinate styles
     *
     * @return Current HUD
     */
    public CustomProgressDialog setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        return this;
    }

    /**
     * Set current progress. Only have effect when use with a determinate style, or a custom
     * view which implements Determinate interface.
     */
    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    /**
     * Provide a custom view to be displayed.
     *
     * @param view Must not be null
     * @return Current HUD
     */
    public CustomProgressDialog setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }

    /**
     * Specify whether this HUD can be cancelled by using back button (default is false)
     *
     * @return Current HUD
     */
    public CustomProgressDialog setCancellable(boolean isCancellable) {
        mCancellable = isCancellable;
        return this;
    }

    /**
     * Specify whether this HUD closes itself if progress reaches max. Default is true.
     *
     * @return Current HUD
     */
    public CustomProgressDialog setAutoDismiss(boolean isAutoDismiss) {
        mIsAutoDismiss = isAutoDismiss;
        return this;
    }

    public CustomProgressDialog show() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        return this;
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public enum Style {
        SPIN_INDETERMINATE, PIE_DETERMINATE, ANNULAR_DETERMINATE, BAR_DETERMINATE
    }

    private class ProgressDialog extends Dialog {

        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private View mView;

        public ProgressDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_progress_dialog_layout);

            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            window.setAttributes(layoutParams);

            setCanceledOnTouchOutside(false);
            setCancelable(mCancellable);

            initViews();
        }

        private void initViews() {
            BackgroundLayout background = (BackgroundLayout) findViewById(R.id.background);
            background.setBaseColor(mWindowColor);
            background.setCornerRadius(mCornerRadius);

            FrameLayout containerFrame = (FrameLayout) findViewById(R.id.container);
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
            containerFrame.addView(mView, params);

            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }

            if (mLabel != null) {
                AppCompatTextView labelText = (AppCompatTextView) findViewById(R.id.actv_label);
                labelText.setText(mLabel);
                labelText.setVisibility(View.VISIBLE);
            }
            if (mDetailsLabel != null) {
                AppCompatTextView detailsText = (AppCompatTextView) findViewById(R.id.
                        actv_details_label);
                detailsText.setText(mDetailsLabel);
                detailsText.setVisibility(View.VISIBLE);
            }
        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }

        public void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                mView = view;
            }
        }
    }
}
