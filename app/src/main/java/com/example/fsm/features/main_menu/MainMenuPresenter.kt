package com.example.fsm.features.main_menu



class MainMenuPresenter(private val repository: MainMenuContract.Repository) : MainMenuContract.Presenter {

    private var view: MainMenuContract.View? = null


    override fun attachView(view: MainMenuContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }


}
