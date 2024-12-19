package com.example.fsm.features.episodes



interface EpisodesContract {

    interface View {

    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()


    }

    interface Repository {

    }
}