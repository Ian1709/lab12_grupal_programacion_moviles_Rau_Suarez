package com.tecsup.pokemon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PokemonEntity::class, PokemonTypeCatalogEntity::class], version = 3, exportSchema = false)
@TypeConverters(PokemonConverters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    companion object {
        @Volatile private var instance: PokemonDatabase? = null

        fun getInstance(context: Context): PokemonDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                PokemonDatabase::class.java,
                "pokemon.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instance = it }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE pokemon ADD COLUMN generation INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE pokemon ADD COLUMN dexNumber INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE pokemon SET dexNumber = id WHERE id BETWEEN 1 AND 1025")
                database.execSQL("CREATE TABLE IF NOT EXISTS pokemon_type_catalog (type TEXT NOT NULL, pokemonId INTEGER NOT NULL, PRIMARY KEY(type, pokemonId), FOREIGN KEY(pokemonId) REFERENCES pokemon(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_pokemon_type_catalog_pokemonId ON pokemon_type_catalog(pokemonId)")
                database.execSQL("INSERT OR IGNORE INTO pokemon_type_catalog(type, pokemonId) SELECT 'fire', id FROM pokemon")
            }
        }
    }
}
