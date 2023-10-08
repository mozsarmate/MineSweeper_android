package hu.ait.minesweeper

import android.util.Log
import kotlin.random.Random

object MineSweeperModel {
    //todo get these from mainactivity
    private const val numRows = 10
    private const val numCols = 10
    private const val numBombs = 15

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

    fun resetGame() {
        for (i in 0..<numRows) {
            for (j in 0..<numCols) {
                matrix[i][j] = TileClass(hidden = true, flagged = false, bomb = 0)
            }
        }
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


        //calculate numeric values
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
    
    fun autoReveal(cx : Int, cy : Int){
        for (k in 0..<8) {
            val ni = cx + dx[k]
            val nj = cy + dy[k]
        
            //check if the neighboring cell is within bounds
            if (ni in 0..<numRows && nj >= 0 && nj < numCols) {
                if(matrix[ni][nj].hidden) {
                    matrix[ni][nj].hidden = false
                    if (matrix[ni][nj].bomb == 0) {
                        autoReveal(ni, nj)
                    }
                }
            }
        }
    }

}