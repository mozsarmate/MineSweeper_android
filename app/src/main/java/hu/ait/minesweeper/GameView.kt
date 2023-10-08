package hu.ait.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import hu.ait.minesweeper.R.color.*
import kotlin.math.abs

@SuppressLint("ResourceAsColor")
class GameView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    
    private var gridNumW = 0
    private var gridNumH = 0
    private val cellPadding = 10
    private val paintBackground = Paint()
    private val paintRect = Paint()
    private val paintLine = Paint()
    private val paintText = Paint()
    private val flag = BitmapFactory.decodeResource(resources, R.mipmap.flag_foreground)
    private val bomb = BitmapFactory.decodeResource(resources, R.mipmap.bomb_foreground)
    private var srcRect = Rect()
    private var destRect = Rect()
    private var gameOver: Boolean
    private var won: Boolean
    private var prevDownClick: ClickClass = ClickClass()
    
    
    init {
        gridNumW = MineSweeperModel.numCols
        gridNumH = MineSweeperModel.numRows
        
        paintBackground.color = ContextCompat.getColor(this.context, R.color.bgGray)
        paintBackground.style = Paint.Style.FILL
        
        paintRect.color = ContextCompat.getColor(this.context, R.color.tileGray)
        paintRect.style = Paint.Style.FILL
        
        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f
        
        paintText.color = Color.BLACK
        paintText.textSize = 60f
        
        srcRect = Rect(0, 0, flag.width, flag.height)
        
        gameOver = false
        won = false
    }
    
    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)
        drawGrid(canvas)
        drawTiles(canvas)
        drawStat()
    }
    
    private fun drawStat() {
        val ans = "Revealed: ${MineSweeperModel.numRevealed} / ${MineSweeperModel.numRows * MineSweeperModel.numCols - MineSweeperModel.numBombs}      Flagged: ${MineSweeperModel.numFlagged} / ${MineSweeperModel.numBombs}"
        (context as MainActivity).binding.tvMainInstruction.text = ans
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
    
    
    private fun drawTiles(canvas: Canvas?) {
        val w: Float = canvas?.width?.toFloat() ?: 100f
        val sw = w / gridNumW.toFloat()
        
        val h: Float = canvas?.height?.toFloat() ?: 100f
        val sh = h / gridNumH.toFloat()
        
        for (i in 0..<gridNumH) {
            for (j in 0..<gridNumW) {
                val curTile = MineSweeperModel.getField(i, j)
                
                if (curTile.hidden && !gameOver) {
                    canvas?.drawRect(
                        sw * i + cellPadding,
                        sh * j + cellPadding,
                        sw * (i + 1) - cellPadding,
                        sh * (j + 1) - cellPadding,
                        paintRect
                    )
                    if (curTile.flagged) {
                        drawIcon(sw, i, sh, j, canvas, flag)
                    }
                } else {
                    if (curTile.bomb == 9) {
                        drawIcon(sw, i, sh, j, canvas, bomb)
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
        
        if (gameOver) {
            Snackbar.make(rootView, "Game Over!", Snackbar.LENGTH_LONG)
                .setAction("ReTry") { resetGameMaster() }
                .show()
        }
        else if (won) {
            Snackbar.make(rootView, "You have flagged all the bombs! Congrats", Snackbar.LENGTH_LONG)
                .setAction("Play Again!") { resetGameMaster() }
                .show()
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
            Log.d("MAIN", "DOWN $prevDownClick")
        }
        if (event.action == MotionEvent.ACTION_UP) {
            //if touched outside, do nothing
            if (cx < 0 || cy < 0 || cx >= gridNumW || cy >= gridNumH) return true
            
            //if Flag mode is on or valid long press was detected, just flag/unflag is
            val longPress : Boolean = prevDownClick.x == cx && prevDownClick.y == cy && abs(prevDownClick.time - System.currentTimeMillis()) > 300
            if ((context as MainActivity).flagModeEnabled || longPress) {
                MineSweeperModel.invertFlag(cx, cy)
            } else {
                val clickedTile = MineSweeperModel.getField(cx, cy)
                
                //if it was already uncovered or flagged, do nothing
                if (!clickedTile.hidden || clickedTile.flagged)
                    return true
                
                //reveal it anyways
                MineSweeperModel.revealTile(cx, cy)
                
                //if it is a bomb, game over
                if (clickedTile.bomb == 9) {
                    gameOver = true
                }
                //if it is a 0, run auto reveal
                else if (clickedTile.bomb == 0) {
                    MineSweeperModel.autoReveal(cx, cy)
                }
            }
            
            //check win
            if (!gameOver) {
                won = MineSweeperModel.checkWin()
            }
            
            //rerender
            invalidate()
        }
        return true
    }
    
    private fun drawIcon(sw: Float, i: Int, sh: Float, j: Int, canvas: Canvas?, icon: Bitmap) {
        
        destRect = Rect(
            ((sw * i) + cellPadding * 2).toInt(),
            ((sh * j) + cellPadding * 2).toInt(),
            ((sw * (i + 1)) - cellPadding * 2).toInt(),
            ((sh * (j + 1)) - cellPadding * 2).toInt(),
        )
        canvas?.drawBitmap(icon, srcRect, destRect, null)
    }
    
    fun resetGameMaster(reason: Int = 0) {
        MineSweeperModel.resetGame()
        gameOver = false
        won = false
        invalidate()
    }
}