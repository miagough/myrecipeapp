package ie.setu.recipeapp.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.net.toUri
import timber.log.Timber.i

// SQLite database constants
private const val DATABASE_NAME = "recipes.db"
private const val TABLE_NAME = "recipes"
private const val COLUMN_ID = "id"
private const val COLUMN_TITLE = "title"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_IMAGE = "image"
private const val COLUMN_LAT = "lat"
private const val COLUMN_LNG = "lng"
private const val COLUMN_ZOOM = "zoom"


class RecipeSQLStore(private val context: Context) : RecipeStore {

    private var database: SQLiteDatabase

    init {
        // Set up local database connection
        database = RecipeDbHelper(context).writableDatabase
    }

    override fun findAll(): List<RecipeModel> {
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = database.rawQuery(query, null)

        val recipes = ArrayList<RecipeModel>()

        cursor.use {
            while (it.moveToNext()) {
                recipes.add(
                    //getRecipeFromCursor(it))
                    RecipeModel(
                        id = cursor.getLong(0),
                        title = cursor.getString(1),
                        description = cursor.getString(2),
                        image = cursor.getString(3).toUri(),
                        lat = cursor.getDouble(4),
                        lng = cursor.getDouble(5),
                        zoom = cursor.getFloat(6)
                    )
                )
            }
        }

        i("recipesdb : findAll() - has ${recipes.size} records")
        return recipes
    }

    override fun findById(id: Long): RecipeModel? {
        TODO("Not yet implemented")
    }

    override fun create(recipe: RecipeModel) {

        // Update the database for persistence beyond app lifetime
        val values = ContentValues()

        // ID is being managed by the table via auto increment
        values.put(COLUMN_TITLE, recipe.title)
        values.put(COLUMN_DESCRIPTION, recipe.description)
        values.put(COLUMN_IMAGE, recipe.image.toString())
        values.put(COLUMN_LAT, recipe.lat)
        values.put(COLUMN_LNG, recipe.lng)
        values.put(COLUMN_ZOOM, recipe.zoom)

        // Do the insert and set the transaction as successful
        recipe.id = database.insert(TABLE_NAME, null, values)
    }

    override fun update(recipe: RecipeModel) {
        TODO("Not yet implemented")
    }

    override fun delete(recipe: RecipeModel) {
        TODO("Not yet implemented")
    }

}

private class RecipeDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    private val createTableSQL =
        "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_IMAGE TEXT, " +
                "$COLUMN_LAT REAL, $COLUMN_LNG REAL, $COLUMN_ZOOM REAL)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades if needed
    }
}