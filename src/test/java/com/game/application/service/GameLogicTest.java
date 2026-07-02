package com.game.application.service;

import com.game.application.dto.ResultadoTurno;
import com.game.application.facade.GameFacade;
import com.game.domain.model.*;
import com.game.infrastructure.datastructures.DoubleNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameLogicTest {

    @Autowired
    private GameFacade gameFacade;

    @Autowired
    private SetupService setupService;

    @BeforeEach
    public void setup() {
        List<String> nomes = List.of("Jogador 1", "Jogador 2");
        List<Personagem> personagens = List.of(Personagem.ESPECULADOR, Personagem.NEGOCIANTE);
        gameFacade.iniciarPartida(nomes, personagens);
    }

    @Test
    public void testSetupInicial() {
        var estado = gameFacade.obterEstadoJogo();
        assertNotNull(estado);
        assertEquals(2, estado.getJogadores().size());
        assertEquals("Jogador 1", estado.getProximoJogadorNome());
        
        // Validar integridade do tabuleiro (circular)
        var casas = gameFacade.getTabuleiroCasas();
        assertTrue(casas.size() > 0);
        assertEquals(CasaTabuleiro.TipoCasa.INICIO, casas.get(0).getTipo());
    }

    @Test
    public void testFluxoDeTurnoEMovimentacao() {
        String nomeJ1 = "Jogador 1";
        var estadoAntes = gameFacade.obterEstadoJogo();
        var resumoJ1Antes = estadoAntes.getJogadores().get(0);
        assertEquals(0, resumoJ1Antes.getPosicaoIndex());

        ResultadoTurno resultado = gameFacade.rolarDados(nomeJ1);
        
        assertNotNull(resultado);
        assertTrue(resultado.getDado1() >= 1 && resultado.getDado1() <= 6);
        assertTrue(resultado.getDado2() >= 1 && resultado.getDado2() <= 6);

        var estadoDepois = gameFacade.obterEstadoJogo();
        var resumoJ1Depois = estadoDepois.getJogadores().stream()
                .filter(j -> j.getNome().equals(nomeJ1))
                .findFirst().get();

        int passos = resultado.getDado1() + resultado.getDado2();
        // A posição deve ter mudado (a menos que tenha dado a volta completa exatamente, improvável com 25 casas)
        assertNotEquals(0, resumoJ1Depois.getPosicaoIndex());
        assertEquals(passos % 25, resumoJ1Depois.getPosicaoIndex());
    }

    @Test
    public void testCompraDeImovelEAluguel() {
        // Forçar Jogador 1 a cair em um imóvel e comprar
        var estado = gameFacade.obterEstadoJogo();
        Jogador j1 = gameFacade.buscarJogadorPorNome("Jogador 1");
        
        // Procurar o primeiro imóvel no tabuleiro
        var casas = gameFacade.getTabuleiroCasas();
        int indexImovel = -1;
        for(int i=0; i<casas.size(); i++) {
            if(casas.get(i).getTipo() == CasaTabuleiro.TipoCasa.IMOVEL) {
                indexImovel = i;
                break;
            }
        }
        
        assertTrue(indexImovel > 0, "Deve haver pelo menos um imóvel no tabuleiro");
        
        // Mover J1 manualmente para o imóvel para testar compra
        DoubleNode<CasaTabuleiro> nodeImovel = gameFacade.getTabuleiroCasasNode(indexImovel);
        j1.setPosicaoAtual(nodeImovel);
        
        double saldoAntes = j1.getSaldo();
        Imovel imovel = nodeImovel.getData().getImovel();
        double preco = imovel.getPrecoCompra();
        
        gameFacade.comprarImovelAtual(j1.getNome());
        
        assertEquals(saldoAntes - preco, j1.getSaldo(), "Saldo deve diminuir após compra");
        assertEquals(j1, imovel.getDono(), "Jogador 1 deve ser o dono");
        
        // Agora forçar Jogador 2 a cair no mesmo imóvel e pagar aluguel
        Jogador j2 = gameFacade.buscarJogadorPorNome("Jogador 2");
        j2.setPosicaoAtual(nodeImovel);
        
        double saldoAntesJ2 = j2.getSaldo();
        double saldoAntesJ1 = j1.getSaldo();
        
        // O aluguel base do primeiro imóvel (Cabana do Druida) é 1000
        // Como J2 é NEGOCIANTE, ele tem -10% no aluguel pago -> 900
        // Mas o cálculo do aluguel é feito no GameEngine.aplicarEfeitoCasa ou resolverImovel
        // Vamos simular a passagem pelo imóvel usando o motor se possível, ou apenas validar a lógica do modelo
        
        double aluguelEsperado = imovel.calcularAluguelAtual();
        assertEquals(1000, aluguelEsperado); // Sem multiplicador extra
        
        j2.pagarAluguel(aluguelEsperado);
        j1.receber(aluguelEsperado);
        
        assertEquals(saldoAntesJ2 - 900, j2.getSaldo(), "Jogador 2 (Negociante) deve pagar 900 (1000 - 10%)");
        assertEquals(saldoAntesJ1 + 1000, j1.getSaldo(), "Jogador 1 deve receber 1000");
    }

    @Test
    public void testHabilidadeEspeculadorSalario() {
        Jogador j1 = gameFacade.buscarJogadorPorNome("Jogador 1"); // ESPECULADOR
        assertEquals(Personagem.ESPECULADOR, j1.getPersonagem());
        
        double saldoAntes = j1.getSaldo();
        j1.receberSalario(100000);
        
        assertEquals(saldoAntes + 120000, j1.getSaldo(), "Especulador deve receber +20% de salário");
    }

    @Test
    public void testHabilidadeConstrutorAluguel() {
        List<String> nomes = List.of("C1", "C2");
        List<Personagem> personagens = List.of(Personagem.CONSTRUTOR, Personagem.ADVOGADO);
        gameFacade.iniciarPartida(nomes, personagens);
        
        Jogador c1 = gameFacade.buscarJogadorPorNome("C1");
        Imovel testImovel = new Imovel("Test", 100000, 1000);
        c1.adicionarImovel(testImovel);
        
        assertEquals(1150, testImovel.calcularAluguelAtual(), "Construtor deve aumentar aluguel em 15%");
    }

    @Test
    public void testRelatorioFinalIntegridade() {
        String nomeJ1 = "Jogador 1";
        gameFacade.rolarDados(nomeJ1);
        
        var relatorio = gameFacade.gerarRelatorioFimDeJogo();
        assertNotNull(relatorio);
        assertNotNull(relatorio.getRanking());
        assertEquals(2, relatorio.getRanking().size());
        assertNotNull(relatorio.getHistoricoFinal());
        assertFalse(relatorio.getHistoricoFinal().isEmpty(), "Histórico deve conter a jogada do J1");
    }
}
