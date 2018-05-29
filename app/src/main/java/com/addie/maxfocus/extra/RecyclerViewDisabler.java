package com.addie.maxfocus.extra;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Disables all touches for a RecyclerView
 */
public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}