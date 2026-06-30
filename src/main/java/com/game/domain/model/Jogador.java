package com.game.domain.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.game.domain.exception.SaldoInsuficienteException;
import com.game.infrastructure.datastructures.DoubleNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um jogador no jogo.
 */
public class Jogador {
    private String nome;
    private double saldo;
    private Personagem personagem;
    private DoubleNode<CasaTabuleiro> posicaoAtual;
    private List<Imovel> listaImoveis;
    private JogadorStatus status;
    private int voltasCompletas;

    public Jogador() {
    }

    public Jogador(String nome, double saldoInicial, Personagem personagem) {
        this.nome = nome;
        this.saldo = saldoInicial;
        this.personagem = personagem;
        this.listaImoveis = new ArrayList<>();
        this.status = JogadorStatus.ATIVO;
        this.posicaoAtual = null;
        this.voltasCompletas = 0;
    }

    /**
     * Realiza um pagamento.
     * @param valor Valor a ser pago.
     * @throws SaldoInsuficienteException se o saldo for insuficiente.
     */
    public void pagar(double valor) {
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para pagar: " + valor);
        }
        this.saldo -= valor;
    }

    /**
     * Recebe um valor.
     * @param valor Valor a ser recebido.
     */
    public void receber(double valor) {
        this.saldo += valor;
    }

    /**
     * Adiciona um imóvel à lista de propriedades do jogador.
     * @param imovel O imóvel comprado.
     */
    public void adicionarImovel(Imovel imovel) {
        this.listaImoveis.add(imovel);
        imovel.setDono(this);
    }

    /**
     * Altera o status do jogador. Se for para falência, limpa seus bens.
     * @param novoStatus O novo status do jogador.
     */
    public void irA(JogadorStatus novoStatus) {
        this.status = novoStatus;
        if (novoStatus == JogadorStatus.FALIDO) {
            this.saldo = 0;
            // Libera os imóveis
            for (Imovel imovel : listaImoveis) {
                imovel.setDono(null);
                imovel.setMultiplicadorDemanda(1.0);
            }
            this.listaImoveis.clear();
        }
    }

    /**
     * Recebe o salário considerando o bônus de Especulador.
     * @param valorBase Valor base do salário.
     */
    public void receberSalario(double valorBase) {
        double valorFinal = valorBase;
        if (personagem == Personagem.ESPECULADOR) {
            valorFinal *= 1.20;
        }
        receber(valorFinal);
    }

    /**
     * Paga aluguel considerando o desconto de Negociante.
     * @param valorBase Valor base do aluguel.
     */
    public void pagarAluguel(double valorBase) {
        double valorFinal = valorBase;
        if (personagem == Personagem.NEGOCIANTE) {
            valorFinal *= 0.90;
        }
        pagar(valorFinal);
    }

    /**
     * Paga imposto considerando o acréscimo de Especulador.
     * @param valorBase Valor base do imposto.
     */
    public void pagarImposto(double valorBase) {
        double valorFinal = valorBase;
        if (personagem == Personagem.ESPECULADOR) {
            valorFinal *= 1.10;
        }
        pagar(valorFinal);
    }

    public int getVoltasCompletas() { return voltasCompletas; }
    public void adicionarVolta() { this.voltasCompletas++; }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public Personagem getPersonagem() { return personagem; }
    public void setPersonagem(Personagem personagem) { this.personagem = personagem; }

    @JsonIgnore
    public DoubleNode<CasaTabuleiro> getPosicaoAtual() { return posicaoAtual; }
    public void setPosicaoAtual(DoubleNode<CasaTabuleiro> posicaoAtual) { this.posicaoAtual = posicaoAtual; }

    @JsonManagedReference
    public List<Imovel> getListaImoveis() { return listaImoveis; }

    public JogadorStatus getStatus() { return status; }
    public void setStatus(JogadorStatus status) { this.status = status; }
}
