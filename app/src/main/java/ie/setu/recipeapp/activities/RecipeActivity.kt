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
import ie.setu.recipeapp.models.Location
import ie.setu.recipeapp.models.RecipeModel
import timber.log.Timber.i

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var recipe = RecipeModel()
    lateinit var app: MainApp
    var edit = false

    //var location = Location(52.245696, -7.139102, 15f)

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


        app = application as MainApp


        i(getString(R.string.recipe_activity_started))

        if (intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("recipe_edit")!!
            binding.recipeTitle.setText(recipe.title)
            binding.recipeDescription.setText(recipe.description)
            binding.btnAdd.text = getString(R.string.save_recipe)
            Picasso.get()
                .load(recipe.image)
                .into(binding.recipeImage)
            if (recipe.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_recipe_image)
            }
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher,this)
        }
        registerImagePickerCallback()

        binding.recipeLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (recipe.zoom != 0f) {
                location.lat =  recipe.lat
                location.lng = recipe.lng
                location.zoom = recipe.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }
        registerMapCallback()


        binding.recipeLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.btnAdd.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.title.isNotEmpty()) {
                if (edit) {
                    app.recipes.update(recipe.copy())
                }
                else{
                    app.recipes.create(recipe.copy())
                }
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it, getString(R.string.enter_recipe_title),
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe, menu)
        if (edit) menu.getItem(1).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.item_delete -> {
                app.recipes.delete(recipe)
                setResult(99)
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

                            val image = result.data!!.data!!
                            contentResolver.takePersistableUriPermission(image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            recipe.image = image

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

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            recipe.lat = location.lat
                            recipe.lng = location.lng
                            recipe.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}