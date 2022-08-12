package dev.sanskar.pokedex.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sanskar.pokedex.model.Favorite

@Database(entities = [Favorite::class], version = 1)
abstract class PokedexDB : RoomDatabase() {
    abstract fun pokedexDao(): PokedexDao
}