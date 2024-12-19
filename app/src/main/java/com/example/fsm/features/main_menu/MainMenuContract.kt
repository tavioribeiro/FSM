package com.example.fsm.features.main_menu



interface MainMenuContract {

    interface View {

    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()


    }

    interface Repository {

    }
}