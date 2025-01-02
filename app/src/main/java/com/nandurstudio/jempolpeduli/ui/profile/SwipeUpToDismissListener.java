package com.nandurstudio.jempolpeduli.ui.profile;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class SwipeUpToDismissListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public SwipeUpToDismissListener(DialogFragment dialogFragment) {
        gestureDetector = new GestureDetector(dialogFragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getY() < e1.getY()) { // Swipe up
                    dialogFragment.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
