package ie.setu.recipeapp.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class RecipeMemStore : RecipeStore {

    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
    }

    override fun create(recipe: RecipeModel) {
        recipes.add(recipe)
        logAll()
    }

    override fun update(recipe: RecipeModel) {
        var foundRecipe: RecipeModel? = recipes.find { p -> p.id == recipe.id }
        if (foundRecipe != null) {
            foundRecipe.title = recipe.title
            foundRecipe.description = recipe.description
            foundRecipe.image = recipe.image
            foundRecipe.lat = recipe.lat
            foundRecipe.lng = recipe.lng
            foundRecipe.zoom = recipe.zoom
            logAll()
        }
    }

    override fun delete(recipe: RecipeModel) {
        recipes.remove(recipe)
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}