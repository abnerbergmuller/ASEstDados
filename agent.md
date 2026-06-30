# Guia de Integração - Engine de Jogo de Tabuleiro (API REST)

Este documento serve como instrução para o desenvolvimento do Front-end (React/JS) e configuração do seu Agente de IA para integração com o Back-end Java.

## 1. Regra de Ouro (Frontend Burro)
**A camada de Front-end NÃO deve calcular regras de negócio.**
*   Não calcule valores de aluguel.
*   Não gerencie o saldo dos jogadores localmente.
*   Não determine se um jogador faliu ou deve ir para a prisão.
*   **O Front-end é apenas um visualizador de estado:** ele envia comandos (requisições HTTP) e renderiza a resposta exata retornada pela API.

## 2. Contrato da API (Endpoints)

A API base está mapeada em `/api/jogo`.

| Método | Rota | Descrição | Payload / Resposta |
| :--- | :--- | :--- | :--- |
| **POST** | `/setup` | Inicializa uma nova partida. | **Body:** `{ "nomes": ["Player1", "Player2"], "personagens": ["ESPECULADOR", "NEGOCIANTE"] }` |
| **GET** | `/tabuleiro` | Retorna a lista ordenada de casas. | **Response:** `List<CasaTabuleiro>` (Tipo, Imóvel associado) |
| **GET** | `/imoveis` | Lista todos os 12 imóveis de Mitologia. | **Response:** `List<Imovel>` (Nome, Preço, Aluguel Base) |
| **POST** | `/jogar/{nome}` | Rola os dados e processa o turno. | **Response:** `ResultadoTurnoDTO` (Dados, Casa final, Efeito, Mensagem) |
| **POST** | `/comprar-imovel/{nome}` | Confirma a compra do imóvel atual. | **Response:** 200 OK |
| **POST** | `/leilao/iniciar` | Abre leilão de um imóvel. | **Body:** `Imovel` |
| **POST** | `/leilao/lance` | Registra um lance no leilão ativo. | **Body:** `{ "nomeJogador": "...", "valor": 1000 }` \| **Retorno:** `boolean` |
| **POST** | `/falencia/{nome}` | Declara falência manualmente. | **Response:** 200 OK |
| **GET** | `/historico` | Retorna as últimas 10 ações. | **Response:** `List<String>` |
| **GET** | `/relatorio` | Estatísticas de fim de jogo. | **Response:** `RelatorioFinalDTO` (Ranking por patrimônio, maior aluguel) |

## 3. Requisitos Visuais e de UX

O tema visual é **"Mitologia e Fantasia"**.

### Estrutura da Interface:
1.  **Tabuleiro (Circuito Fechado):** Renderize as casas em um formato de anel ou retângulo fechado. A primeira casa é sempre o **INÍCIO**.
2.  **Log Lateral (Histórico):** Deve haver um painel fixo mostrando o histórico das rodadas (obtido via `/api/jogo/historico`).
3.  **Modais de Evento:**
    *   Ao receber uma carta de **Sorte/Revés**, exiba um modal com a descrição da carta.
    *   Ao ativar uma **Habilidade Passiva** (ex: Advogado saindo da prisão ou Especulador recebendo bônus), exiba um alerta visual ou notificação (Toast).
4.  **Decisão de Compra:** Quando o `ResultadoTurnoDTO` vier com `aguardandoDecisaoCompra: true`, o front-end deve travar o jogo e exibir os botões "Comprar" ou "Passar".

## 4. Personagens Disponíveis
*   `ESPECULADOR`: Bônus em salário e maior taxa de imposto.
*   `NEGOCIANTE`: Paga menos aluguel.
*   `ADVOGADO`: Sai da prisão gratuitamente.
*   `CONSTRUTOR`: Seus imóveis rendem mais aluguel.

# Plano: Frontend React para Jogo de Tabuleiro Mitologico

## Contexto

O projeto ASEstDados e uma API Spring Boot para um jogo de tabuleiro estilo Monopoly com tema de mitologia. O backend esta completo mas nao possui frontend. O `agent.md` define a regra do "Frontend Burro": o frontend NAO calcula regras de negocio, apenas envia comandos HTTP e renderiza respostas da API.

Antes de construir o frontend, sao necessarios **pequenos ajustes no backend** para expor endpoints ja implementados mas nao mapeados no controller.

---

## Fase 1: Ajustes no Backend (3 alteracoes minimas)

### 1.1 Adicionar endpoints faltantes em `GameController.java`
**Arquivo:** `src/main/java/com/game/facade/GameController.java`

Adicionar 3 endpoints que ja estao implementados no `GameFacade` mas nao expostos:

```java
// Pular compra (metodo ja existe: gameFacade.pularCompra())
@PostMapping("/pular-compra/{nomeJogador}")
public void pularCompra(@PathVariable String nomeJogador) {
    gameFacade.pularCompra(nomeJogador);
}

// Estado do jogo (metodo ja existe: gameFacade.obterEstadoJogo())
@GetMapping("/estado")
public EstadoJogoDTO getEstado() {
    return gameFacade.obterEstadoJogo();
}

// Encerrar leilao
@PostMapping("/leilao/encerrar")
public void encerrarLeilao() {
    gameFacade.getLeilaoService().encerrarLeilao();
}
```

### 1.2 Corrigir serializacao do dono em `Imovel.java`
**Arquivo:** `src/main/java/com/game/domain/model/Imovel.java`

O `@JsonBackReference` no `getDono()` faz com que o dono nunca apareca no JSON. Adicionar um getter para o nome do dono:

```java
@JsonProperty("donoNome")
public String getDonoNome() {
    return dono != null ? dono.getNome() : null;
}
```

---

## Fase 2: Criar Projeto Frontend

### Setup
```bash
cd /c/Users/rmart/WebstormProjects/ASEstDados
npm create vite@latest frontend -- --template react
cd frontend && npm install
```

**Dependencias:** Nenhuma adicional. React puro + CSS.

### Estrutura de Pastas
```
frontend/src/
  api/
    gameApi.js              # Todas as chamadas HTTP
  components/
    Setup/Setup.jsx + .css        # Tela inicial (nomes + personagens)
    Board/Board.jsx + .css        # Tabuleiro em circuito retangular
    Board/BoardSquare.jsx + .css  # Casa individual do tabuleiro
    PlayerPanel/PlayerPanel.jsx + .css  # Info do jogador (saldo, imoveis, personagem)
    Dice/Dice.jsx + .css          # Dados com animacao CSS
    History/History.jsx + .css    # Painel lateral de historico
    CardModal/CardModal.jsx + .css     # Modal para cartas Sorte/Reves
    PurchasePrompt/PurchasePrompt.jsx + .css  # Botoes Comprar/Passar
    Toast/Toast.jsx + .css        # Notificacoes de habilidades passivas
    Auction/Auction.jsx + .css    # Interface de leilao
    GameOver/GameOver.jsx + .css  # Tela de relatorio final
  context/
    GameContext.jsx         # React Context com estado compartilhado
  App.jsx                   # Roteamento por fase (SETUP/PLAYING/GAME_OVER)
  index.css                 # CSS variables (tema escuro mitologico)
```

---

## Fase 3: Implementacao dos Componentes

### 3.1 Tema CSS (`index.css`)
Paleta escura/mistica com CSS variables:
- `--bg-primary: #0d0d1a` (fundo principal)
- `--bg-secondary: #1a1a2e` (paineis)
- `--accent-gold: #d4a849` (destaques)
- `--accent-purple: #7b2cbf` (magico)
- Cores por tipo de casa (inicio=dourado, imovel=azul, imposto=vermelho, etc)
- Font serif para tema fantastico

### 3.2 API Service (`api/gameApi.js`)
Wrappers finos com `fetch` para todos os endpoints:
- `setupGame(nomes, personagens)` - POST /setup
- `getTabuleiro()` - GET /tabuleiro
- `getImoveis()` - GET /imoveis
- `getEstado()` - GET /estado
- `jogar(nome)` - POST /jogar/{nome}
- `comprarImovel(nome)` - POST /comprar-imovel/{nome}
- `pularCompra(nome)` - POST /pular-compra/{nome}
- `declararFalencia(nome)` - POST /falencia/{nome}
- `getHistorico()` - GET /historico
- `getRelatorio()` - GET /relatorio
- `iniciarLeilao(imovel)`, `fazerLance(nome, valor)`, `encerrarLeilao()`

Usar Vite proxy (`/api` -> `localhost:8080`) para evitar problemas de CORS em dev.

### 3.3 Estado Compartilhado (`GameContext.jsx`)
React Context com:
```
{
  phase: 'SETUP' | 'PLAYING' | 'GAME_OVER',
  tabuleiro: [],        // de GET /tabuleiro
  imoveis: [],          // de GET /imoveis
  estado: null,         // de GET /estado (jogadores, saldos, proximo turno)
  turnoResult: null,    // de POST /jogar
  historico: [],        // de GET /historico
  relatorio: null,      // de GET /relatorio
  showCardModal: false,
  showPurchasePrompt: false,
  toastMessage: null,
}
```

**Fluxo principal do turno:**
1. Jogador clica "Jogar Dados"
2. POST /jogar/{nome} -> recebe ResultadoTurno
3. GET /estado -> atualiza saldos e posicoes de todos
4. GET /imoveis -> atualiza propriedades
5. GET /historico -> atualiza log
6. Se `aguardandoDecisaoCompra` -> mostra botoes Comprar/Passar
7. Se `cartaSorteReves` != null -> mostra modal da carta
8. Verificar mensagem para toasts de habilidades passivas

### 3.4 Componentes Principais

**Setup.jsx** - Formulario com campos para 2-4 nomes de jogadores, dropdown de personagem para cada um (ESPECULADOR, NEGOCIANTE, ADVOGADO, CONSTRUTOR com descricao em portugues). Botao "Iniciar Partida".

**Board.jsx** - Tabuleiro como retangulo fechado usando CSS Grid. 25 casas distribuidas no perimetro (7 em cima, 6 na direita, 6 em baixo, 6 na esquerda). Centro do tabuleiro mostra dados e botao de jogar.

**BoardSquare.jsx** - Casa individual com cor por tipo, nome do imovel/tipo, indicador de dono, tokens dos jogadores presentes.

**PlayerPanel.jsx** - Painel por jogador: nome, personagem + habilidade, saldo formatado, lista de imoveis. Dados vindos do GET /estado. Destaque visual no jogador da vez.

**Dice.jsx** - Dois dados com faces CSS. Animacao de rotacao por ~1s antes de mostrar resultado.

**History.jsx** - Painel lateral fixo com scroll, mostra ultimas 10 acoes do GET /historico.

**CardModal.jsx** - Overlay modal estilo carta mistica. Mostra descricao da carta Sorte/Reves. Cor verde para ganhos, vermelha para perdas. Botao "Fechar".

**PurchasePrompt.jsx** - Overlay com nome/preco do imovel. Dois botoes: "Comprar" (POST /comprar-imovel) e "Passar" (POST /pular-compra).

**Toast.jsx** - Notificacao flutuante com auto-dismiss em 3s. Acionada quando mensagem do turno indica ativacao de habilidade passiva.

**Auction.jsx** - Modal de leilao com campo de valor, botao "Dar Lance", lance atual mais alto.

**GameOver.jsx** - Tabela de ranking por patrimonio total, destaque do imovel com maior aluguel, historico final. Botao "Nova Partida".

---

## Ordem de Implementacao

1. Ajustes no backend (3 endpoints + fix JSON)
2. Scaffold Vite + tema CSS
3. `gameApi.js` (camada HTTP)
4. `GameContext.jsx` (estado)
5. `Setup.jsx` (tela inicial)
6. `Board.jsx` + `BoardSquare.jsx` (tabuleiro visual)
7. `PlayerPanel.jsx` (info dos jogadores)
8. `Dice.jsx` (dados animados)
9. Fluxo de turno no `App.jsx`
10. `PurchasePrompt.jsx` (decisao de compra)
11. `CardModal.jsx` (cartas Sorte/Reves)
12. `Toast.jsx` (notificacoes)
13. `History.jsx` (log lateral)
14. `GameOver.jsx` (relatorio final)
15. `Auction.jsx` (leilao)

---

## Verificacao

1. Iniciar backend: `mvn spring-boot:run`
2. Iniciar frontend: `cd frontend && npm run dev`
3. Testar fluxo completo: Setup -> Jogar turnos -> Comprar imoveis -> Ver historico -> Falencia -> Relatorio
4. Verificar que NENHUMA regra de negocio esta no frontend (saldos, alugueis, efeitos todos vem da API)
