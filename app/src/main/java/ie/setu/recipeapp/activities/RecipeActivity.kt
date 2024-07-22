package ie.setu.recipeapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import ie.setu.recipeapp.R
import ie.setu.recipeapp.databinding.ActivityRecipeBinding
import ie.setu.recipeapp.main.MainApp
import ie.setu.recipeapp.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        app = application as MainApp
        i("Recipe Activity started...")
        binding.btnAdd.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.title.isNotEmpty()) {
                app.recipes.add(recipe.copy())
                i("add Button Pressed: ${recipe}")
                for (i in app.recipes.indices)
                { i("Recipe[$i]:${this.app.recipes[i]}") }
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

}