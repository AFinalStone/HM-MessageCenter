package com.hm.iou.msg.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hm.iou.tools.DensityUtil;

/**
 * @author hjy
 */
public class SideLetterBar extends View {

    private static final String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private static final int SHOW_OVERLAY_INDEX = 0;

    private int mSelectedIndex = -1;
    private Paint mPaint = new Paint();
    private boolean mShowBg = false;
    private TextView mOverlayTextView;

    private OnLetterChangedListener onLetterChangedListener;

    public SideLetterBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideLetterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideLetterBar(Context context) {
        super(context);
    }

    /**
     * 设置悬浮的TextView
     *
     * @param overlayTextView
     */
    public void setOverlay(TextView overlayTextView) {
        this.mOverlayTextView = overlayTextView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowBg) {
            canvas.drawColor(Color.TRANSPARENT);
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            mPaint.setTextSize(DensityUtil.dip2px(getContext(), 10));
            mPaint.setColor(0xff9b9b9b);
            mPaint.setAntiAlias(true);
            if (i == mSelectedIndex) {
                mPaint.setColor(0xff666666);
            }
            float xPos = width / 2 - mPaint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, mPaint);
            mPaint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mSelectedIndex;
        final OnLetterChangedListener listener = onLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShowBg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onLetterChanged(b[c]);
                        mSelectedIndex = c;
                        invalidate();
                        if (c >= SHOW_OVERLAY_INDEX) {
                            if (mOverlayTextView != null) {
                                mOverlayTextView.setVisibility(VISIBLE);
                                mOverlayTextView.setText(b[c]);
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onLetterChanged(b[c]);
                        mSelectedIndex = c;
                        invalidate();
                        if (c >= SHOW_OVERLAY_INDEX) {
                            if (mOverlayTextView != null) {
                                mOverlayTextView.setVisibility(VISIBLE);
                                mOverlayTextView.setText(b[c]);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mShowBg = false;
                mSelectedIndex = -1;
                invalidate();
                if (mOverlayTextView != null) {
                    mOverlayTextView.setVisibility(GONE);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface OnLetterChangedListener {
        void onLetterChanged(String letter);
    }

}
