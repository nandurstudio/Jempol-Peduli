package com.nandurstudio.jempolpeduli.ui.campaign;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nandurstudio.jempolpeduli.R;

public class CustomRecyclerView extends RecyclerView {

    private boolean isDragging = false;
    private Drawable normalThumbDrawable;
    private Drawable enlargedThumbDrawable;

    public CustomRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Load drawable resources for normal and enlarged scrollbar
        normalThumbDrawable = ContextCompat.getDrawable(context, R.drawable.custom_scrollbar_thumb);
        enlargedThumbDrawable = ContextCompat.getDrawable(context, R.drawable.custom_scrollbar_thumb_large);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            if (isTouchOnScrollbar(e)) {
                isDragging = true;
                invalidate(); // Trigger redraw
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            isDragging = false;
            invalidate(); // Trigger redraw
        }
        return super.onInterceptTouchEvent(e);
    }

    private boolean isTouchOnScrollbar(MotionEvent e) {
        // Check if touch is on the vertical scrollbar area
        int scrollBarWidth = getVerticalScrollbarWidth();
        return e.getX() > getWidth() - scrollBarWidth;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Set the appropriate scrollbar drawable
        if (isDragging) {
            setVerticalThumbDrawable(enlargedThumbDrawable);
        } else {
            setVerticalThumbDrawable(normalThumbDrawable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setVerticalThumbDrawable(Drawable drawable) {
        if (drawable != null) {
            setVerticalScrollbarThumbDrawable(drawable);
        }
    }
}


