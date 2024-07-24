package ie.setu.recipeapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.recipeapp.databinding.CardRecipeBinding
import ie.setu.recipeapp.models.RecipeModel


interface RecipeListener {
    fun onRecipeClick(recipe: RecipeModel)
}
class RecipeAdapter constructor(private var recipes: List<RecipeModel>,
                                   private val listener: RecipeListener) :
    RecyclerView.Adapter<RecipeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe, listener)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder(private val binding : CardRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel, listener: RecipeListener) {
            binding.recipeTitle.text = recipe.title
            binding.recipeDescription.text = recipe.description
            binding.root.setOnClickListener { listener.onRecipeClick(recipe)}
        }
    }
}