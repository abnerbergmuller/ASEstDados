import { useGame } from '../../context/GameContext.jsx';
import './GameOver.css';

function formatLogMessage(text) {
  if (!text) return '';
  return text.replace(/\b(\d+(?:\.\d+)?)\b/g, (match) => {
    const num = parseFloat(match);
    if (num >= 1000) {
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
        maximumFractionDigits: 0
      }).format(num);
    }
    return match;
  });
}

export default function GameOver() {
  const { relatorio, novaPartida } = useGame();

  if (!relatorio) return null;

  const ranking = relatorio.ranking || [];
  const winner = ranking[0];

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  return (
    <div className="gameover-container">
      <div className="gameover-card glass-card">
        <header className="gameover-header">
          <h1 className="gameover-title">Fim de Jogo</h1>
          <p className="gameover-subtitle">Relatório Final da Partida</p>
        </header>

        {winner && (
          <div className="gameover-winner-banner">
            <span className="gameover-crown" aria-hidden="true">👑</span>
            <div className="gameover-winner-name">{winner.nome}</div>
            <div className="gameover-winner-desc">
              Grande Campeão com patrimônio total de {formatCurrency(winner.patrimonioTotal)}!
            </div>
          </div>
        )}

        <section className="gameover-results-section">
          <h2 className="gameover-section-title">Classificação Geral</h2>
          <div className="gameover-table-wrapper">
            <table className="gameover-table">
              <thead>
                <tr>
                  <th>Posição</th>
                  <th>Jogador</th>
                  <th>Patrimônio Total</th>
                  <th>Voltas</th>
                </tr>
              </thead>
              <tbody>
                {ranking.map((jogador, index) => {
                  const isWinner = index === 0;
                  return (
                    <tr key={jogador.nome} className={isWinner ? 'winner-row' : ''}>
                      <td>{index + 1}º</td>
                      <td>
                        {jogador.nome} {isWinner ? '🏆' : ''}
                      </td>
                      <td>{formatCurrency(jogador.patrimonioTotal)}</td>
                      <td>{jogador.voltasCompletas} voltas</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </section>

        <section className="gameover-results-section">
          <h2 className="gameover-section-title">Destaques da Partida</h2>
          <div className="gameover-stats-grid">
            {relatorio.imovelMaiorAluguelNome ? (
              <div className="gameover-stat-card">
                <span className="gameover-stat-label">Maior Aluguel da Partida</span>
                <span className="gameover-stat-value">
                  {formatCurrency(relatorio.maiorAluguelValor)}
                </span>
                <span className="gameover-stat-subtext">
                  Propriedade: {relatorio.imovelMaiorAluguelNome}
                </span>
              </div>
            ) : (
              <div className="gameover-stat-card">
                <span className="gameover-stat-label">Maior Aluguel da Partida</span>
                <span className="gameover-stat-value">R$ 0</span>
                <span className="gameover-stat-subtext">
                  Nenhum aluguel cobrado.
                </span>
              </div>
            )}
          </div>
        </section>

        <button 
          className="btn-primary gameover-restart-btn"
          onClick={novaPartida}
        >
          Jogar Novamente
        </button>
      </div>
    </div>
  );
}
