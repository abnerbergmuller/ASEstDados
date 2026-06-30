package com.game.domain.model;

/**
 * Enum que representa os tipos de personagens e suas habilidades passivas.
 */
public enum Personagem {
    ESPECULADOR("Especulador", "+20% salário, +10% imposto"),
    NEGOCIANTE("Negociante", "-10% no aluguel pago"),
    ADVOGADO("Advogado", "1 saída grátis da prisão"),
    CONSTRUTOR("Construtor", "aluguel de seus imóveis +15%");

    private final String nome;
    private final String descricaoHabilidade;

    Personagem(String nome, String descricaoHabilidade) {
        this.nome = nome;
        this.descricaoHabilidade = descricaoHabilidade;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricaoHabilidade() {
        return descricaoHabilidade;
    }
}
