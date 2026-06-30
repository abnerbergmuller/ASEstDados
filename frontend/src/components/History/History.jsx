import { useGame } from '../../context/GameContext.jsx';
import './History.css';

function formatLogMessage(text) {
  if (!text) return '';
  // Format numbers >= 1000 with or without decimals into R$ currency
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

export default function History() {
  const { historico } = useGame();

  const reversed = [...historico].reverse();

  return (
    <aside className="history-panel glass-card" aria-label="Histórico de ações">
      <header className="history-header">
        <h3 className="history-title">📜 Histórico</h3>
        {historico.length > 0 && (
          <span className="history-count">{historico.length}</span>
        )}
      </header>

      <div className="history-list" role="log" aria-live="polite">
        {reversed.length === 0 ? (
          <div className="history-empty">
            <span className="history-empty-icon">🕯️</span>
            <p className="text-muted">Nenhuma ação ainda...</p>
          </div>
        ) : (
          reversed.map((entry, idx) => {
            const originalIndex = historico.length - idx;
            return (
              <div
                className="history-entry"
                key={`${originalIndex}-${entry}`}
                style={{ animationDelay: `${Math.min(idx * 40, 400)}ms` }}
              >
                <span className="history-entry-index">{originalIndex}</span>
                <p className="history-entry-text">{formatLogMessage(entry)}</p>
              </div>
            );
          })
        )}
      </div>
    </aside>
  );
}
