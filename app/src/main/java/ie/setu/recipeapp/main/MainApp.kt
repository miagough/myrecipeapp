package ie.setu.recipeapp.main

import android.app.Application
import ie.setu.recipeapp.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val recipes = ArrayList<RecipeModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Recipe started")
    }
}