package hu.ait.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hu.ait.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var flagModeEnabled : Boolean = false

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener {
            binding.gameView.resetGameMaster()
        }

        binding.btnReset.performClick()

        binding.swMode.setOnCheckedChangeListener { _, isChecked ->
            flagModeEnabled = isChecked
            Log.d("MAIN","Switch state changed flagMode: $flagModeEnabled")
        }

        
    }
}


