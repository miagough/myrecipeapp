package ie.setu.recipeapp.models

interface RecipeStore {
    fun findAll(): List<RecipeModel>
    fun create(recipe: RecipeModel)

    fun update(recipe: RecipeModel)
}