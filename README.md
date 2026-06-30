# Reinos Mitológicos - Jogo de Tabuleiro Estratégico

Um jogo de tabuleiro estratégico digital em formato de circuito retangular fechado (25 casas) com temática mitológica. De 2 a 4 jogadores escolhem cargos com habilidades passivas únicas, lançam dados, adquirem propriedades, cobram aluguel com base em multiplicadores de demanda dinâmica e enfrentam o Oráculo de Sorte/Revés.

---

## 🚀 Como Rodar o Projeto

Após clonar o repositório, abra o terminal na raiz do projeto e execute o comando abaixo para executar o backend e o frontend simultaneamente:

```powershell
Start-Process mvn "spring-boot:run"; cd frontend; npm run dev
```

*Este comando iniciará o servidor backend Java Spring Boot em uma janela separada e executará o servidor de desenvolvimento Vite (React) no terminal ativo.*

---

## 🛠️ Tecnologias do Projeto

### Backend
- **Java 17**: Linguagem principal que provê recursos modernos e tipagem forte para o núcleo de regras.
- **Spring Boot 3.2.0**: Framework para orquestração da API REST, injeção de dependências e inversão de controle.
- **Spring Web**: Módulo usado para expor os endpoints HTTP e receber as chamadas AJAX do cliente.
- **Jackson**: Biblioteca padrão de serialização utilizada para traduzir entidades e DTOs Java para JSON.
- **Estruturas de Dados Customizadas**: Implementações estruturais manuais livres de bibliotecas prontas para gerenciar o tabuleiro circular e as filas.

### Frontend
- **React 19**: Biblioteca para renderização declarativa de interfaces baseada em componentes reativos.
- **Vite 8**: Ferramenta de build e servidor de desenvolvimento local ultrarrápido com Hot Module Replacement (HMR).
- **Vanilla CSS**: Estilização modular com variáveis globais (custom properties), animações `@keyframes` nativas e controle de layout responsivo (Flexbox/Grid).
- **Fetch API**: Cliente nativo do navegador usado para fazer requisições assíncronas assíncronas aos endpoints REST do backend.

---

## 📂 Estrutura de Diretórios e Arquitetura

O projeto é dividido em uma estrutura desacoplada em duas pastas principais: `src/` (para o backend Java) e `frontend/` (para a aplicação React).

### 🖥️ Estrutura do Backend (Java)

O backend segue um modelo de arquitetura em camadas bem definidas:

```
src/main/java/com/game/
│
├── domain/                  # Entidades de Domínio e Regras de Negócio Core
│   └── model/
│       ├── Jogador.java     # Estado do jogador (saldo, posição, status de falência)
│       ├── Imovel.java      # Propriedades, demanda e cálculo dinâmico de aluguéis
│       ├── Personagem.java  # Enumeração dos heróis do jogo e suas regras passivas
│       └── ...
│
├── infrastructure/          # Estruturas de Dados Fundamentais (Mapeamento em Memória)
│   └── datastructures/
│       ├── CircularLinkedList.java # Lista circular ligada simulando o perímetro do tabuleiro
│       ├── CustomQueue.java        # Fila de tamanho dinâmico (logs do histórico e prisão)
│       └── Node.java               # Elemento base para ligação de nós
│
├── application/             # Lógica e Coordenação do Fluxo de Jogo
│   ├── dto/                 # Classes DTOs que formatam as respostas JSON
│   ├── service/
│   │   ├── GameEngine.java  # Motor de turno (movimentação, aluguel, taxas e oráculo)
│   │   └── SetupService.java # Setup de criação de casas e disposição de cartas
│   └── facade/
│       └── GameFacade.java  # Ponto de sincronização global do estado da rodada
│
└── facade/                  # Camada de Apresentação
    └── GameController.java  # Controlador REST expondo os serviços na rede
```

### 🎨 Estrutura do Frontend (React)

O frontend é composto por componentes modulares acoplados a um contexto global que gerencia e sincroniza o estado recebido da API:

```
frontend/
│
├── src/
│   ├── api/
│   │   └── gameApi.js       # Camada de integração HTTP que faz chamadas à API REST (/setup, /jogar)
│   │
│   ├── components/          # Componentes modulares e reutilizáveis de interface
│   │   ├── Setup/           # Formulário de entrada de nomes e heróis
│   │   ├── Board/           # Tabuleiro, dado digital e card de detalhes das casas
│   │   ├── PlayerPanel/     # Sidebar que monitora ativos, saldo e posses dos jogadores
│   │   ├── History/         # Feed vertical de logs do jogo em tempo real
│   │   ├── CardModal/       # Overlay místico para as cartas compradas de Sorte/Revés
│   │   ├── PurchasePrompt/  # Modal de decisão de compra ao parar em imóveis vagos
│   │   ├── Auction/         # Sistema de leilões dinâmicos em tempo real
│   │   └── GameOver/        # Tela final de relatório da partida, estatísticas e pódio
│   │
│   ├── context/
│   │   └── GameContext.jsx  # Contexto React unificado que sincroniza dados da API REST com o estado local
│   │
│   ├── App.jsx              # Arquivo de roteamento entre telas (SETUP -> PLAYING -> GAME_OVER)
│   ├── index.css            # Folha de estilo base contendo tokens de cores, transições e variáveis
│   └── main.jsx             # Ponto de entrada que monta o React na árvore DOM do HTML
```