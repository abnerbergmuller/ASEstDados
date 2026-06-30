package com.game.application.service;

import com.game.application.dto.ResultadoTurno;
import com.game.domain.model.*;
import com.game.infrastructure.datastructures.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.Random;

/**
 * Motor principal do jogo, responsável pela lógica de turnos e regras de negócio.
 */
@Service
public class GameEngine {

    private final CustomQueue<String> historico;
    private final CustomQueue<Prisioneiro> filaPrisao;
    private CustomStack<Carta> baralho;
    private DoubleNode<CasaTabuleiro> casaInicio;
    private Jogador[] jogadores;
    private final SetupService setupService;
    private final Random random = new Random();
    
    private double maiorAluguelCobrado = 0;
    private Imovel imovelMaiorAluguel = null;

    public GameEngine(SetupService setupService) {
        this.setupService = setupService;
        this.historico = new CustomQueue<>(10); // Limite de 10 rodadas
        this.filaPrisao = new CustomQueue<>();
    }

    @PostConstruct
    public void init() {
        CircularDoublyLinkedList<CasaTabuleiro> tabuleiro = setupService.inicializarTabuleiro(setupService.getImoveisFantasy());
        this.casaInicio = tabuleiro.getHead();
        this.baralho = setupService.inicializarBaralho();
    }

    /**
     * Configura uma nova partida com os jogadores fornecidos.
     * Reseta o estado do jogo.
     */
    public void configurarPartida(Jogador[] jogadores) {
        this.jogadores = jogadores;
        this.maiorAluguelCobrado = 0;
        this.imovelMaiorAluguel = null;
        
        // Limpa filas
        while (!filaPrisao.isEmpty()) filaPrisao.dequeue();
        while (!historico.isEmpty()) historico.dequeue();
        
        // Reinicializa tabuleiro e baralho para garantir que imóveis estejam sem donos
        init();

        // Posiciona todos no início
        for (Jogador j : jogadores) {
            j.setPosicaoAtual(casaInicio);
        }
    }

    /**
     * Executa o turno de um jogador.
     */
    public ResultadoTurno executarTurno(Jogador jogador) {
        ResultadoTurno resultado = new ResultadoTurno(jogador.getNome());
        
        if (jogador.getStatus() == JogadorStatus.FALIDO) {
            resultado.setMensagem("Jogador está falido e não pode jogar.");
            return resultado;
        }

        // Rolar dados
        int d1 = random.nextInt(6) + 1;
        int d2 = random.nextInt(6) + 1;
        resultado.setDado1(d1);
        resultado.setDado2(d2);
        int soma = d1 + d2;

        // Verificar Prisão
        if (estaNaPrisao(jogador)) {
            boolean saiu = tentarSairDaPrisao(jogador, d1 == d2, resultado);
            if (!saiu) {
                registrarNoHistorico(jogador.getNome() + " permanece na prisão.");
                return resultado;
            }
        }

        // Mover Jogador
        moverJogador(jogador, soma, true, resultado);
        
        // Aplicar Efeito da Casa
        aplicarEfeitoCasa(jogador, jogador.getPosicaoAtual().getData(), resultado);

        // Verificar Falência após o turno
        verificarFalencia(jogador, resultado);

        registrarNoHistorico(resultado.getMensagem());
        return resultado;
    }

    /**
     * Move o jogador pelo tabuleiro.
     */
    public void moverJogador(Jogador jogador, int passos, boolean paraFrente, ResultadoTurno resultado) {
        DoubleNode<CasaTabuleiro> atual = jogador.getPosicaoAtual();
        
        for (int i = 0; i < passos; i++) {
            if (paraFrente) {
                atual = atual.getNext();
                // Regra: se cruzar o INICIO para frente, recebe salário
                if (atual.getData().getTipo() == CasaTabuleiro.TipoCasa.INICIO) {
                    jogador.adicionarVolta();
                    jogador.receberSalario(200000); // Salário base 200k
                    String msgSalario = " Passou pelo início e recebeu salário.";
                    resultado.setMensagem(resultado.getMensagem() == null ? msgSalario : resultado.getMensagem() + msgSalario);
                }
            } else {
                atual = atual.getPrev();
                // Retroceder não dá salário
            }
        }
        
        jogador.setPosicaoAtual(atual);
        resultado.setCasaFinal(atual.getData());
        
        String acao = paraFrente ? "avançou" : "voltou";
        String msgMover = String.format("%s %s %d casas e parou em %s.", 
            jogador.getNome(), acao, passos, descreverCasa(atual.getData()));
        
        resultado.setMensagem(resultado.getMensagem() == null ? msgMover : msgMover + " " + resultado.getMensagem());
    }

    /**
     * Aplica o efeito da casa onde o jogador parou.
     */
    private void aplicarEfeitoCasa(Jogador jogador, CasaTabuleiro casa, ResultadoTurno resultado) {
        switch (casa.getTipo()) {
            case IMOVEL:
                resolverImovel(jogador, casa.getImovel(), resultado);
                break;
            case IMPOSTO:
                resolverImposto(jogador, resultado);
                break;
            case RESTITUICAO:
                double restituicao = 200000 * 0.10; // 10% do salário
                jogador.receber(restituicao);
                resultado.setMensagem(resultado.getMensagem() + " Recebeu " + restituicao + " de restituição.");
                break;
            case PRISAO:
                enviarParaPrisao(jogador);
                resultado.setMensagem(resultado.getMensagem() + " Entrou na fila de espera da prisão.");
                break;
            case SORTE_REVES:
                resolverSorteReves(jogador, resultado);
                break;
            case LEILAO:
                resultado.setMensagem(resultado.getMensagem() + " Casa de Leilão (funcionalidade simplificada).");
                break;
            case INICIO:
                resultado.setMensagem(resultado.getMensagem() + " Descansando no Início.");
                break;
        }
    }

    private void resolverImovel(Jogador jogador, Imovel imovel, ResultadoTurno resultado) {
        if (imovel.getDono() == null) {
            resultado.setAguardandoDecisaoCompra(true);
            resultado.setMensagem(resultado.getMensagem() + " Imóvel disponível para compra por " + imovel.getPrecoCompra());
        } else if (imovel.getDono() != jogador) {
            double aluguel = imovel.calcularAluguelAtual();
            // Desconto de Negociante já aplicado no Jogador.pagarAluguel
            jogador.pagarAluguel(aluguel);
            
            // Credita ao dono (sem o desconto do outro)
            // Na verdade, o dono recebe o que o outro pagou?
            // "Calcula aluguel (reduz 10% se o pagador for NEGOCIANTE), debita de um, credita no outro"
            // Se o pagador tem 10% de desconto, o dono recebe 10% a menos.
            double valorPago = aluguel;
            boolean temDesconto = false;
            if (jogador.getPersonagem() == Personagem.NEGOCIANTE) {
                valorPago *= 0.90;
                temDesconto = true;
            }
            imovel.getDono().receber(valorPago);
            imovel.registrarVisita();

            if (valorPago > maiorAluguelCobrado) {
                maiorAluguelCobrado = valorPago;
                imovelMaiorAluguel = imovel;
            }
            
            String msgAluguel = " Pagou " + valorPago + " de aluguel para " + imovel.getDono().getNome();
            if (temDesconto) {
                msgAluguel += " (com 10% de desconto de Negociante)";
            }
            resultado.setMensagem(resultado.getMensagem() + msgAluguel);
        } else {
            resultado.setMensagem(resultado.getMensagem() + " Você já é o dono deste imóvel.");
        }
    }

    private void resolverImposto(Jogador jogador, ResultadoTurno resultado) {
        double patrimonio = jogador.getSaldo();
        for (Imovel i : jogador.getListaImoveis()) {
            patrimonio += i.getPrecoCompra();
        }
        double taxa = patrimonio * 0.05;
        jogador.pagarImposto(taxa); // Jogador.pagarImposto aplica os +10% se for Especulador
        
        double valorFinal = (jogador.getPersonagem() == Personagem.ESPECULADOR) ? taxa * 1.10 : taxa;
        resultado.setMensagem(resultado.getMensagem() + " Pagou " + valorFinal + " de imposto.");
    }

    private void resolverSorteReves(Jogador jogador, ResultadoTurno resultado) {
        if (baralho.isEmpty()) {
            this.baralho = setupService.inicializarBaralho();
        }
        Carta carta = baralho.pop();
        resultado.setCartaSorteReves(carta);
        resultado.setMensagem(resultado.getMensagem() + " Sorte/Revés: " + carta.getDescricao());

        switch (carta.getTipoEfeito()) {
            case AVANCAR_CASAS:
                moverJogador(jogador, (int) carta.getValor(), true, resultado);
                break;
            case VOLTAR_CASAS:
                moverJogador(jogador, (int) carta.getValor(), false, resultado);
                break;
            case PAGAR_BANCO:
                jogador.pagar(carta.getValor());
                break;
            case RECEBER_BANCO:
                jogador.receber(carta.getValor());
                break;
            case IR_PRISAO:
                enviarParaPrisao(jogador);
                break;
            case IR_INICIO:
                jogador.setPosicaoAtual(casaInicio);
                break;
            case PAGAR_JOGADORES:
                for (Jogador j : jogadores) {
                    if (j != jogador && j.getStatus() == JogadorStatus.ATIVO) {
                        jogador.pagar(carta.getValor());
                        j.receber(carta.getValor());
                    }
                }
                break;
            case RECEBER_JOGADORES:
                for (Jogador j : jogadores) {
                    if (j != jogador && j.getStatus() == JogadorStatus.ATIVO) {
                        j.pagar(carta.getValor());
                        jogador.receber(carta.getValor());
                    }
                }
                break;
        }
    }

    private void enviarParaPrisao(Jogador jogador) {
        filaPrisao.enqueue(new Prisioneiro(jogador));
        jogador.setPosicaoAtual(buscarNoPrisao());
    }

    private boolean estaNaPrisao(Jogador jogador) {
        for (Prisioneiro p : filaPrisao.toList()) {
            if (p.getJogador() == jogador) {
                return true;
            }
        }
        return false;
    }

    private boolean tentarSairDaPrisao(Jogador jogador, boolean deuDuplo, ResultadoTurno resultado) {
        Prisioneiro p = encontrarPrisioneiro(jogador);
        if (p == null) return true;
        
        p.incrementarTentativas();

        if (jogador.getPersonagem() == Personagem.ADVOGADO) {
            liberarDaPrisao(jogador);
            resultado.setMensagem("Advogado usou sua influência e saiu da prisão livremente!");
            return true;
        }

        if (deuDuplo) {
            liberarDaPrisao(jogador);
            resultado.setMensagem("Tirou dados iguais (" + resultado.getDado1() + ") e saiu da prisão!");
            return true;
        }

        // Fiança opcional ou automática na 3ª tentativa?
        // "na 3ª falha sai sem jogar"
        if (p.getTentativas() >= 3) {
            liberarDaPrisao(jogador);
            resultado.setMensagem("Falhou 3 vezes na prisão. Saiu, mas perdeu o turno de movimento.");
            return false;
        }

        resultado.setMensagem("Falhou ao tentar sair da prisão (Tentativa " + p.getTentativas() + "/3).");
        return false;
    }

    public void liberarDaPrisao(Jogador jogador) {
        CustomQueue<Prisioneiro> novaFila = new CustomQueue<>();
        while (!filaPrisao.isEmpty()) {
            Prisioneiro p = filaPrisao.dequeue();
            if (p.getJogador() != jogador) {
                novaFila.enqueue(p);
            }
        }
        // Devolve os outros para a fila
        while (!novaFila.isEmpty()) {
            filaPrisao.enqueue(novaFila.dequeue());
        }
    }

    private Prisioneiro encontrarPrisioneiro(Jogador jogador) {
        for (Prisioneiro p : filaPrisao.toList()) {
            if (p.getJogador() == jogador) {
                return p;
            }
        }
        return null;
    }

    private DoubleNode<CasaTabuleiro> buscarNoPrisao() {
        DoubleNode<CasaTabuleiro> atual = casaInicio;
        do {
            if (atual.getData().getTipo() == CasaTabuleiro.TipoCasa.PRISAO) {
                return atual;
            }
            atual = atual.getNext();
        } while (atual != casaInicio);
        return casaInicio;
    }

    private String descreverCasa(CasaTabuleiro casa) {
        if (casa.getTipo() == CasaTabuleiro.TipoCasa.IMOVEL) {
            return "Imóvel: " + casa.getImovel().getNome();
        }
        return casa.getTipo().toString();
    }

    private void registrarNoHistorico(String acao) {
        historico.enqueue(acao);
    }

    public void verificarFalencia(Jogador jogador, ResultadoTurno resultado) {
        if (jogador.getSaldo() < 0) {
            jogador.irA(JogadorStatus.FALIDO);
            String msgFalencia = " [FALÊNCIA] " + jogador.getNome() + " faliu!";
            resultado.setMensagem(resultado.getMensagem() == null ? msgFalencia : resultado.getMensagem() + msgFalencia);
        }
    }

    public double getMaiorAluguelCobrado() {
        return maiorAluguelCobrado;
    }

    public Imovel getImovelMaiorAluguel() {
        return imovelMaiorAluguel;
    }

    public DoubleNode<CasaTabuleiro> getCasaInicio() {
        return casaInicio;
    }

    public Jogador[] getJogadores() {
        return jogadores;
    }

    public CustomQueue<String> getHistorico() {
        return historico;
    }
}
