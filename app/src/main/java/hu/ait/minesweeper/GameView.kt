package hu.ait.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import hu.ait.minesweeper.MainActivity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColor
import com.google.android.material.snackbar.Snackbar
import hu.ait.minesweeper.R.color.*
import hu.ait.minesweeper.databinding.ActivityMainBinding
import kotlinx.coroutines.delay

@SuppressLint("ResourceAsColor")
class GameView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var gridNumW = 10;
    private var gridNumH = 10;
    private val cellPadding = 10;
    private val paintBackground = Paint()
    private val paintRect = Paint()
    private val paintLine = Paint()
    private val paintText = Paint()
    private val flagModeEnabled: Boolean = false
    private val flag = BitmapFactory.decodeResource(resources, R.mipmap.flag_foreground)
    private var srcRect = Rect()
    private var destRect = Rect()
    private var gameOver: Boolean
    private var prevDownClick: ClickClass = ClickClass()


    init {

        gridNumW = (context as MainActivity).gridNumW
        gridNumH = (context as MainActivity).gridNumH


        paintBackground.color = R.color.bgGray
        paintBackground.style = Paint.Style.FILL

        //todo use real color
        paintRect.color = R.color.tileGray
        paintRect.style = Paint.Style.FILL

        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintText.color = Color.BLACK
        paintText.textSize = 60f

        srcRect = Rect(0, 0, flag.width, flag.height)

        gameOver = false

        setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                if (gameOver) return true
                if (v == null) return true
                val cx = v.x.toInt() / (width / gridNumW)
                val cy = v.y.toInt() / (height / gridNumH)

                setFlag(cx, cy)
                return true
            }
        })
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
        if (gameOver) return true
        val cx: Int = event.x.toInt() / (width / gridNumW)
        val cy: Int = event.y.toInt() / (height / gridNumH)
        if (event.action == MotionEvent.ACTION_DOWN) {
            //save the start of the click
            prevDownClick = ClickClass(cx, cy)
        }
        if (event.action == MotionEvent.ACTION_UP) {

            //if touched outside, do nothing
            if (cx < 0 || cy < 0 || cx >= gridNumW || cy >= gridNumH) return true

            val clickedTile = MineSweeperModel.getField(cx, cy)

            //if long press, flag it
            if (prevDownClick.x == cx && prevDownClick.y == cy && Math.abs(prevDownClick.time - System.currentTimeMillis()) > 300) {
                setFlag(cx, cy, clickedTile)
                invalidate()
                return true
            }

            //if Flag mode is on, just flag/unflag is
            if ((context as MainActivity).flagModeEnabled) {
                //invert flag
                setFlag(cx, cy, clickedTile)

            } else {
                //if it was already uncovered or flagged, do nothing
                if (!clickedTile.hidden || clickedTile.flagged)
                    return true

                //if it is a bomb, game over
                if (clickedTile.bomb == -1) {
                    gameOver = true
                    Snackbar.make(
                        rootView,
                        "You found a bomb :( The game is over",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //reveal it anyways
                clickedTile.hidden = false
                MineSweeperModel.setField(cx, cy, clickedTile)
            }

            //rerender
            invalidate()

        }
        return true
    }

    private fun setFlag(
        cx: Int,
        cy: Int,
        clickedTile: TileClass = MineSweeperModel.getField(cx, cy)
    ) {
        Log.d("MAIN", "FLAGGED $cx , $cy")
        clickedTile.flagged = !clickedTile.flagged
        MineSweeperModel.setField(cx, cy, clickedTile)
    }

    override fun hasOnLongClickListeners(): Boolean {
        return super.hasOnLongClickListeners()
    }


    private fun drawTiles(canvas: Canvas?) {

        val w: Float = canvas?.width?.toFloat() ?: 100f
        val sw = w / gridNumW.toFloat()

        val h: Float = canvas?.height?.toFloat() ?: 100f
        val sh = h / gridNumH.toFloat()

        for (i in 0..<gridNumH) {
            for (j in 0..<gridNumW) {
                val curTile = MineSweeperModel.getField(i, j)



                if (curTile.hidden && !gameOver) {
                    //draw covering tile

                    canvas?.drawRect(
                        sw * i + cellPadding,
                        sh * j + cellPadding,
                        sw * (i + 1) - cellPadding,
                        sh * (j + 1) - cellPadding,
                        paintRect
                    )

                    if (curTile.flagged) {
                        //draw flag
                        drawFlag(sw, i, sh, j, canvas)
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

    private fun drawFlag(sw: Float, i: Int, sh: Float, j: Int, canvas: Canvas?) {
        destRect = Rect(
            ((sw * i) + cellPadding * 2).toInt(),
            ((sh * j) + cellPadding * 2).toInt(),
            ((sw * (i + 1)) - cellPadding * 2).toInt(),
            ((sh * (j + 1)) - cellPadding * 2).toInt(),
        )
        canvas?.drawBitmap(flag, srcRect, destRect, null)
    }

    fun resetGameMaster(reason: Int = 0) {
        MineSweeperModel.resetGame()
        gameOver = false
        invalidate()
    }

}