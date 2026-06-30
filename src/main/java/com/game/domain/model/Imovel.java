package com.game.domain.model;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Representa uma propriedade imobiliária no tabuleiro.
 */
public class Imovel {
    private String nome;
    private double precoCompra;
    private double aluguelBase;
    private Jogador dono;
    private double multiplicadorDemanda;

    public Imovel() {
    }

    public Imovel(String nome, double precoCompra, double aluguelBase) {
        this.nome = nome;
        this.precoCompra = precoCompra;
        this.aluguelBase = aluguelBase;
        this.multiplicadorDemanda = 1.0;
        this.dono = null;
    }

    /**
     * Calcula o aluguel atual considerando o multiplicador de demanda
     * e o bônus passivo do personagem Construtor.
     * @return O valor do aluguel a ser pago.
     */
    public double calcularAluguelAtual() {
        double aluguel = aluguelBase * multiplicadorDemanda;
        
        if (dono != null && dono.getPersonagem() == Personagem.CONSTRUTOR) {
            aluguel *= 1.15;
        }
        
        return aluguel;
    }

    /**
     * Registra uma visita ao imóvel, aumentando seu multiplicador de demanda.
     */
    public void registrarVisita() {
        if (multiplicadorDemanda < 2.0) {
            multiplicadorDemanda += 0.1;
            if (multiplicadorDemanda > 2.0) {
                multiplicadorDemanda = 2.0;
            }
        }
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPrecoCompra() { return precoCompra; }
    public void setPrecoCompra(double precoCompra) { this.precoCompra = precoCompra; }

    public double getAluguelBase() { return aluguelBase; }
    public void setAluguelBase(double aluguelBase) { this.aluguelBase = aluguelBase; }

    @JsonBackReference
    public Jogador getDono() { return dono; }
    public void setDono(Jogador dono) { this.dono = dono; }

    public double getMultiplicadorDemanda() { return multiplicadorDemanda; }
    public void setMultiplicadorDemanda(double multiplicadorDemanda) { this.multiplicadorDemanda = multiplicadorDemanda; }
}
