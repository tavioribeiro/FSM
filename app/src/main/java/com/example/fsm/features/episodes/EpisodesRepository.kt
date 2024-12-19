package com.example.fsm.features.episodes

import android.content.Context
import com.example.fsm.core.extentions.ContextManager

class EpisodesRepository: EpisodesContract.Repository {
    val context = ContextManager.getGlobalContext()

    val sharedPref = context.getSharedPreferences("SharedPrefsReNerd", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()


}

