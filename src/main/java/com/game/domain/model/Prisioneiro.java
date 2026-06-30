package com.game.domain.model;

/**
 * Representa um jogador que está na prisão e suas tentativas de saída.
 */
public class Prisioneiro {
    private Jogador jogador;
    private int tentativas;

    public Prisioneiro(Jogador jogador) {
        this.jogador = jogador;
        this.tentativas = 0;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void incrementarTentativas() {
        this.tentativas++;
    }
}
