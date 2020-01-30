package com.quickhandslogistics.view.activities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_signature.*
import kotlinx.android.synthetic.main.activity_signature.view.*
import kotlinx.android.synthetic.main.layout_header.*
import java.io.ByteArrayOutputStream

class SignatureActivity : AppCompatActivity() {

    var mSignature: Signature?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        mSignature = Signature(this, null)
        linear_signature.addView(mSignature)

        text_title.text = "Add Signature"

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        button_save_sign.setOnClickListener(View.OnClickListener { view ->
            finish()
        })

        button_clear.setOnClickListener(View.OnClickListener { view ->
            mSignature?.clearSign()
        })

    }

    class Signature(
        context: Context?,
        attrs: AttributeSet?
    ) :
        View(context, attrs) {
        val dirtyRect = RectF()
        var paint = Paint()
        var path = Path()
        var lastTouchX = 0f
        var lastTouchY = 0f


        fun clearSign() {
            path.reset()
            invalidate()
           // button_save_sign.setEnabled(false)
        }

        fun save() {
            val returnedBitmap = Bitmap.createBitmap(
                linear_signature.getWidth(),
                linear_signature.getHeight(), Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            val bgDrawable: Drawable = linear_signature.getBackground()
            if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
            linear_signature.draw(canvas)
            val bs = ByteArrayOutputStream()
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawPath(path, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val eventX = event.x
            val eventY = event.y
            //button_save_sign.setEnabled(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(eventX, eventY)
                    lastTouchX = eventX
                    lastTouchY = eventY
                    return true
                }
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                    resetDirtyRect(eventX, eventY)
                    val historySize = event.historySize
                    var i = 0
                    while (i < historySize) {
                        val historicalX = event.getHistoricalX(i)
                        val historicalY = event.getHistoricalY(i)
                        path.lineTo(historicalX, historicalY)
                        i++
                    }
                    path.lineTo(eventX, eventY)
                }
            }
            invalidate(
                (dirtyRect.left - HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.top - HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.right + HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.bottom + HALF_STROKE_WIDTH).toInt()
            )
            lastTouchX = eventX
            lastTouchY = eventY
            return true
        }

        private fun resetDirtyRect(eventX: Float, eventY: Float) {
            dirtyRect.left = Math.min(lastTouchX, eventX)
            dirtyRect.right = Math.max(lastTouchX, eventX)
            dirtyRect.top = Math.min(lastTouchY, eventY)
            dirtyRect.bottom = Math.max(lastTouchY, eventY)
        }

        companion object {
            const val STROKE_WIDTH = 6f
            const val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
        }

        init {
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = STROKE_WIDTH
        }
    }
}
