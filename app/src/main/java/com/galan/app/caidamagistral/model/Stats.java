package com.galan.app.caidamagistral.model;

import java.io.Serializable;

public class Stats implements Serializable {

    private int top1, top2, top3;
    private int jugadores, minutos, puntos;
    private int partidas, kills;

    public Stats() {

    }

    public Stats(int top1, int top2, int top3, int jugadores, int minutos, int puntos, int partidas, int kills) {
        this.top1 = top1;
        this.top2 = top2;
        this.top3 = top3;
        this.jugadores = jugadores;
        this.minutos = minutos;
        this.puntos = puntos;
        this.partidas = partidas;
        this.kills = kills;
    }

    public int getTop1() {
        return top1;
    }

    public void setTop1(int top1) {
        this.top1 = top1;
    }

    public int getTop2() {
        return top2;
    }

    public void setTop2(int top2) {
        this.top2 = top2;
    }

    public int getTop3() {
        return top3;
    }

    public void setTop3(int top3) {
        this.top3 = top3;
    }

    public int getJugadores() {
        return jugadores;
    }

    public void setJugadores(int jugadores) {
        this.jugadores = jugadores;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getPartidas() {
        return partidas;
    }

    public void setPartidas(int partidas) {
        this.partidas = partidas;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
