package com.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;


public class ColorImageView extends AppCompatImageView {

    private final Paint paint = new Paint();

    private int width;
    private float radius;
    private int startColor;
    private int endColor;

    public ColorImageView(Context context) {
        this(context, null);
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColor(context, attrs);
        post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    private void initColor(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorImageView);
        startColor = a.getColor(R.styleable.ColorImageView_startColor, Color.RED);
        endColor = a.getColor(R.styleable.ColorImageView_endColor, 0);
        a.recycle();
    }

    private void init() {
        radius = width / 2f;

        RadialGradient rg = new RadialGradient(
                radius,
                radius,
                radius,
                startColor, endColor, Shader.TileMode.CLAMP
        );
        paint.setShader(rg);

        paint.setAntiAlias(true);
    }

    public void setStartColor(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        RadialGradient rg = new RadialGradient(
                radius,
                radius,
                radius,
                startColor, endColor, Shader.TileMode.CLAMP
        );
        paint.setShader(rg);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        int h = width;
        setMeasuredDimension(width, h);
        if (BuildConfig.DEBUG){
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(radius, radius, radius, paint);
        super.onDraw(canvas);
    }
}
