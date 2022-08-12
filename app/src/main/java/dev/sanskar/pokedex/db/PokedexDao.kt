package dev.sanskar.pokedex.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanskar.pokedex.model.Favorite

@Dao
interface PokedexDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    suspend fun getAllFavorites(): List<Favorite>

    @Query("SELECT * FROM favorite WHERE id = :id")
    suspend fun checkIsFavorite(id: Int): Favorite?

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun removeFavorite(id: Int)
}