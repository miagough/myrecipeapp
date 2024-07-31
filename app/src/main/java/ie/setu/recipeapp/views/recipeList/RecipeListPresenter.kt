package ie.setu.recipeapp.views.recipeList

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.recipeapp.views.map.RecipeMapView
import ie.setu.recipeapp.main.MainApp
import ie.setu.recipeapp.models.RecipeModel
import ie.setu.recipeapp.views.recipe.RecipeView

class RecipeListPresenter(val view: RecipeListView) {

    var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private var position: Int = 0

    init {
        app = view.application as MainApp
        registerMapCallback()
        registerRefreshCallback()
    }

    fun getRecipes() = app.recipes.findAll()

    fun doAddRecipe() {
        val launcherIntent = Intent(view, RecipeView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doEditRecipe(recipe: RecipeModel, pos: Int) {
        val launcherIntent = Intent(view, RecipeView::class.java)
        launcherIntent.putExtra("recipe_edit", recipe)
        position = pos
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doShowRecipesMap() {
        val launcherIntent = Intent(view, RecipeMapView::class.java)
        mapIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK)
                    view.onRefresh()
                else if (it.resultCode == 99)   // Deleting
                    view.onDelete(position)
            }
    }
    private fun registerMapCallback() {
        mapIntentLauncher = view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
        }
    }
}