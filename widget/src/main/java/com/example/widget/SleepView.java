package com.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SleepView extends View {

    public static int IGNORE = 10;

    public enum Type {
        DEEP(5), WEAK(14), WAKE(15);

        int quality;
        Paint paint;

        Type(int quality) {
            this.quality = quality;
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    private float mWidth;

    private float mHeight;

    private float mTopHeight;

    private float mRound;

    private final RectF mDeepRectF;

    private final Work work;

    private final List<RectF> mSleepRectFs;

    private final ExecutorService service;

    public SleepView(Context context) {
        this(context, null);
    }

    public SleepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTopHeight = dipToPx(20);
        mRound = dipToPx(5);
        mDeepRectF = new RectF();
        service = Executors.newSingleThreadExecutor();
        mSleepRectFs = new ArrayList<>();
        work = new Work();
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int wakeColor;
        int weakColor;
        int deepColor;

        wakeColor = ContextCompat.getColor(getContext(), R.color.wake_color);
        weakColor = ContextCompat.getColor(getContext(), R.color.weak_color);
        deepColor = ContextCompat.getColor(getContext(), R.color.deep_color);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SleepView, 0, 0);

        wakeColor = array.getColor(R.styleable.SleepView_wake_color, wakeColor);
        weakColor = array.getColor(R.styleable.SleepView_weak_color, weakColor);
        deepColor = array.getColor(R.styleable.SleepView_deep_color, deepColor);

        array.recycle();

        Type.WAKE.paint.setColor(wakeColor);
        Type.DEEP.paint.setColor(deepColor);
        Type.WEAK.paint.setColor(weakColor);

        post(new Runnable() {
            @Override
            public void run() {
                initDeepRect();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(mDeepRectF, mRound, mRound, Type.DEEP.paint);

        for (RectF mSleepRectF : mSleepRectFs) {
            SleepRectF sleepRectF = (SleepRectF) mSleepRectF;
            if (sleepRectF.right > mWidth)
                sleepRectF.right = mWidth;
            canvas.drawRoundRect(sleepRectF, mRound, mRound, sleepRectF.mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            mHeight = dipToPx(80);
        }
        setMeasuredDimension((int) mWidth, (int) mHeight);
        if (BuildConfig.DEBUG) {
            initDeepRect();
        }
    }

    private void initDeepRect() {
        mDeepRectF.left = 0;
        mDeepRectF.top = mTopHeight;
        mDeepRectF.right = mWidth;
        mDeepRectF.bottom = mHeight;
    }

    private float dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public void addRectF(List<SleepData> data) {
        work.data = data;
        if (mWidth == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    service.execute(work);
                }
            });
        } else {
            service.execute(work);
        }
    }

    private void addDeepRectF(float width) {
        int size = mSleepRectFs.size();

        if (size == 0) {
            addSleepRectF(width, mTopHeight, null, Type.DEEP.paint);
        } else {
            SleepRectF last = (SleepRectF) mSleepRectFs.get(size - 1);
            if (last.mPaint == Type.DEEP.paint) {
                last.right += width;
            } else {
                addSleepRectF(width, mTopHeight, last, Type.DEEP.paint);
            }
        }
    }

    private void addWeakRectF(float width) {
        int size = mSleepRectFs.size();

        if (size == 0) {
            addSleepRectF(width, 0, null, Type.WEAK.paint);
        } else {
            SleepRectF last = (SleepRectF) mSleepRectFs.get(size - 1);
            if (last.mPaint == Type.WEAK.paint) {
                last.right += width;
            } else {
                addSleepRectF(width, 0, last, Type.WEAK.paint);
            }
        }
    }

    private void addWakeRectF(float width) {
        int size = mSleepRectFs.size();

        if (size == 0) {
            addSleepRectF(width, 0, null, Type.WAKE.paint);
        } else {
            SleepRectF last = (SleepRectF) mSleepRectFs.get(size - 1);
            if (last.mPaint == Type.WAKE.paint) {
                last.right += width;
            } else {
                addSleepRectF(width, 0, last, Type.WAKE.paint);
            }
        }
    }

    private void addSleepRectF(float width, float top, SleepRectF last, Paint paint) {
        SleepRectF rectF = new SleepRectF();
        rectF.mPaint = paint;
        if (last != null) {
            rectF.left = last.right;
        } else {
            rectF.left = 0;
        }
        rectF.top = top;
        rectF.right = rectF.left + width;
        rectF.bottom = mHeight;
        mSleepRectFs.add(rectF);
    }

    public void setTopHeight(float mTopHeight) {
        this.mTopHeight = mTopHeight;
    }

    public void setRound(float mRound) {
        this.mRound = mRound;
    }

    class Work implements Runnable {

        List<SleepData> data;

        @Override
        public void run() {
            mSleepRectFs.clear();
            int total = 0;
            for (SleepData data : data) {
                List<Integer> ds = data.getDurations();
                for (Integer d : ds) {
                    total += d;
                }
            }
            for (SleepData data : data) {
                List<Integer> ds = data.getDurations();
                List<Integer> qs = data.getQualitys();
                int dSize = ds.size();
                int qSize = qs.size();
                if (qSize < dSize) {
                    dSize = qSize;
                }
                for (int i = 0; i < dSize; i++) {
                    float sleepTime = ds.get(i);
                    if (sleepTime <= IGNORE) {
                        continue;
                    }
                    int quality = qs.get(i);
                    float p = sleepTime / total;
                    float width = (mWidth * p);
                    if (quality <= Type.DEEP.quality) {
                        addDeepRectF(width);
                    } else if (quality <= Type.WEAK.quality) {
                        addWeakRectF(width);
                    } else {
                        addWakeRectF(width);
                    }
                }
            }
            postInvalidate();
        }
    }
}
