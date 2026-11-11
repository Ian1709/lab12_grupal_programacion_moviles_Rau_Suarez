package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.debut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonDebutTest {
    @Test fun `el id del catalogo corresponde a la generacion esperada`() {
        assertEquals(1, generationFromId(4))
        assertEquals(2, generationFromId(155))
        assertEquals(9, generationFromId(909))
    }

    @Test fun `la ficha indica el primer juego de la generacion nueve y su anime`() {
        val debut = Pokemon(id = 909, name = "fuecoco", generation = 9).debut()

        assertTrue(debut.game.contains("Scarlet & Violet"))
        assertTrue(debut.animeSeason.contains("Horizons"))
    }

    @Test fun `la generacion uno usa el lanzamiento original japones`() {
        val debut = Pokemon(id = 4, name = "charmander", generation = 1).debut()

        assertTrue(debut.game.contains("Red & Green"))
        assertTrue(debut.animeSeason.contains("Liga Añil"))
    }
}
