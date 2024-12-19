package com.example.fsm.app

import com.example.fsm.features.main_menu.di.MainMenuModule


object MainModule {
    var instance = listOf(
        MainMenuModule.instance
    )
}
