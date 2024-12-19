package com.example.fsm.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.databinding.ActivityMainBinding
import com.example.fsm.features.episodes.EpisodesActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //this.setUpUi()
        this.goTo(EpisodesActivity::class.java)
    }


    private fun setUpUi(){
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.navigationBarColor = Color.parseColor(ContextManager.getColorHex(1))
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            statusBarColor = Color.parseColor(ContextManager.getColorHex(0))
        }
    }

    private fun goTo(destiny: Class<EpisodesActivity>){
        val intent = Intent(this, destiny)
        // Usando FLAG para evitar que MainActivity permaneça na pilha
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
