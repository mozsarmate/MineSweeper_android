package hu.ait.minesweeper

import android.service.quicksettings.Tile
import android.util.Log
import kotlin.random.Random

object MineSweeperModel {
    //todo get these from mainactivity
    val numRows = 10
    val numCols = 10
    val numBombs = 10

    // Define the deltas for the neighboring cells (8 possible directions)
    val dx = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    val dy = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

    // Create a 2D array of TileClass objects
    val matrix: Array<Array<TileClass>> = Array(numRows) { Array(numCols) { TileClass() } }

    fun getField(x: Int, y: Int): TileClass {
        if (x < 0 || y < 0 || x >= numRows || y >= numCols) TileClass(false, true, 100)
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
            if (matrix[rndx][rndy].bomb != -1) {
                matrix[rndx][rndy].bomb = -1
                Log.d("MAIN", "BOMB placed $rndx , $rndy")
                bombsPlaced++
            }
        }



        //calculate numeric values
        for (i in 0..<numRows) {
            for (j in 0..<numCols) {
                //if not a bomb
                if (matrix[i][j].bomb != -1) {
                    var nearBombs = 0
                    for (k in 0..<8) {
                        val ni = i + dx[k]
                        val nj = j + dy[k]

                        //check if the neighboring cell is within bounds
                        if (ni in 0..<numRows && nj >= 0 && nj < numCols) {
                            //check if the neighboring cell is a bomb
                            if (matrix[ni][nj].bomb == -1) {
                                nearBombs++
                            }
                        }
                    }
                    matrix[i][j].bomb = nearBombs
                }
            }
        }
    }

}