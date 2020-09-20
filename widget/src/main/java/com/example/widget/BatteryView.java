package com.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.FloatRange;

public class BatteryView extends View {

    private int batteryMiddleColor;
    private int batteryLowColor;
    private int batteryHighColor;
    private int outSideColor;
    private int mMargin = 5;    //电池内芯与边框的距离
    private int mBorderWidth = 4;    //电池外框的宽度
    private int mWidth = 80;    //总长
    private int mHeight = 40;   //总高
    private int mHeadWidth = 6;
    private int mHeadHeight = 10;
    private float mCorner = 4f;   //圆角
    private float mHeadCorner = 4f;   //圆角
    private float mPower;

    private final Paint inSlidePaint = new Paint();
    private final Rect powerRect = new Rect();
    private final Paint outSidePaint = new Paint();

    private RectF mMainRect;
    private RectF mHeadRect;

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public BatteryView(Context context) {
        super(context);
        initView(null);
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryView, 0, 0);
        mMargin = (int) a.getDimension(R.styleable.BatteryView_margin, mMargin);
        mBorderWidth = (int) a.getDimension(R.styleable.BatteryView_borderWidth, mBorderWidth);
        mWidth = (int) a.getDimension(R.styleable.BatteryView_width, mWidth);
        mHeight = (int) a.getDimension(R.styleable.BatteryView_height, mHeight);
        mHeadWidth = (int) a.getDimension(R.styleable.BatteryView_headWidth, mHeadWidth);
        mHeadHeight = (int) a.getDimension(R.styleable.BatteryView_headHeight, mHeadHeight);
        mCorner = (int) a.getDimension(R.styleable.BatteryView_corner, mCorner);
        mHeadCorner = (int) a.getDimension(R.styleable.BatteryView_headCorner, mHeadCorner);
        outSideColor = a.getColor(R.styleable.BatteryView_outSide_color, 0);
        batteryLowColor = a.getColor(R.styleable.BatteryView_battery_low_color, 0);
        batteryMiddleColor = a.getColor(R.styleable.BatteryView_battery_middle_color, 0);
        batteryHighColor = a.getColor(R.styleable.BatteryView_battery_high_color, 0);
        mPower = a.getFloat(R.styleable.BatteryView_power, 0);
        a.recycle();
        mHeadRect = new RectF(0, (mHeight - mHeadHeight) / 2f, mHeadWidth, (mHeight + mHeadHeight) / 2f);
        float left = mHeadRect.width();
        float top = mBorderWidth;
        float right = mWidth - mBorderWidth;
        float bottom = mHeight - mBorderWidth;
        mMainRect = new RectF(left, top, right, bottom);
        initPowerRect();
    }

    private void initPowerRect() {
        int width = (int) (mPower * (mMainRect.width() - mMargin * 2));
        int left = (int) (mMainRect.right - mMargin - width);
        int right = (int) (mMainRect.right - mMargin);
        int top = (int) (mMainRect.top + mMargin);
        int bottom = (int) (mMainRect.bottom - mMargin);
        powerRect.left = left;
        powerRect.top = top;
        powerRect.bottom = bottom;
        powerRect.right = right;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画电池头
        outSidePaint.setStyle(Paint.Style.FILL);
        outSidePaint.setColor(batteryHighColor);
        canvas.drawRoundRect(mHeadRect.left, mHeadRect.top, mHeadRect.right, mHeadRect.bottom, mHeadCorner, mHeadCorner, outSidePaint);

        //画外框
        outSidePaint.setStyle(Paint.Style.STROKE);    //设置空心矩形
        outSidePaint.setStrokeWidth(mBorderWidth);    //设置边框宽度
        outSidePaint.setColor(outSideColor);
        canvas.drawRoundRect(mMainRect, mCorner, mCorner, outSidePaint);

        //画电池芯
        if (mPower < 0.2) {
            inSlidePaint.setColor(batteryLowColor);
        } else if (mPower < 0.5) {
            inSlidePaint.setColor(batteryMiddleColor);
        } else {
            inSlidePaint.setColor(batteryHighColor);
        }

        int width = (int) (mPower * (mMainRect.width() - mMargin * 2));
        powerRect.left = (int) (mMainRect.right - mMargin - width);

        canvas.drawRect(powerRect, inSlidePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    public void setPower(@FloatRange(from = 0, to = 1.0) float power) {
        mPower = power;
        invalidate();
    }
}
