package com.example.fsm.features.main_menu.di



import com.example.fsm.features.main_menu.MainMenuContract
import com.example.fsm.features.main_menu.MainMenuPresenter
import com.example.fsm.features.main_menu.MainMenuRepository
import org.koin.dsl.module

object MainMenuModule {

//    val instance = module {
//
//        //Activity
//        factory<FiltersContract.Presenter> { (view: FiltersContract.View) ->
//            FiltersPresenter(
//                view = view
//            )
//        }
//
//        //Fragment
//        factory<FiltersFragmentContract.Presenter> { (view: FiltersFragmentContract.View) ->
//            FiltersFragmentPresenter(
//                view = view
//            )
//        }
//    }



    val instance = module {
        factory<MainMenuContract.Repository> { MainMenuRepository() }
        factory<MainMenuContract.Presenter> { MainMenuPresenter(get()) }
    }
}