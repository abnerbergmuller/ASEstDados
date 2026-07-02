# Documentação Técnica: Sistema de Gestão Imobiliária Mitológica

## 1. Resumo do projeto e regras do jogo
O projeto consiste em um simulador de jogo de tabuleiro de gestão imobiliária com temática inspirada em mitologia e fantasia. O objetivo principal é a acumulação de patrimônio através da compra de imóveis e cobrança de aluguéis, enquanto os jogadores navegam por um tabuleiro cíclico repleto de eventos aleatórios.

**Regras Básicas:**
- Cada jogador escolhe um personagem com habilidades passivas únicas (Advogado, Negociante ou Especulador).
- O movimento é determinado pela soma de dois dados de seis faces.
- Ao cair em um imóvel sem dono, o jogador pode optar por comprá-lo.
- Se cair em um imóvel de um oponente, deve pagar aluguel.
- Casas especiais como "Sorte/Revés", "Imposto" e "Prisão" adicionam camadas de estratégia e risco.
- O jogo termina quando resta apenas um jogador ativo ou após um limite de turnos, vencendo quem possuir o maior patrimônio total (saldo em conta + valor dos imóveis).

## 2. Justificativa da estrutura escolhida para gerenciar jogadores e imóveis
Para a gestão dos jogadores e seus respectivos patrimônios, foram utilizadas estruturas que priorizam a facilidade de acesso e iteração.

- **Jogadores:** No motor do jogo (`GameEngine`) e na fachada (`GameFacade`), os jogadores são armazenados em um **Array (`Jogador[]`)**. Esta escolha justifica-se pelo fato de que o número de participantes é definido no início da partida e não se altera durante a execução, permitindo acesso indexado rápido para o controle de turnos.
- **Imóveis do Jogador:** Cada objeto `Jogador` possui uma **`List<Imovel>`** (implementada como `ArrayList`). Esta estrutura dinâmica é ideal para armazenar as propriedades adquiridas, pois a quantidade de imóveis de um jogador cresce ao longo do jogo, e a API de coleções do Java facilita operações de soma de patrimônio e listagem para o relatório final.


![S1.png](../../../Users/abner/Downloads/Screenshots%20AS/S1.png)
![S2.png](../../../Users/abner/Downloads/Screenshots%20AS/S2.png)
![S3.png](../../../Users/abner/Downloads/Screenshots%20AS/S3.png)

## 3. A Lista Duplamente Ligada Circular no Tabuleiro
O tabuleiro é implementado através da classe `CircularDoublyLinkedList<CasaTabuleiro>`. Diferente de um array linear, esta estrutura permite que o jogo flua infinitamente sem a necessidade de resetar índices manualmente ao atingir o final do tabuleiro.

**Vantagens Técnicas:**
- **Circularidade:** O último nó (`tail`) aponta para o primeiro (`head`), e vice-versa, modelando perfeitamente a natureza cíclica de um tabuleiro.
- **Navegação Bidirecional:** O uso de `DoubleNode` (com referências `next` e `prev`) é fundamental para suportar cartas de "Revés" que obrigam o jogador a retroceder casas. Em uma lista simples, retroceder exigiria percorrer quase toda a lista novamente; aqui, basta acessar `getPrev()`.

![S3.png](../../../Users/abner/Downloads/Screenshots%20AS/S3.png)

## 4. A Pilha e o Baralho (Princípio LIFO)
As cartas de "Sorte ou Revés" são gerenciadas pela classe `CustomStack<Carta>`, seguindo o princípio **LIFO (Last-In, First-Out)**.

**Lógica de Implementação:**
- **Embaralhamento:** No `SetupService`, as cartas são geradas em um array, embaralhadas usando o algoritmo de Fisher-Yates e, em seguida, inseridas (`push`) na pilha.
- **Consumo:** A cada parada em uma casa de "Sorte/Revés", o motor do jogo retira a carta do topo (`pop`).
- **Reabastecimento:** Caso a pilha se esvazie (`isEmpty`), o `GameEngine` solicita ao `SetupService` a reinicialização e reembaralhamento do baralho, garantindo a continuidade do fluxo.

![S6.png](../../../Users/abner/Downloads/Screenshots%20AS/S6.png)

## 5. A Fila no Histórico e na Prisão (Princípio FIFO)
A estrutura `CustomQueue<T>` foi implementada para gerenciar processos que exigem ordem de chegada, seguindo o princípio **FIFO (First-In, First-Out)**.

- **Fila da Prisão:** Jogadores enviados para a prisão entram na `filaPrisao`. A ordem de saída respeita quem chegou primeiro, e a lógica de `tentarSairDaPrisao` gerencia as tentativas e o uso de habilidades especiais.
- **Histórico de Rodadas:** O histórico utiliza uma `CustomQueue` com capacidade limitada (N=10). Quando um novo evento é registrado via `enqueue` e a capacidade máxima é atingida, a estrutura remove automaticamente a entrada mais antiga (`dequeue`), mantendo apenas os registros mais recentes para exibição no front-end.

![S7.png](../../../Users/abner/Downloads/Screenshots%20AS/S7.png)
![S8.png](../../../Users/abner/Downloads/Screenshots%20AS/S8.png)
![S9.png](../../../Users/abner/Downloads/Screenshots%20AS/S9.png)

## 6. Integração das Habilidades Passivas
Seguindo princípios de **Arquitetura Limpa**, as habilidades passivas dos personagens foram integradas de forma a não poluir excessivamente o motor de regras (`GameEngine`).

A lógica foi distribuída da seguinte forma:
- **Especulador:** Possui lógica embutida no método `receberSalario` (bônus de 20%) e `pagarImposto` (acréscimo de 10%) dentro da própria classe `Jogador`.
- **Negociante:** O desconto de 10% no aluguel é validado tanto na classe `Jogador` quanto no `GameEngine` no momento da transação financeira entre jogadores.
- **Advogado:** Sua habilidade de saída imediata da prisão é verificada no método `tentarSairDaPrisao` da `GameEngine`.

Essa abordagem mantém as regras de negócio centradas nas entidades, facilitando a manutenção e a adição de novos personagens.

![S10.png](../../../Users/abner/Downloads/Screenshots%20AS/S10.png)
![S10p2.png](../../../Users/abner/Downloads/Screenshots%20AS/S10p2.png)

## 7. Lógica de Passagem e Retrocesso pelo Início
O sistema diferencia o sentido do movimento para aplicar as regras de bonificação salarial através do método `moverJogador`.

- **Passagem Direta (Next):** Durante o laço de movimento para frente, se o ponteiro `atual` atingir uma casa do tipo `INICIO`, o método `jogador.adicionarVolta()` e `jogador.receberSalario()` são invocados.
- **Retrocesso (Prev):** Quando o jogador se move para trás (devido a um efeito de carta), a navegação utiliza `getPrev()`. Nesse caso, o sistema não dispara a bonificação de salário, mesmo que o jogador cruze a casa inicial, respeitando a lógica clássica de jogos de tabuleiro.

![S11.png](../../../Users/abner/Downloads/Screenshots%20AS/S11.png)
![S12.png](../../../Users/abner/Downloads/Screenshots%20AS/S12.png)

## 8. Funcionalidades Adicionais e Fluxo de Negócio
O sistema conta com uma interface reativa que reflete as mudanças de estado processadas pelo Back-end. Abaixo, apresentamos os fluxos finais de transações financeiras, falência e a tela de encerramento com o ranking de jogadores.

![S13.png](../../../Users/abner/Downloads/Screenshots%20AS/S13.png)
![S14.png](../../../Users/abner/Downloads/Screenshots%20AS/S14.png)
![S16.png](../../../Users/abner/Downloads/Screenshots%20AS/S16.png)
![S17.png](../../../Users/abner/Downloads/Screenshots%20AS/S17.png)