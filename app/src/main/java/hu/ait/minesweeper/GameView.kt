package hu.ait.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import hu.ait.minesweeper.MainActivity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColor
import hu.ait.minesweeper.R.color.*
import hu.ait.minesweeper.databinding.ActivityMainBinding

@SuppressLint("ResourceAsColor")
class GameView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var gridNumW = 10;
    private var gridNumH = 10;
    private val cellPadding = 10;
    private val paintBackground = Paint()
    private val paintRect = Paint()
    private val paintRectFlagged = Paint()
    private val paintLine = Paint()
    private val paintText = Paint()
    private val flagModeEnabled: Boolean = false
    private val flag = BitmapFactory.decodeResource(resources, R.drawable.flag_foreground)


    init {

        gridNumW = (context as MainActivity).gridNumW
        gridNumH = (context as MainActivity).gridNumH


        paintBackground.color = bgGray
        paintBackground.style = Paint.Style.FILL

        //todo use real color
        paintRect.color = tileGray
        paintRect.style = Paint.Style.FILL

        paintRectFlagged.color = Color.GREEN
        paintRectFlagged.style = Paint.Style.FILL

        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintText.color = Color.BLACK
        paintText.textSize = 60f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawGrid(canvas)
        drawTiles(canvas)
    }

    private fun drawGrid(canvas: Canvas?) {

        val w: Float = canvas?.width?.toFloat() ?: 100f
        val sw = w / gridNumW.toFloat()

        val h: Float = canvas?.height?.toFloat() ?: 100f
        val sh = h / gridNumH.toFloat()

        // draw horizontal lines
        for (i in 0..gridNumH + 1) {
            canvas?.drawLine(0f, sh * i, w, sh * i, paintLine)
        }

        // draw vertical lines
        for (i in 0..gridNumW + 1) {
            canvas?.drawLine(sw * i, 0f, sw * i, h, paintLine)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        if (event.action == MotionEvent.ACTION_UP) {
            val cx: Int = event.x.toInt() / (width / gridNumW)
            val cy: Int = event.y.toInt() / (height / gridNumH)

            //if touched outside, do nothing
            if (cx < 0 || cy < 0 || cx >= gridNumW || cy >= gridNumH) return true

            val clickedTile = MineSweeperModel.getField(cx, cy)

            //if Flag mode is on, just flag/unflag is
            if ((context as MainActivity).flagModeEnabled) {
                //invert flag
                Log.d("MAIN","FLAGGED $cx , $cy")
                clickedTile.flagged = !clickedTile.flagged
                MineSweeperModel.setField(cx, cy, clickedTile)

            } else {
                //if it was already uncovered or flagged, do nothing
                if (!clickedTile.hidden || clickedTile.flagged)
                    return true

                //if it is a bomb, game over
                if (clickedTile.bomb == -1) {
                    //found a bomb
                    //resetGameMaster()
                }

                //reveal it anyways
                clickedTile.hidden = false
                MineSweeperModel.setField(cx, cy, clickedTile)
            }

            //rerender
            invalidate()

        }
        //performClick()
        return true
    }

    private fun drawTiles(canvas: Canvas?) {

        val w: Float = canvas?.width?.toFloat() ?: 100f
        val sw = w / gridNumW.toFloat()

        val h: Float = canvas?.height?.toFloat() ?: 100f
        val sh = h / gridNumH.toFloat()

        for (i in 0..<gridNumH) {
            for (j in 0..<gridNumW) {
                val curTile = MineSweeperModel.getField(i, j)



                if (curTile.hidden) {
                    //draw covering tile
                    if (!curTile.flagged) {
                        canvas?.drawRect(
                            sw * i + cellPadding,
                            sh * j + cellPadding,
                            sw * (i + 1) - cellPadding,
                            sh * (j + 1) - cellPadding,
                            paintRect
                        )
                    }
                    if (curTile.flagged) {
                        canvas?.drawRect(
                            sw * i + cellPadding,
                            sh * j + cellPadding,
                            sw * (i + 1) - cellPadding,
                            sh * (j + 1) - cellPadding,
                            paintRectFlagged
                        )

                        //draw flag
                        //todo make this work
                        //canvas?.drawBitmap(flag, sw * i + cellPadding, sh * j + cellPadding, null)
                    }
                } else {
                    if (curTile.bomb == -1) {
                        //draw a bomb
                        //canvas?.drawBitmap(bomb, sw * i + cellPadding, sh * j + cellPadding, null)
                    } else {
                        canvas?.drawText(
                            curTile.bomb.toString(),
                            sw * i + cellPadding + 30,
                            sh * j + cellPadding + 60,
                            paintText
                        )
                    }
                }
            }
        }


    }

    fun resetGameMaster() {
        MineSweeperModel.resetGame()
        invalidate()
    }

}