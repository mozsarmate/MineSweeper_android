package hu.ait.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var flagModeEnabled : Boolean = false
    var autoRevealEnabled : Boolean = true
    var luckyFirst : Boolean = true

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
        }
    
        binding.cbAutoReveal.setOnCheckedChangeListener { _, isChecked ->
            autoRevealEnabled = isChecked
        }
    
        binding.cbLuckyFirst.setOnCheckedChangeListener { _, isChecked ->
            luckyFirst = isChecked
        }
    }
}


