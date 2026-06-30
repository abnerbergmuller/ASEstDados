package com.game.application.facade;

import com.game.application.dto.*;
import com.game.application.service.*;
import com.game.domain.model.*;
import com.game.infrastructure.datastructures.*;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Fachada principal do jogo, servindo como ponto de entrada para a API/Front-end.
 * Implementa arquitetura orientada a serviços para facilitar integração com REST.
 */
@Component
public class GameFacade {
    private final GameEngine engine;
    private final SetupService setupService;
    private final LeilaoService leilaoService;
    private Jogador[] jogadores;
    private int indiceJogadorAtual;

    public GameFacade(GameEngine engine, SetupService setupService, LeilaoService leilaoService) {
        this.engine = engine;
        this.setupService = setupService;
        this.leilaoService = leilaoService;
    }

    /**
     * Inicia uma nova partida com os jogadores e personagens informados.
     */
    public void iniciarPartida(List<String> nomes, List<Personagem> personagens) {
        if (nomes == null || personagens == null || nomes.size() != personagens.size()) {
            throw new IllegalArgumentException("Listas de nomes e personagens inválidas ou com tamanhos diferentes.");
        }

        this.jogadores = new Jogador[nomes.size()];
        for (int i = 0; i < nomes.size(); i++) {
            // Saldo inicial padrão: 500k
            jogadores[i] = new Jogador(nomes.get(i), 500000, personagens.get(i));
        }

        this.engine.configurarPartida(jogadores);
        this.indiceJogadorAtual = 0;
    }

    /**
     * Rola os dados para o jogador atual e processa o turno.
     * @return DTO com o resultado da jogada.
     */
    public ResultadoTurno rolarDados(String nomeJogador) {
        Jogador atual = jogadores[indiceJogadorAtual];
        if (!atual.getNome().equalsIgnoreCase(nomeJogador)) {
            throw new IllegalStateException("Não é o turno do jogador: " + nomeJogador + ". Esperado: " + atual.getNome());
        }

        ResultadoTurno resultado = engine.executarTurno(atual);
        
        // Se não parou em um imóvel disponível para compra, avança o turno automaticamente
        if (!resultado.isAguardandoDecisaoCompra()) {
            avancarTurno();
        }
        
        return resultado;
    }

    /**
     * O jogador atual decide comprar o imóvel da casa onde parou.
     */
    public void comprarImovelAtual(String nomeJogador) {
        Jogador atual = buscarJogador(nomeJogador);
        CasaTabuleiro casa = atual.getPosicaoAtual().getData();
        
        if (casa.getTipo() == CasaTabuleiro.TipoCasa.IMOVEL) {
            Imovel imovel = casa.getImovel();
            if (imovel.getDono() == null && atual.getSaldo() >= imovel.getPrecoCompra()) {
                atual.pagar(imovel.getPrecoCompra());
                atual.adicionarImovel(imovel);
                engine.getHistorico().enqueue(atual.getNome() + " comprou Imóvel: " + imovel.getNome() + " por " + imovel.getPrecoCompra());
            }
        }
        avancarTurno();
    }

    /**
     * O jogador atual decide não comprar o imóvel. O turno avança.
     */
    public void pularCompra(String nomeJogador) {
        Jogador atual = buscarJogador(nomeJogador);
        CasaTabuleiro casa = atual.getPosicaoAtual().getData();
        if (casa.getTipo() == CasaTabuleiro.TipoCasa.IMOVEL) {
            engine.getHistorico().enqueue(atual.getNome() + " decidiu não comprar o Imóvel: " + casa.getImovel().getNome());
        }
        avancarTurno();
    }

    /**
     * Paga a fiança para sair da prisão imediatamente.
     */
    public void pagarFianca(String nomeJogador) {
        Jogador jogador = buscarJogador(nomeJogador);
        jogador.pagar(50000); // Fiança padrão de 50k
        engine.liberarDaPrisao(jogador);
    }

    /**
     * Retorna o estado atual do jogo para exibição no front-end.
     */
    public EstadoJogoDTO obterEstadoJogo() {
        if (jogadores == null) return null;
        
        EstadoJogoDTO estado = new EstadoJogoDTO();
        estado.setProximoJogadorNome(jogadores[indiceJogadorAtual].getNome());
        
        List<EstadoJogoDTO.JogadorResumo> resumos = new ArrayList<>();
        for (Jogador j : jogadores) {
            String casaNome = j.getPosicaoAtual().getData().getTipo().toString();
            if (j.getPosicaoAtual().getData().getTipo() == CasaTabuleiro.TipoCasa.IMOVEL) {
                casaNome = j.getPosicaoAtual().getData().getImovel().getNome();
            }
            
            // Calculate the board square index
            int squareIndex = 0;
            com.game.infrastructure.datastructures.DoubleNode<CasaTabuleiro> startNode = engine.getCasaInicio();
            com.game.infrastructure.datastructures.DoubleNode<CasaTabuleiro> currNode = startNode;
            while (currNode != j.getPosicaoAtual() && currNode != null) {
                squareIndex++;
                currNode = currNode.getNext();
                if (currNode == startNode) break;
            }
            
            resumos.add(new EstadoJogoDTO.JogadorResumo(j.getNome(), j.getSaldo(), casaNome, j.getStatus().toString(), squareIndex));
        }
        estado.setJogadores(resumos);
        return estado;
    }

    /**
     * Gera o relatório final com ranking e estatísticas.
     */
    public RelatorioFinalDTO gerarRelatorioFimDeJogo() {
        RelatorioFinalDTO relatorio = new RelatorioFinalDTO();
        
        List<RelatorioFinalDTO.JogadorEstatistica> stats = new ArrayList<>();
        for (Jogador j : jogadores) {
            double valorImoveis = 0;
            for (Imovel i : j.getListaImoveis()) {
                valorImoveis += i.getPrecoCompra();
            }
            stats.add(new RelatorioFinalDTO.JogadorEstatistica(j.getNome(), j.getSaldo(), valorImoveis, j.getVoltasCompletas()));
        }
        
        // Ordena por patrimônio total desc
        stats.sort((a, b) -> Double.compare(b.getPatrimonioTotal(), a.getPatrimonioTotal()));
        relatorio.setRanking(stats);
        
        if (engine.getImovelMaiorAluguel() != null) {
            relatorio.setImovelMaiorAluguelNome(engine.getImovelMaiorAluguel().getNome());
            relatorio.setMaiorAluguelValor(engine.getMaiorAluguelCobrado());
        }
        
        relatorio.setHistoricoFinal(engine.getHistorico().toList());
        
        return relatorio;
    }

    private void avancarTurno() {
        int totalJogadores = jogadores.length;
        int contagem = 0;
        do {
            indiceJogadorAtual = (indiceJogadorAtual + 1) % totalJogadores;
            contagem++;
        } while (jogadores[indiceJogadorAtual].getStatus() == JogadorStatus.FALIDO && contagem < totalJogadores);
    }

    private Jogador buscarJogador(String nome) {
        for (Jogador j : jogadores) {
            if (j.getNome().equalsIgnoreCase(nome)) return j;
        }
        throw new IllegalArgumentException("Jogador não encontrado: " + nome);
    }

    public LeilaoService getLeilaoService() {
        return leilaoService;
    }

    public List<CasaTabuleiro> getTabuleiroCasas() {
        if (engine == null) return new ArrayList<>();
        List<CasaTabuleiro> casas = new ArrayList<>();
        DoubleNode<CasaTabuleiro> inicio = engine.getCasaInicio();
        DoubleNode<CasaTabuleiro> atual = inicio;
        do {
            casas.add(atual.getData());
            atual = atual.getNext();
        } while (atual != inicio);
        return casas;
    }

    public void declararFalencia(String nomeJogador) {
        Jogador j = buscarJogador(nomeJogador);
        j.irA(JogadorStatus.FALIDO);
    }

    public Jogador buscarJogadorPorNome(String nome) {
        return buscarJogador(nome);
    }
}
