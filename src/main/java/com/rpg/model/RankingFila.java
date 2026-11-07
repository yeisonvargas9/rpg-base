package com.rpg.model;

public class RankingFila {
    private final String usuario;
    private final int totalVictorias;

    public RankingFila(String usuario, int totalVictorias) {
        this.usuario = usuario;
        this.totalVictorias = totalVictorias;
    }

    public String getUsuario() { return usuario; }
    public int getTotalVictorias() { return totalVictorias; }
}