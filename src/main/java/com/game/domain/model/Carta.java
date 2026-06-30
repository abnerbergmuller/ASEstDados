package com.game.domain.model;

/**
 * Representa uma carta de Sorte ou Revés.
 */
public class Carta {

    public enum TipoEfeito {
        AVANCAR_CASAS, 
        VOLTAR_CASAS, 
        PAGAR_BANCO, 
        RECEBER_BANCO, 
        IR_PRISAO, 
        IR_INICIO, 
        PAGAR_JOGADORES, 
        RECEBER_JOGADORES
    }

    private String descricao;
    private TipoEfeito tipoEfeito;
    private double valor;

    public Carta(String descricao, TipoEfeito tipoEfeito, double valor) {
        this.descricao = descricao;
        this.tipoEfeito = tipoEfeito;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoEfeito getTipoEfeito() {
        return tipoEfeito;
    }

    public void setTipoEfeito(TipoEfeito tipoEfeito) {
        this.tipoEfeito = tipoEfeito;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
