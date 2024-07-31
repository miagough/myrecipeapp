package ie.setu.recipeapp.views.recipe

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ie.setu.recipeapp.databinding.ActivityRecipeBinding
import ie.setu.recipeapp.helpers.showImagePicker
import ie.setu.recipeapp.main.MainApp
import ie.setu.recipeapp.models.Location
import ie.setu.recipeapp.models.RecipeModel
import ie.setu.recipeapp.views.editLocation.EditLocationView
import timber.log.Timber

class RecipePresenter(private val view: RecipeView) {

    var recipe = RecipeModel()
    var app: MainApp = view.application as MainApp
    var binding: ActivityRecipeBinding = ActivityRecipeBinding.inflate(view.layoutInflater)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false;

    init {
        if (view.intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = view.intent.extras?.getParcelable("recipe_edit")!!
            view.showRecipe(recipe)
        }
        registerImagePickerCallback()
        registerMapCallback()
    }
    fun doAddOrSave(title: String, description: String) {
        recipe.title = title
        recipe.description = description
        if (edit) {
            app.recipes.update(recipe)
        } else {
            app.recipes.create(recipe)
        }
        view.setResult(RESULT_OK)
        view.finish()
    }
    fun doCancel() {
        view.finish()
    }
    fun doDelete() {
        view.setResult(99)
        app.recipes.delete(recipe)
        view.finish()
    }
    fun doSelectImage() {
        showImagePicker(imageIntentLauncher,view)
    }
    fun doSetLocation() {
        val location = Location(52.245696, -7.139102, 15f)
        if (recipe.zoom != 0f) {
            location.lat =  recipe.lat
            location.lng = recipe.lng
            location.zoom = recipe.zoom
        }
        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("location", location)
        mapIntentLauncher.launch(launcherIntent)
    }
    fun cacheRecipe (title: String, description: String) {
        recipe.title = title;
        recipe.description = description
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            recipe.image = result.data!!.data!!
                            view.contentResolver.takePersistableUriPermission(recipe.image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            view.updateImage(recipe.image)
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }            }    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location == $location")
                            recipe.lat = location.lat
                            recipe.lng = location.lng
                            recipe.zoom = location.zoom
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}