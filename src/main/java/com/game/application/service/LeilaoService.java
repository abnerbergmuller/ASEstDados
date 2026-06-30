package com.game.application.service;

import com.game.domain.model.Imovel;
import com.game.domain.model.Jogador;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por gerenciar a lógica de leilões de imóveis.
 * Desacoplado para permitir que o front-end controle o fluxo de lances.
 */
@Service
public class LeilaoService {
    private Imovel imovelEmLeilao;
    private Jogador melhorLicitante;
    private double maiorLance;
    private boolean leilaoAtivo;

    /**
     * Inicia um novo leilão para um imóvel.
     */
    public void iniciarLeilao(Imovel imovel) {
        this.imovelEmLeilao = imovel;
        this.melhorLicitante = null;
        this.maiorLance = 0;
        this.leilaoAtivo = true;
    }

    /**
     * Recebe um lance de um jogador.
     * @return true se o lance for válido e superior ao atual.
     */
    public boolean receberLance(Jogador jogador, double valor) {
        if (!leilaoAtivo || jogador == null) return false;
        
        if (valor > maiorLance && jogador.getSaldo() >= valor) {
            this.maiorLance = valor;
            this.melhorLicitante = jogador;
            return true;
        }
        return false;
    }

    /**
     * Encerra o leilão, transfere o imóvel para o vencedor e debita o valor.
     * @return O jogador vencedor ou null se não houve lances.
     */
    public Jogador encerrarLeilao() {
        if (!leilaoAtivo) return null;
        
        if (melhorLicitante != null) {
            melhorLicitante.pagar(maiorLance);
            melhorLicitante.adicionarImovel(imovelEmLeilao);
        }
        
        Jogador vencedor = melhorLicitante;
        limparLeilao();
        return vencedor;
    }

    private void limparLeilao() {
        this.imovelEmLeilao = null;
        this.melhorLicitante = null;
        this.maiorLance = 0;
        this.leilaoAtivo = false;
    }

    public boolean isLeilaoAtivo() {
        return leilaoAtivo;
    }

    public Imovel getImovelEmLeilao() {
        return imovelEmLeilao;
    }

    public Jogador getMelhorLicitante() {
        return melhorLicitante;
    }

    public double getMaiorLance() {
        return maiorLance;
    }
}
