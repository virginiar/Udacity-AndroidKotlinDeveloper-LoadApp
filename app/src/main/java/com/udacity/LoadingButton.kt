package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonText = resources.getString(R.string.button_name)
    private var loadingWidth = 0f
    private var loadingAngle = 0f
    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
                invalidate()
            }
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.apply {
                    setFloatValues(0f, 1f)
                    duration = 5000L
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    interpolator = LinearInterpolator()
                    addUpdateListener { valueAnimator ->
                        loadingWidth = widthSize * (valueAnimator.animatedValue as Float)
                        loadingAngle = 360f * (valueAnimator.animatedValue as Float)
                        invalidate()
                    }
                }
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.end()
                invalidate()
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    private var backgroundStartColor = 0
    private var backgroundActionColor = 0
    private var buttonTextColor = 0
    private var circleColor = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundStartColor = getColor(R.styleable.LoadingButton_backgroundDefaultColor, 0)
            backgroundActionColor = getColor(R.styleable.LoadingButton_backgroundLoadingColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        isClickable = true
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = backgroundStartColor
        canvas?.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(), 0f, paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = backgroundActionColor
            canvas?.drawRect(0f, heightSize.toFloat(), loadingWidth, 0f, paint)
            paint.color = circleColor
            //canvas?.drawCircle(widthSize.toFloat()/4*3, heightSize.toFloat()/2, heightSize.toFloat()/8, paint)
            canvas?.drawArc(
                widthSize.toFloat()/4*3,
                heightSize.toFloat()/4,
                widthSize.toFloat()*3/4 + heightSize.toFloat()/2,
                heightSize.toFloat()*3/4,
                0f,
                loadingAngle,
                true,
                paint
            )

        }

        paint.color = buttonTextColor
        canvas?.drawText(buttonText,
            widthSize.toFloat() / 2f,
            heightSize.toFloat() / 2f  + (marginTop + marginBottom) / 8f,
            paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setStateCompleted() {
        buttonState = ButtonState.Completed
    }
}