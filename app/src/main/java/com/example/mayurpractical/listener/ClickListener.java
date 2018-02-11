package com.example.mayurpractical.listener;

import android.view.View;

/**
 * Created by Shreenathji on 10-02-2018.
 */

public interface ClickListener {
    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}