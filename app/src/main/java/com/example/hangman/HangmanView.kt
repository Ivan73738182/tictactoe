package com.example.hangman

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HangmanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var mistakes: Int = 0
        set(value) {
            field = value.coerceIn(0, 6)
            invalidate()
        }

    var isLost: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val gallowsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        bodyPaint.color = if (isLost) Color.parseColor("#E53935") else Color.WHITE

        // Виселица
        canvas.drawLine(w * 0.10f, h * 0.95f, w * 0.70f, h * 0.95f, gallowsPaint)
        canvas.drawLine(w * 0.20f, h * 0.95f, w * 0.20f, h * 0.05f, gallowsPaint)
        canvas.drawLine(w * 0.20f, h * 0.05f, w * 0.55f, h * 0.05f, gallowsPaint)
        canvas.drawLine(w * 0.55f, h * 0.05f, w * 0.55f, h * 0.15f, gallowsPaint)

        val cx = w * 0.55f
        val headR = h * 0.07f
        val headY = h * 0.15f + headR

        // 1. Голова
        if (mistakes >= 1) {
            canvas.drawCircle(cx, headY, headR, bodyPaint)
        }

        val bodyTop = headY + headR
        val bodyBottom = bodyTop + h * 0.15f

        // 2. Туловище
        if (mistakes >= 2) {
            canvas.drawLine(cx, bodyTop, cx, bodyBottom, bodyPaint)
        }

        // 3. Левая рука
        if (mistakes >= 3) {
            canvas.drawLine(cx, bodyTop + h * 0.03f, cx - w * 0.10f, bodyTop + h * 0.10f, bodyPaint)
        }

        // 4. Правая рука
        if (mistakes >= 4) {
            canvas.drawLine(cx, bodyTop + h * 0.03f, cx + w * 0.10f, bodyTop + h * 0.10f, bodyPaint)
        }

        // 5. Левая нога
        if (mistakes >= 5) {
            canvas.drawLine(cx, bodyBottom, cx - w * 0.10f, bodyBottom + h * 0.12f, bodyPaint)
        }

        // 6. Правая нога
        if (mistakes >= 6) {
            canvas.drawLine(cx, bodyBottom, cx + w * 0.10f, bodyBottom + h * 0.12f, bodyPaint)
        }
    }
}
