package ie.setu.recipeapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(var id: Long = 0,
                       var title: String = "",
                       var description: String = "") : Parcelable
