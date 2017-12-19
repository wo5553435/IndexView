package com.git.indexnum

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by sinner on 2017-12-08.
 * mail: wo5553435@163.com
 * github: https://github.com/wo5553435
 */
class IndexNumView:View{

    private lateinit var mContext: Context
    private var mTextSize = 20
    private var mText = "0"
    private var mDuration = 200;
    private var mCurrrentMode = 1;//默认
    private var mTextColor = 0;
    //wrap模式下的自定义宽高
    private lateinit var mBound: Rect
    private var inputText = ""//将要书写的文字
    private var lasttextwidth = 0f //上一次保存的文字大小
    private var lasttextheight = 0f;
    private lateinit var textPaint: Paint
    private var orientation = 0;// 0 vertical 1 horizontal 动画的方向  0 代表
    private var textwidth = 0f
    private var textheight = 0f
    private var startwidth = 0f
    private var startheight = 0f
    private var textoffset = 20
    private var reverse = false;//是否让动画相反
    val scale = context.resources.displayMetrics.density

    constructor(context: Context) : super(context) {
        mContext = context;
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context;
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defstyl: Int = 0) : super(context, attrs, defstyl) {
        mContext = context;
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.apply {
            val a = mContext.obtainStyledAttributes(attrs, R.styleable.IndexNumView)
            mText=a.getString(R.styleable.IndexNumView_in_text)?:"0"
            mTextSize = a.getInteger(R.styleable.IndexNumView_in_textsize, 14)
            mTextColor = a.getColor(R.styleable.IndexNumView_in_textColor, Color.BLACK)
            orientation = a.getInt(R.styleable.IndexNumView_in_orientation, 0)
            mDuration=a.getInteger(R.styleable.IndexNumView_in_duration,200)
            textoffset=a.getInteger(R.styleable.IndexNumView_in_offset,20)
            reverse = a.getBoolean(R.styleable.IndexNumView_in_reverse, false);
        }

        mBound = Rect()
        textPaint = Paint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isAntiAlias = true
        inputText = mText
        textPaint.textSize = dip2px(mTextSize.toFloat())
        textPaint.color = mTextColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL

    }

    public fun setText(text:String){
        this.mText=text;
        inputText=text;
        requestLayout()
    }

    public fun  setDuration(duration:Int){
        this.mDuration=duration
    }

    public fun setTextSize(textsize:Int){
        this.mTextSize=textsize
    }

    public fun setOffset(offset:Int){
        this.textoffset=offset
    }

    public  fun setReverse(isreverse:Boolean){
        this.reverse=reverse
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureText()
        val measureWidth = measureWidth(widthMeasureSpec)
        val measureHeight = measureHeight(heightMeasureSpec)
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight)
    }

    private fun measureText() {
        mBound?.setEmpty()
        textPaint.getTextBounds(inputText, 0, inputText.length, mBound)
        if (mBound.width().toFloat() > lasttextwidth)//当前的文字宽度比上一次比较
            textwidth = mBound.width().toFloat()
        if (mBound.height().toFloat() > lasttextheight)
            textheight = mBound.height().toFloat()
        startwidth = (width / 2).toFloat()/*- (mBound.width() / 2)*/
        startheight = (height / 2).toFloat()
    }


    private fun measureWidth(pWidthMeasureSpec: Int): Int {
        var result = 0
        val widthMode = View.MeasureSpec.getMode(pWidthMeasureSpec)// 得到模式
        val widthSize = View.MeasureSpec.getSize(pWidthMeasureSpec)// 得到尺寸

        when (widthMode) {
        /**
         * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
         * MeasureSpec.AT_MOST。
         *
         *
         * MeasureSpec.EXACTLY是精确尺寸，
         * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
         * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
         *
         *
         * MeasureSpec.AT_MOST是最大尺寸，
         * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
         * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
         * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
         *
         *
         * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
         * 通过measure方法传入的模式。
         */
            View.MeasureSpec.AT_MOST//自适应下的宽度判断 在重进入下一次动画会重构
            -> {
                result = textwidth.toInt() + textoffset
                mCurrrentMode = 2
            }

            View.MeasureSpec.EXACTLY -> result = widthSize
            View.MeasureSpec.UNSPECIFIED -> {
            }
        }
        return result
    }

    private fun measureHeight(pHeightMeasureSpec: Int): Int {
        var result = 0
        val heightMode = View.MeasureSpec.getMode(pHeightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(pHeightMeasureSpec)

        when (heightMode) {
            View.MeasureSpec.AT_MOST -> result = textheight.toInt() + textoffset / 2//偏移量我写死了
            View.MeasureSpec.EXACTLY -> result = heightSize
        }
        return result
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(dpValue: Float): Float {

        return (dpValue * scale + 0.5f)
    }


    override fun onDraw(canvas: Canvas) {
        // measureText()//根据当前模式确定需要输入的内容
        canvas.drawText(inputText, startwidth, startheight + mBound.height() / 2, textPaint)
    }


    fun changeText(str: String) {
        ShowText(str)
    }


    /**
     * 展示动画
     */
    private fun ShowText(nextstr: String) {
        var endcount:Float=if(orientation==0) {
            -(2*textheight/height)
            //-0.1f
        } else {
            if(!reverse)
                -0.1f
            else -(2*textwidth)/width
        }

        var va = ValueAnimator.ofFloat(1f, endcount)
        va.duration = (mDuration / 2).toLong()
        va.interpolator = LinearInterpolator()
        va.addUpdateListener { animation ->
            //            Log.e("end--"+endcount,"float:"+(animation.animatedValue as Float ))
            when (orientation) {
                0 -> startheight = if (!reverse) (animation.animatedValue as Float) * (height / 2) else (height / 2) + (1 - (animation.animatedValue as Float)) * (height / 2)
                1 -> startwidth = if (!reverse) /*(animation.animatedFraction)*/(1 - (animation.animatedValue as Float)) * (width / 2) + (width / 2)
                else (width / 2) - (1 - (animation.animatedValue as Float)) * (width / 2)
            }
            Log.e("startwidth"+startwidth,"width"+width)
            invalidate()
        }
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                inputText = nextstr
                mText = inputText
                lasttextwidth = textwidth + textoffset
                lasttextheight = textheight + textoffset / 2
                QuitText((mCurrrentMode == 2&&isNeedMeasure()))
                //当发现自适应内容且超过上次边界时候 强制刷新下一次绘图计算边界
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        va.start()


    }


    private fun isNeedMeasure():Boolean{
        mBound?.setEmpty()
        textPaint.getTextBounds(inputText, 0, inputText.length, mBound)
        return (mBound.width().toFloat() > lasttextwidth||mBound.height().toFloat() > lasttextheight)//当前的文字宽度比上一次比较
    }

    /**
     * 褪去动画
     * isover 是否重构layout
     */
    private fun QuitText(isOver:Boolean) {
        var startvalue:Float=if(!reverse){
            -(2*textwidth/width)
        }else 0f
        var va = ValueAnimator.ofFloat(startvalue, 1f)
        va.duration = (mDuration / 2).toLong()
        va.interpolator = LinearInterpolator()
        va.addUpdateListener { animation ->
            if((animation.animatedValue as Float)==startvalue &&isOver) requestLayout()
            when (orientation) {
                0 -> startheight = if (!reverse)
                    (1 - (animation.animatedValue as Float)) * (height / 2) + (height / 2)
                else (animation.animatedValue as Float) * (height / 2)
                1 -> startwidth = if (!reverse)
                    (animation.animatedValue as Float) * (width / 2)
                else (width / 2) + (width / 2) * (1 - (animation.animatedValue as Float))
            }

            invalidate()
        }
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        va.start()
    }}