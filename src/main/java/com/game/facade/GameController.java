package com.game.facade;

import com.game.application.dto.*;
import com.game.application.facade.GameFacade;
import com.game.application.service.SetupService;
import com.game.domain.model.CasaTabuleiro;
import com.game.domain.model.Imovel;
import com.game.domain.model.Jogador;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/jogo")
public class GameController {

    private final GameFacade gameFacade;
    private final SetupService setupService;

    public GameController(GameFacade gameFacade, SetupService setupService) {
        this.gameFacade = gameFacade;
        this.setupService = setupService;
    }

    @PostMapping("/setup")
    public void setup(@RequestBody(required = true) SetupRequest request) {
        gameFacade.iniciarPartida(request.getNomes(), request.getPersonagens());
    }

    @GetMapping("/tabuleiro")
    public List<CasaTabuleiro> getTabuleiro() {
        return gameFacade.getTabuleiroCasas();
    }

    @GetMapping("/imoveis")
    public List<Imovel> getImoveis() {
        List<Imovel> imoveis = new java.util.ArrayList<>();
        for (com.game.domain.model.CasaTabuleiro casa : gameFacade.getTabuleiroCasas()) {
            if (casa.getTipo() == com.game.domain.model.CasaTabuleiro.TipoCasa.IMOVEL && casa.getImovel() != null) {
                imoveis.add(casa.getImovel());
            }
        }
        return imoveis;
    }

    @PostMapping("/jogar/{nomeJogador}")
    public ResultadoTurno jogar(@PathVariable("nomeJogador") String nomeJogador) {
        return gameFacade.rolarDados(nomeJogador);
    }

    @PostMapping("/comprar-imovel/{nomeJogador}")
    public void comprarImovel(@PathVariable("nomeJogador") String nomeJogador) {
        gameFacade.comprarImovelAtual(nomeJogador);
    }

    @PostMapping("/leilao/iniciar")
    public void iniciarLeilao(@RequestBody(required = true) Imovel imovel) {
        gameFacade.getLeilaoService().iniciarLeilao(imovel);
    }

    @PostMapping("/leilao/lance")
    public boolean fazerLance(@RequestBody(required = true) LanceRequest request) {
        Jogador j = gameFacade.buscarJogadorPorNome(request.getNomeJogador());
        return gameFacade.getLeilaoService().receberLance(j, request.getValor());
    }

    @PostMapping("/pular-compra/{nomeJogador}")
    public void pularCompra(@PathVariable("nomeJogador") String nomeJogador) {
        gameFacade.pularCompra(nomeJogador);
    }

    @PostMapping("/falencia/{nomeJogador}")
    public void declararFalencia(@PathVariable("nomeJogador") String nomeJogador) {
        gameFacade.declararFalencia(nomeJogador);
    }

    @GetMapping("/estado")
    public EstadoJogoDTO getEstado() {
        return gameFacade.obterEstadoJogo();
    }

    @PostMapping("/leilao/encerrar")
    public void encerrarLeilao() {
        gameFacade.getLeilaoService().encerrarLeilao();
    }

    @GetMapping("/historico")
    public List<String> getHistorico() {
        RelatorioFinalDTO relatorio = gameFacade.gerarRelatorioFimDeJogo();
        return relatorio.getHistoricoFinal();
    }

    @GetMapping("/relatorio")
    public RelatorioFinalDTO getRelatorio() {
        return gameFacade.gerarRelatorioFimDeJogo();
    }
}
