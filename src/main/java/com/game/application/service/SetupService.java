package com.game.application.service;

import com.game.domain.model.Carta;
import com.game.domain.model.CasaTabuleiro;
import com.game.domain.model.Imovel;
import com.game.infrastructure.datastructures.CircularDoublyLinkedList;
import com.game.infrastructure.datastructures.CustomStack;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * Serviço responsável pela configuração inicial do jogo.
 */
@Service
public class SetupService {

    private final Random random = new Random();

    @PostConstruct
    public void init() {
        // Inicialização silenciosa da carga mestre de dados
        getImoveisFantasy();
    }

    /**
     * Retorna a lista de imóveis pré-cadastrados (Tema: Mitologia e Fantasia).
     */
    public List<Imovel> getImoveisFantasy() {
        return List.of(
            new Imovel("Cabana do Druida", 200000, 1000),
            new Imovel("Torre do Mago", 380000, 1900),
            new Imovel("Fortaleza do Guardião", 470000, 2350),
            new Imovel("Castelo do Dragão", 620000, 3100),
            new Imovel("Palácio da Fênix", 800000, 4000),
            new Imovel("Gruta do Oráculo", 250000, 1250),
            new Imovel("Templo de Atena", 550000, 2750),
            new Imovel("Mansão do Vampiro", 430000, 2150),
            new Imovel("Cidadela dos Elfos", 700000, 3500),
            new Imovel("Covil do Minotauro", 210000, 1050),
            new Imovel("Palácios dos Titãs", 950000, 4750),
            new Imovel("Santuário do Grifo", 330000, 1650)
        );
    }

    /**
     * Inicializa o baralho com 12 cartas variadas.
     * @return Uma CustomStack contendo as cartas embaralhadas.
     */
    public CustomStack<Carta> inicializarBaralho() {
        Carta[] cartas = new Carta[12];
        
        // Ganhos / Avanço (6)
        cartas[0] = new Carta("Prêmio de Produtividade: Receba 200k", Carta.TipoEfeito.RECEBER_BANCO, 200000);
        cartas[1] = new Carta("Vento Favorável: Avance 3 casas", Carta.TipoEfeito.AVANCAR_CASAS, 3);
        cartas[2] = new Carta("Portal Místico: Vá para o Início", Carta.TipoEfeito.IR_INICIO, 0);
        cartas[3] = new Carta("Banquete de Celebração: Receba 50k de cada jogador", Carta.TipoEfeito.RECEBER_JOGADORES, 50000);
        cartas[4] = new Carta("Herança Inesperada: Receba 100k", Carta.TipoEfeito.RECEBER_BANCO, 100000);
        cartas[5] = new Carta("Passo Largo: Avance 5 casas", Carta.TipoEfeito.AVANCAR_CASAS, 5);
        
        // Perdas / Retrocesso (6)
        cartas[6] = new Carta("Taxa de Manutenção: Pague 150k", Carta.TipoEfeito.PAGAR_BANCO, 150000);
        cartas[7] = new Carta("Rodada de Bebidas: Pague 40k a cada jogador", Carta.TipoEfeito.PAGAR_JOGADORES, 40000);
        cartas[8] = new Carta("Maldição Temporária: Vá para a Prisão", Carta.TipoEfeito.IR_PRISAO, 0);
        cartas[9] = new Carta("Névoa da Confusão: Volte 3 casas", Carta.TipoEfeito.VOLTAR_CASAS, 3);
        cartas[10] = new Carta("Imposto de Luxo: Pague 100k", Carta.TipoEfeito.PAGAR_BANCO, 100000);
        cartas[11] = new Carta("Tropeço: Volte 2 casas", Carta.TipoEfeito.VOLTAR_CASAS, 2);

        embaralhar(cartas);

        CustomStack<Carta> pilha = new CustomStack<>();
        for (Carta carta : cartas) {
            pilha.push(carta);
        }
        return pilha;
    }

    /**
     * Inicializa o tabuleiro intercalando os imóveis com casas especiais.
     * @param imoveis Lista com os 12 imóveis de Mitologia e Fantasia.
     * @return Uma CircularDoublyLinkedList representando o tabuleiro.
     */
    public CircularDoublyLinkedList<CasaTabuleiro> inicializarTabuleiro(List<Imovel> imoveis) {
        CircularDoublyLinkedList<CasaTabuleiro> tabuleiro = new CircularDoublyLinkedList<>();
        
        // Casa INICIO
        tabuleiro.add(new CasaTabuleiro(CasaTabuleiro.TipoCasa.INICIO));
        
        int imovelIndex = 0;
        CasaTabuleiro.TipoCasa[] especiais = {
            CasaTabuleiro.TipoCasa.SORTE_REVES,
            CasaTabuleiro.TipoCasa.IMPOSTO,
            CasaTabuleiro.TipoCasa.RESTITUICAO,
            CasaTabuleiro.TipoCasa.PRISAO,
            CasaTabuleiro.TipoCasa.LEILAO
        };
        int especialIndex = 0;

        while (imovelIndex < imoveis.size()) {
            // Adiciona um imóvel
            tabuleiro.add(new CasaTabuleiro(imoveis.get(imovelIndex++)));
            
            // Adiciona uma casa especial intercalada
            tabuleiro.add(new CasaTabuleiro(especiais[especialIndex]));
            
            especialIndex = (especialIndex + 1) % especiais.length;
        }

        return tabuleiro;
    }

    private void embaralhar(Carta[] cartas) {
        for (int i = cartas.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Carta temp = cartas[index];
            cartas[index] = cartas[i];
            cartas[i] = temp;
        }
    }
}
