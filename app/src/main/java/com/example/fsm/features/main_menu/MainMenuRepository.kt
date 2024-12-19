package com.example.fsm.features.main_menu

import android.content.Context
import com.example.fsm.core.extentions.ContextManager

class MainMenuRepository: MainMenuContract.Repository {
    val context = ContextManager.getGlobalContext()

    val sharedPref = context.getSharedPreferences("SharedPrefsReNerd", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()


}

