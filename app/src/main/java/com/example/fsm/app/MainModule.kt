package com.example.fsm.app

import com.example.fsm.features.episodes.di.EpisodesModule


object MainModule {
    var instance = listOf(
        EpisodesModule.instance
    )
}
