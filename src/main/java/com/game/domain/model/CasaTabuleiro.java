package com.game.domain.model;

/**
 * Representa o conteúdo de uma casa no tabuleiro.
 */
public class CasaTabuleiro {
    
    public enum TipoCasa {
        INICIO, 
        IMOVEL, 
        IMPOSTO, 
        RESTITUICAO, 
        PRISAO, 
        LEILAO, 
        SORTE_REVES
    }

    private TipoCasa tipo;
    private Imovel imovel;

    public CasaTabuleiro(TipoCasa tipo) {
        this.tipo = tipo;
        this.imovel = null;
    }

    public CasaTabuleiro(Imovel imovel) {
        this.tipo = TipoCasa.IMOVEL;
        this.imovel = imovel;
    }

    public TipoCasa getTipo() {
        return tipo;
    }

    public void setTipo(TipoCasa tipo) {
        this.tipo = tipo;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }
}
