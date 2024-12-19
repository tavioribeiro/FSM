package com.example.fsm.features.episodes


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.databinding.ActivityEpisodesBinding
import org.koin.android.ext.android.inject
import android.content.pm.ActivityInfo


class EpisodesActivity: AppCompatActivity(), EpisodesContract.View{

    private lateinit var binding: ActivityEpisodesBinding
    private val presenter: EpisodesContract.Presenter by inject()



    private var originalColor1 = ContextManager.getColorHex(0)
    private var originalColor2 = ContextManager.getColorHex(0)

    private var currentRecyclerViewPoition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEpisodesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setUpUi()
        presenter.attachView(this)
    }


    private fun setUpUi(){
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.navigationBarColor = Color.parseColor(ContextManager.getColorHex(1))
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            statusBarColor = Color.parseColor(ContextManager.getColorHex(0))
        }
    }


    /*private fun setUpCallbacks(){
        binding.floatingPlayer.setonBackgroundCollorsChangeListener{ color1, color2 ->

        }
    }*/




    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
