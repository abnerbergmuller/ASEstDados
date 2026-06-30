package com.game.application.dto;

import java.util.List;

/**
 * DTO que representa o estado atual simplificado do jogo para o front-end.
 */
public class EstadoJogoDTO {
    private String proximoJogadorNome;
    private List<JogadorResumo> jogadores;

    public static class JogadorResumo {
        private String nome;
        private double saldo;
        private String posicaoCasaNome;
        private String status;

        public JogadorResumo(String nome, double saldo, String posicaoCasaNome, String status) {
            this.nome = nome;
            this.saldo = saldo;
            this.posicaoCasaNome = posicaoCasaNome;
            this.status = status;
        }

        public String getNome() { return nome; }
        public double getSaldo() { return saldo; }
        public String getPosicaoCasaNome() { return posicaoCasaNome; }
        public String getStatus() { return status; }
    }

    public String getProximoJogadorNome() {
        return proximoJogadorNome;
    }

    public void setProximoJogadorNome(String proximoJogadorNome) {
        this.proximoJogadorNome = proximoJogadorNome;
    }

    public List<JogadorResumo> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<JogadorResumo> jogadores) {
        this.jogadores = jogadores;
    }
}
