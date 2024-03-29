package com.unshareit.cleantest.view

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.tan

class BackgroundDrawable private constructor(private val builder: Builder) : Drawable() {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path: Path = Path()

    override fun draw(canvas: Canvas) {
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        // draw background
        paint.reset()
        paint.flags = paint.flags or Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL
        setPaintColors(w, h, builder.colors, builder.angle, builder.positions)
        val radii = if (builder.round != null) {
            FloatArray(8){ index ->
                if (builder.round!![index / 2]) {
                    h / 2
                } else {
                    0f
                }
            }
        } else {
            builder.radii
        }
        path.reset()
        path.addRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            radii,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)

        // draw stroke
        if (builder.strokeColors == null || builder.strokeWidth <= 0) {
            return
        }
        paint.reset()
        paint.flags = paint.flags or Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.STROKE
        setPaintColors(w, h, builder.strokeColors?:return, builder.angle, builder.positions)
        paint.strokeWidth = builder.strokeWidth
        val offset = builder.strokeWidth * 0.5f
        path.reset()
        path.addRoundRect(
            bounds.left.toFloat() + offset,
            bounds.top.toFloat() + offset,
            bounds.right.toFloat() - offset,
            bounds.bottom.toFloat() - offset,
            radii,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)
    }

    private fun setPaintColors(w:Float, h:Float, colors: IntArray, angle: Float, positions: FloatArray?){
        if (colors.size > 1) {
            var x0 = 0f
            var y0 = 0f
            var x1 = 0f
            var y1 = 0f
            if (angle < 90) {
                x0 = 0f
                y0 = 0f
                x1 = w
                y1 = w * tan(angle.toRadians).toFloat()
                if (y1 >= h) {
                    x1 = h * tan((90 - angle).toRadians).toFloat()
                    y1 = h
                }
            } else if (angle < 180) {
                x0 = w
                y0 = 0f
                x1 = (w - h * tan((angle - 90).toRadians)).toFloat()
                y1 = h
                if (x1 <= 0) {
                    x1 = 0f
                    y1 = (w * tan((180 - angle).toRadians)).toFloat()
                }
            } else if (angle < 270) {
                x0 = w
                y0 = h
                x1 = 0f
                y1 = (h - w * tan((angle - 180).toRadians)).toFloat()
                if (y1 <= 0) {
                    x1 = w - h * tan((270 - angle).toRadians).toFloat()
                    y1 = 0f
                }
            } else if (angle < 360) {
                x0 = 0f
                y0 = h
                x1 = h * tan((angle - 270).toRadians).toFloat()
                y1 = 0f
                if (x1 >= w) {
                    x1 = w
                    y1 = h - w * tan((360 - angle).toRadians).toFloat()
                }
            }

            paint.shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                colors,
                positions,
                Shader.TileMode.CLAMP
            )
        } else {
            paint.color = colors[0]
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    class Builder(commonBgStyleConfig: CommonBgStyleConfig? = null) {
        var colors: IntArray = intArrayOf(Color.TRANSPARENT)
            private set
        var strokeColors: IntArray? = null
            private set
        var strokeWidth:Float = 0f
            private set
        val radii: FloatArray = FloatArray(8)
        var positions: FloatArray? = null
            private set
        var angle: Float = 0f
            private set
        var round: BooleanArray? = null
            private set

        init {
            commonBgStyleConfig?.apply {
                setBackground(bgColors)
                setStrokeColor(strokeColors)
                setCornerRadius(radii)
                setRoundCorner(roundCorner)
                setGradientPositions(gradientPosition)
            }
        }

        fun setBackground(@ColorInt colors: IntArray?): Builder {
            if (colors != null) {
                this.colors = colors
            }
            return this
        }

        fun setStrokeColor(@ColorInt strokeColors: IntArray?): Builder {
            this.strokeColors = strokeColors
            return this
        }

        fun setStrokeWidth(strokeWidth: Float): Builder {
            this.strokeWidth = strokeWidth
            return this
        }

        fun setRoundCorner(round: Boolean): Builder {
            this.round = listOf(round, round, round, round).toBooleanArray()
            return this
        }

        fun setRoundCorner(round: BooleanArray?): Builder {
            if (round != null) {
                if (round.size != 4) {
                    throw IllegalArgumentException("round array size must be four, that means left_top/right_top/right_bottom/left_bottom")
                }
                this.round = round
            }
            return this
        }

        fun setCornerRadius(radius: Float): Builder {
            for (i in 0 until this.radii.size) {
                this.radii[i] = radius
            }
            return this
        }

        fun setCornerRadius(radii: FloatArray?): Builder {
            if (radii != null) {
                if (radii.size != 4) {
                    throw IllegalArgumentException("Radii array size must be four, that means left_top/right_top/right_bottom/left_bottom")
                }
                for (i in 0 until this.radii.size) {
                    this.radii[i] = radii[i / 2]
                }
            }
            return this
        }

        fun setGradientPositions(positions: FloatArray?): Builder {
            if (positions != null) {
                this.positions = positions
            }
            return this
        }

        /**
         * @param angle
         * 0: 从左到右 →
         * 90：从上到下 ↓
         * 180 从右到左 ←
         * 270 从下到上 ↑
         */
        fun setGradientAngle(
            @FloatRange(
                from = 0.0,
                to = 360.0,
                toInclusive = false
            ) angle: Float
        ): Builder {
            this.angle = angle
            return this
        }

        fun build(): BackgroundDrawable {
            return BackgroundDrawable(this)
        }
    }

    private val Float.toRadians: Double
        get() {
            return Math.toRadians(this.toDouble())
        }
}