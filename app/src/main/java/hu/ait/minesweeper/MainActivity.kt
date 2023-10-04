package hu.ait.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hu.ait.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    public val gridNumW = 10;
    public val gridNumH = 10;
    public var flagModeEnabled : Boolean = false;

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener(){
            binding.gameView.resetGameMaster()
        }

        binding.btnReset.performClick()

        binding.swMode.setOnClickListener(){
            //todo implement this more elegantly, maybe not with a global var
            flagModeEnabled = binding.swMode.isChecked
            Log.d("MAIN","Switch state changed flagMode: $flagModeEnabled")
        }


        binding.btnList.setOnClickListener() {
            Log.d("MAIN","LIST")
            for (i in 0..<gridNumW) {
                for (j in 0..<gridNumH) {

                    val cur = MineSweeperModel.getField(i,j)
                    Log.d("MAIN","$i, $j  :::::   ${cur.bomb}   ${cur.flagged}   ${cur.hidden}")
                }
            }
        }
    }
}