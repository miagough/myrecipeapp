package ie.setu.recipeapp.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ie.setu.recipeapp.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "recipes.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<RecipeModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class RecipeJSONStore(private val context: Context) : RecipeStore {

    var recipes = mutableListOf<RecipeModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<RecipeModel> {
        logAll()
        return recipes
    }

    override fun create(recipe: RecipeModel) {
        recipe.id = generateRandomId()
        recipes.add(recipe)
        serialize()
    }


    override fun update(recipe: RecipeModel) {
        val recipesList = findAll() as ArrayList<RecipeModel>
        val foundRecipe: RecipeModel? = recipesList.find { p -> p.id == recipe.id }
        if (foundRecipe != null) {
            foundRecipe.title = recipe.title
            foundRecipe.description = recipe.description
            foundRecipe.image = recipe.image
            foundRecipe.lat = recipe.lat
            foundRecipe.lng = recipe.lng
            foundRecipe.zoom = recipe.zoom
        }
        serialize()
    }

    override fun delete(recipe: RecipeModel) {
        recipes.remove(recipe)
        serialize()
    }

    override fun findById(id:Long) : RecipeModel? {
        val foundRecipe: RecipeModel? = recipes.find { it.id == id }
        return foundRecipe
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(recipes, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        recipes = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        recipes.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}