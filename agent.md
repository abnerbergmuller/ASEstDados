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

---
*Documento gerado automaticamente pela Arquitetura do Sistema.*
