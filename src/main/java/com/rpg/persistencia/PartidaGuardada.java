package com.rpg.persistencia;

import com.rpg.entities.Gremio;
import com.rpg.entities.PerfilJugador;

import java.io.Serializable;

/** Contenedor del estado de la partida que se guarda en el .dat. */
public class PartidaGuardada implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PerfilJugador perfil;
    private final Gremio gremio;

    public PartidaGuardada(PerfilJugador perfil, Gremio gremio) {
        this.perfil = perfil;
        this.gremio = gremio;
    }

    public PerfilJugador getPerfil() {
        return perfil;
    }

    public Gremio getGremio() {
        return gremio;
    }
}