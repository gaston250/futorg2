package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.models.Jugador;

import org.junit.Test;

public class JugadorTest {

    @Test
    public void winRate_isCorrect() {
        Jugador jugador = new Jugador();
        jugador.setPartidosJugados(10);
        jugador.setPartidosGanados(5);
        
        assertEquals(50.0, jugador.getWinRate(), 0.01);
    }

    @Test
    public void winRate_zeroMatches_isZero() {
        Jugador jugador = new Jugador();
        jugador.setPartidosJugados(0);
        jugador.setPartidosGanados(0);
        
        assertEquals(0.0, jugador.getWinRate(), 0.01);
    }

    @Test
    public void jugadorValidation_isValid() {
        Jugador jugador = new Jugador();
        jugador.setNombre("Messi");
        jugador.setEmail("leo@goat.com");
        
        assertTrue(jugador.isValid());
    }

    @Test
    public void jugadorValidation_emptyName_isInvalid() {
        Jugador jugador = new Jugador();
        jugador.setNombre("");
        jugador.setEmail("leo@goat.com");
        
        assertFalse(jugador.isValid());
    }
}
