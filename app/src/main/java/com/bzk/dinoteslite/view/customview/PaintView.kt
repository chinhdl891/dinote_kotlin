package com.bzk.dinoteslite.view.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bzk.dinoteslite.model.FingerPath
import kotlin.math.abs

class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0,
) : View(context, attrs) {
    companion object {
        const val COLOR_PEN = Color.BLACK
        const val COLOR_ERASER = Color.WHITE
        const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }

    private var brushSize = 10
    private var mX: Float = 0f
    private var mY: Float = 0f
    private var currentColor: Int = 0x000000
    private lateinit var mPath: Path
    private lateinit var mPaint: Paint
    private var paths = arrayListOf<FingerPath>()
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private var mBitmapPaint = Paint(Paint.DITHER_FLAG)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = COLOR_PEN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = null
            alpha = 0xff
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
        currentColor = COLOR_PEN
    }

    fun pen(color: Int = Color.BLACK, brushSize: Int = 10) {
        currentColor = color
        this.brushSize = brushSize
        invalidate()
    }

    fun onBackDraw() {
        if (paths.size > 0) {
            paths.removeAt(paths.size - 1)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        mCanvas.drawColor(DEFAULT_BG_COLOR)
        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.stokeWidth.toFloat()
            mPaint.maskFilter = null
            mCanvas.drawPath(fp.path, mPaint)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.restore()


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path().apply {
            reset()
            moveTo(x, y)
        }
        val fp = FingerPath(currentColor, brushSize, mPath).apply { }
        paths.add(fp)
        mX = x
        mY = y
    }

}