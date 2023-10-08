package hu.ait.minesweeper

import android.util.Log
import kotlin.random.Random

object MineSweeperModel {
    const val numRows = 10
    const val numCols = 10
    const val numBombs = 16
    var numRevealed = 0
    var numFlagged = 0
    
    
    // Define the deltas for the neighboring cells (8 possible directions)
    private val dx = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val dy = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    
    // Create a 2D array of TileClass objects
    private val matrix: Array<Array<TileClass>> = Array(numRows) { Array(numCols) { TileClass() } }
    
    
    fun getField(x: Int, y: Int): TileClass {
        //if invalid indexes, return dummy
        if (x < 0 || y < 0 || x >= numRows || y >= numCols) {
            return TileClass(hidden = false, flagged = true, bomb = 100)
        }
        return matrix[x][y]
    }
    
    fun setField(x: Int, y: Int, content: TileClass) {
        matrix[x][y] = content
    }
    
    fun revealTile(x: Int, y: Int) {
        if (matrix[x][y].hidden) numRevealed++
        
        matrix[x][y].hidden = false
    }
    
    fun invertFlag(x: Int, y: Int) {
        matrix[x][y].flagged = !matrix[x][y].flagged
        
        if (matrix[x][y].flagged) numFlagged++
        else numFlagged--
    }
    
    fun resetGame() {
        numRevealed = 0
        numFlagged = 0
        
        resetTiles()
        placeBombs()
        calculateNumerics()
    }
    
    private fun resetTiles() {
        for (i in 0..<numRows) {
            for (j in 0..<numCols) {
                matrix[i][j] = TileClass(hidden = true, flagged = false, bomb = 0)
            }
        }
    }
    
    private fun placeBombs() {
        var bombsPlaced = 0
        while (bombsPlaced < numBombs) {
            val rndx = Random.nextInt(0, 10)
            val rndy = Random.nextInt(0, 10)
            if (matrix[rndx][rndy].bomb != 9) {
                matrix[rndx][rndy].bomb = 9
                Log.d("MAIN", "BOMB placed $rndx , $rndy")
                bombsPlaced++
            }
        }
    }
    
    private fun calculateNumerics() {
        for (i in 0..<numRows) {
            for (j in 0..<numCols) {
                //if not a bomb
                if (matrix[i][j].bomb != 9) {
                    var nearBombs = 0
                    for (k in 0..<8) {
                        val ni = i + dx[k]
                        val nj = j + dy[k]
                    
                        //check if the neighboring cell is within bounds
                        if (ni in 0..<numRows && nj >= 0 && nj < numCols) {
                            //check if the neighboring cell is a bomb
                            if (matrix[ni][nj].bomb == 9) {
                                nearBombs++
                            }
                        }
                    }
                    matrix[i][j].bomb = nearBombs
                }
            }
        }
    }
    
    fun autoReveal(cx: Int, cy: Int) {
        for (k in 0..<8) {
            val ni = cx + dx[k]
            val nj = cy + dy[k]
            
            //check if the neighboring cell is within bounds
            if (ni in 0..<numRows && nj >= 0 && nj < numCols) {
                if (matrix[ni][nj].hidden && !matrix[ni][nj].flagged) {
                    revealTile(ni, nj)
                    
                    if (matrix[ni][nj].bomb == 0) {
                        autoReveal(ni, nj)
                    }
                }
            }
        }
    }
    
    fun checkWin(): Boolean {
        if (numRevealed != numCols * numRows - numBombs)
            return false
        if (numFlagged != numBombs)
            return false
        
        return true
    }
    
    
}