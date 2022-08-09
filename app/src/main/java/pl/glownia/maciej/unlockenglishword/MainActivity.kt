package pl.glownia.maciej.unlockenglishword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Creates an Activity that hosts the UnlockWordFragment in the app.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}