package ie.setu.recipeapp.views.recipe

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.recipeapp.R
import ie.setu.recipeapp.databinding.ActivityRecipeBinding
import ie.setu.recipeapp.models.RecipeModel
import timber.log.Timber.i

class RecipeView : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    private lateinit var presenter: RecipePresenter
    var recipe = RecipeModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        presenter = RecipePresenter(this)

        binding.chooseImage.setOnClickListener {
            presenter.cacheRecipe(binding.recipeTitle.text.toString(), binding.recipeDescription.text.toString())
            presenter.doSelectImage()
        }

        binding.recipeLocation.setOnClickListener {
            presenter.cacheRecipe(binding.recipeTitle.text.toString(), binding.recipeDescription.text.toString())
            presenter.doSetLocation()
        }

        binding.btnAdd.setOnClickListener {
            if (binding.recipeTitle.text.toString().isEmpty()) {
                Snackbar.make(binding.root, R.string.enter_recipe_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                // presenter.cacheRecipe(binding.recipeTitle.text.toString(), binding.description.text.toString())
                presenter.doAddOrSave(binding.recipeTitle.text.toString(), binding.recipeDescription.text.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe, menu)
        val deleteMenu: MenuItem = menu.findItem(R.id.item_delete)
        deleteMenu.isVisible = presenter.edit
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                presenter.doDelete()
            }
            R.id.item_cancel -> {
                presenter.doCancel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showRecipe(recipe: RecipeModel) {
        binding.recipeTitle.setText(recipe.title)
        binding.recipeDescription.setText(recipe.description)
        binding.btnAdd.setText(R.string.save_recipe)
        Picasso.get()
            .load(recipe.image)
            .into(binding.recipeImage)
        if (recipe.image != Uri.EMPTY) {
            binding.chooseImage.setText(R.string.change_recipe_image)
        }
    }

    fun updateImage(image: Uri) {
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.recipeImage)
        binding.chooseImage.setText(R.string.change_recipe_image)
    }
}