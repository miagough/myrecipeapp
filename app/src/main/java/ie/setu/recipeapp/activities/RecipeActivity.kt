package ie.setu.recipeapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.recipeapp.R
import ie.setu.recipeapp.databinding.ActivityRecipeBinding
import ie.setu.recipeapp.helpers.showImagePicker
import ie.setu.recipeapp.main.MainApp
import ie.setu.recipeapp.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var recipe = RecipeModel()
    lateinit var app : MainApp
    var edit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        if (intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("recipe_edit")!!
            binding.recipeTitle.setText(recipe.title)
            binding.recipeDescription.setText(recipe.description)
            binding.btnAdd.setText(R.string.save_recipe)
            Picasso.get()
                .load(recipe.image)
                .into(binding.recipeImage)
            if (recipe.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_recipe_image)
            }

            binding.chooseImage.setOnClickListener {
                i("Select image")
            }

            app = application as MainApp
            i("Recipe Activity started...")
            binding.btnAdd.setOnClickListener() {
                recipe.title = binding.recipeTitle.text.toString()
                recipe.description = binding.recipeDescription.text.toString()
                if (recipe.title.isNotEmpty()) {
                    if (edit) {
                        app.recipes.update(recipe.copy())
                    } else {
                        app.recipes.create(recipe.copy())
                    }
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(
                        it, getString(R.string.enter_recipe_title),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            binding.chooseImage.setOnClickListener {
                showImagePicker(imageIntentLauncher)
            }
            registerImagePickerCallback()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            recipe.image = result.data!!.data!!
                            Picasso.get()
                                .load(recipe.image)
                                .into(binding.recipeImage)
                            binding.chooseImage.setText(R.string.change_recipe_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}