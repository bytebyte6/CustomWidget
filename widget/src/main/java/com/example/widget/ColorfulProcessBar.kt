package com.example.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap

class ColorfulProcessBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mMax = 100f
    private var mProcessBarHeight = dipToPx(15f)
    private val mProgressAnimator = ValueAnimator.ofFloat(0f, mMax)
    private var mCurrentProcess = 0f
    private var mIndicatorHeight = 0f
    private var mIndicatorWidth = 0f
    private var mThreeColorPaint = Paint()
    private var mWidth = 0f
    private var mHeight = 0f
    private var mColor = intArrayOf(Color.RED, Color.BLACK, Color.BLUE)
    private val mProcessRectF = RectF()
    private var mCornerSize = dipToPx(5f)
    private var bitmapTop = 0f
    private var mIndicatorBitmap: Bitmap? = null
    private var mIndicatorDrawable: Drawable? = null

    init {
        init(attrs)
    }

    fun setColor(color: IntArray) {
        mColor = color
        val linearGradient = LinearGradient(
            0f, 0f, mWidth, 0f, mColor, null, Shader.TileMode.CLAMP
        )
        mThreeColorPaint.shader = linearGradient
    }

    fun setIndicatorColor(color: Int) {
        mIndicatorDrawable?.setTint(color)
        mIndicatorBitmap = mIndicatorDrawable?.toBitmap()
    }

    fun setProcess(process: Float) {
        setAnimation(process)
    }

    private fun setAnimation(process: Float) {
        mProgressAnimator.cancel()
        mProgressAnimator.setFloatValues(process)
        mProgressAnimator.duration = 1000
        mProgressAnimator.setTarget(mCurrentProcess)
        mProgressAnimator.addUpdateListener { animation ->
            mCurrentProcess = animation.animatedValue as Float
            invalidate()
        }
        mProgressAnimator.start()
    }

    private fun init(attrs: AttributeSet?) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorfulProcessBar)
        mMax = a.getFloat(R.styleable.ColorfulProcessBar_max, mMax)
        mCornerSize = a.getDimension(R.styleable.ColorfulProcessBar_corner, mCornerSize)
        mProcessBarHeight = a.getDimension(R.styleable.ColorfulProcessBar_barHeight, mProcessBarHeight)
        a.recycle()

        mIndicatorDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indicate)
        mIndicatorBitmap = mIndicatorDrawable?.toBitmap()
        if (mIndicatorBitmap != null) {
            mIndicatorWidth = mIndicatorBitmap!!.width.toFloat()
            mIndicatorHeight = mIndicatorBitmap!!.height.toFloat()
        }

        mThreeColorPaint.isAntiAlias = true
        mThreeColorPaint.strokeCap = Paint.Cap.ROUND

        mProcessRectF.left = mIndicatorWidth / 2
        mProcessRectF.top = mProcessRectF.left
        mProcessRectF.bottom = mProcessRectF.top + mProcessBarHeight

        bitmapTop = mProcessBarHeight + 10

        post {
            initShader()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = mProcessBarHeight + mIndicatorHeight
        setMeasuredDimension(width, height.toInt())
        mWidth = width.toFloat()
        mHeight = height
        if (BuildConfig.DEBUG) {
            initShader()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(mProcessRectF, mCornerSize, mCornerSize, mThreeColorPaint)
        mIndicatorBitmap?.let {
            val left = mCurrentProcess / mMax * (mWidth - mIndicatorWidth)
            canvas.drawBitmap(it, left, bitmapTop, null)
        }
    }

    private fun initShader() {
        mThreeColorPaint.shader = LinearGradient(
            0f, 0f, mWidth, 0f, mColor, null, Shader.TileMode.CLAMP
        )
        mProcessRectF.right = mWidth - mIndicatorWidth / 2
    }

    private fun dipToPx(dip: Float): Float {
        val density = context.resources.displayMetrics.density
        return (dip * density + 0.5f * if (dip >= 0) 1 else -1)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mProgressAnimator.cancel()
        mIndicatorBitmap?.recycle()
    }
}