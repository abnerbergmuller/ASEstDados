package com.game.application.dto;

import com.game.domain.model.Imovel;
import java.util.List;

/**
 * DTO que contém os dados estatísticos e o ranking final da partida.
 */
public class RelatorioFinalDTO {
    
    public static class JogadorEstatistica {
        private String nome;
        private double saldo;
        private double patrimonioImoveis;
        private double patrimonioTotal;
        private int voltasCompletas;

        public JogadorEstatistica(String nome, double saldo, double patrimonioImoveis, int voltasCompletas) {
            this.nome = nome;
            this.saldo = saldo;
            this.patrimonioImoveis = patrimonioImoveis;
            this.patrimonioTotal = saldo + patrimonioImoveis;
            this.voltasCompletas = voltasCompletas;
        }

        public String getNome() { return nome; }
        public double getPatrimonioTotal() { return patrimonioTotal; }
        public int getVoltasCompletas() { return voltasCompletas; }
    }

    private List<JogadorEstatistica> ranking;
    private String imovelMaiorAluguelNome;
    private double maiorAluguelValor;
    private List<String> historicoFinal;

    public List<JogadorEstatistica> getRanking() {
        return ranking;
    }

    public void setRanking(List<JogadorEstatistica> ranking) {
        this.ranking = ranking;
    }

    public String getImovelMaiorAluguelNome() {
        return imovelMaiorAluguelNome;
    }

    public void setImovelMaiorAluguelNome(String imovelMaiorAluguelNome) {
        this.imovelMaiorAluguelNome = imovelMaiorAluguelNome;
    }

    public double getMaiorAluguelValor() {
        return maiorAluguelValor;
    }

    public void setMaiorAluguelValor(double maiorAluguelValor) {
        this.maiorAluguelValor = maiorAluguelValor;
    }

    public List<String> getHistoricoFinal() {
        return historicoFinal;
    }

    public void setHistoricoFinal(List<String> historicoFinal) {
        this.historicoFinal = historicoFinal;
    }
}
