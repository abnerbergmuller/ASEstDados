package com.game.application.dto;

import com.game.domain.model.CasaTabuleiro;
import com.game.domain.model.Carta;

/**
 * DTO que encapsula o resultado de um turno para o Front-end.
 */
public class ResultadoTurno {
    private String jogadorNome;
    private int dado1;
    private int dado2;
    private CasaTabuleiro casaFinal;
    private String mensagem;
    private boolean aguardandoDecisaoCompra;
    private Carta cartaSorteReves;

    public ResultadoTurno(String jogadorNome) {
        this.jogadorNome = jogadorNome;
        this.aguardandoDecisaoCompra = false;
    }

    // Getters e Setters
    public String getJogadorNome() { return jogadorNome; }
    public void setJogadorNome(String jogadorNome) { this.jogadorNome = jogadorNome; }

    public int getDado1() { return dado1; }
    public void setDado1(int dado1) { this.dado1 = dado1; }

    public int getDado2() { return dado2; }
    public void setDado2(int dado2) { this.dado2 = dado2; }

    public CasaTabuleiro getCasaFinal() { return casaFinal; }
    public void setCasaFinal(CasaTabuleiro casaFinal) { this.casaFinal = casaFinal; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public boolean isAguardandoDecisaoCompra() { return aguardandoDecisaoCompra; }
    public void setAguardandoDecisaoCompra(boolean aguardandoDecisaoCompra) { this.aguardandoDecisaoCompra = aguardandoDecisaoCompra; }

    public Carta getCartaSorteReves() { return cartaSorteReves; }
    public void setCartaSorteReves(Carta cartaSorteReves) { this.cartaSorteReves = cartaSorteReves; }

    public int getSomaDados() { return dado1 + dado2; }
}
