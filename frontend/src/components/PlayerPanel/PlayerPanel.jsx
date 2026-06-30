import { useGame, PERSONAGEM_INFO } from '../../context/GameContext.jsx';
import './PlayerPanel.css';

/**
 * Format a numeric value as Brazilian Real currency.
 * e.g. 500000 → "R$ 500.000"
 */
function formatCurrency(value) {
  return `R$ ${Number(value).toLocaleString('pt-BR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  })}`;
}

/** Player color CSS variable by card index (0‑based). */
const PLAYER_COLORS = [
  'var(--player-1)',
  'var(--player-2)',
  'var(--player-3)',
  'var(--player-4)',
];

export default function PlayerPanel() {
  const { estado, imoveis, playerPersonagens, declararFalenciaAction } = useGame();

  if (!estado || !estado.jogadores) return null;

  const { jogadores, proximoJogadorNome } = estado;

  return (
    <aside className="player-panel" aria-label="Painel de jogadores">
      <h2 className="player-panel__title">⚔️ Jogadores</h2>

      {jogadores.map((jogador, index) => {
        const isActive = jogador.nome === proximoJogadorNome;
        const isFalido = jogador.status === 'FALIDO';

        // Character info
        const personagemKey = playerPersonagens[jogador.nome];
        const charInfo = personagemKey ? PERSONAGEM_INFO[personagemKey] : null;

        // Owned properties
        const ownedProps = (imoveis || []).filter(
          (im) => im.donoNome === jogador.nome,
        );

        // Card classes
        const cardClasses = [
          'player-card',
          'glass-card',
          isActive && 'player-card--active',
          isFalido && 'player-card--falido',
        ]
          .filter(Boolean)
          .join(' ');

        const playerColor = PLAYER_COLORS[index] || PLAYER_COLORS[0];

        return (
          <article
            key={jogador.nome}
            className={cardClasses}
            style={{ animationDelay: `${index * 0.1}s` }}
            aria-current={isActive ? 'true' : undefined}
          >
            {/* Color indicator bar */}
            <div
              className="player-card__color-bar"
              style={{ background: playerColor }}
              aria-hidden="true"
            />

            <div className="player-card__body">
              {/* Name + Status */}
              <div className="player-card__header">
                <span className="player-card__name" title={jogador.nome}>
                  {jogador.nome}
                </span>
                <span
                  className={`player-card__status player-card__status--${
                    isFalido ? 'falido' : 'ativo'
                  }`}
                >
                  {isFalido ? 'Falido' : 'Ativo'}
                </span>
              </div>

              {/* Character */}
              {charInfo && (
                <>
                  <div className="player-card__character">
                    <span className="player-card__char-emoji" aria-hidden="true">
                      {charInfo.emoji}
                    </span>
                    <span className="player-card__char-name">{charInfo.nome}</span>
                  </div>
                  <span className="player-card__char-desc">{charInfo.desc}</span>
                </>
              )}

              {/* Balance */}
              <div className="player-card__balance">
                <span className="player-card__balance-label">Saldo</span>
                <span className="player-card__balance-value">
                  {formatCurrency(jogador.saldo)}
                </span>
              </div>

              <hr className="player-card__divider" />

              {/* Properties */}
              <div className="player-card__properties">
                <span className="player-card__prop-header">
                  Imóveis ({ownedProps.length})
                </span>
                {ownedProps.length > 0 ? (
                  <div className="player-card__prop-list">
                    {ownedProps.map((prop) => (
                      <span
                        key={prop.nome}
                        className="player-card__prop-tag"
                        title={prop.nome}
                      >
                        {prop.nome}
                      </span>
                    ))}
                  </div>
                ) : (
                  <span className="player-card__prop-empty">Nenhum imóvel</span>
                )}
              </div>

              {/* Bankruptcy action */}
              {!isFalido && (
                <>
                  <hr className="player-card__divider" />
                  <button
                    className="btn-danger player-card__bankrupt-btn"
                    onClick={() => declararFalenciaAction(jogador.nome)}
                  >
                    Declarar Falência
                  </button>
                </>
              )}
            </div>
          </article>
        );
      })}
    </aside>
  );
}
