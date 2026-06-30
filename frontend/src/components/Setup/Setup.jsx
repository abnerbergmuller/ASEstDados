import { useState } from 'react';
import { useGame, PERSONAGEM_INFO } from '../../context/GameContext.jsx';
import './Setup.css';

export default function Setup() {
  const { iniciarPartida } = useGame();
  const [players, setPlayers] = useState([
    { nome: 'Jogador 1', personagem: 'ESPECULADOR' },
    { nome: 'Jogador 2', personagem: 'NEGOCIANTE' },
  ]);
  const [error, setError] = useState(null);

  const handleNameChange = (index, value) => {
    const updated = [...players];
    updated[index].nome = value;
    setPlayers(updated);
  };

  const handleCharChange = (index, value) => {
    const updated = [...players];
    updated[index].personagem = value;
    setPlayers(updated);
  };

  const addPlayer = () => {
    if (players.length >= 4) return;
    const nextIdx = players.length + 1;
    const defaultChars = ['ADVOGADO', 'CONSTRUTOR', 'ESPECULADOR', 'NEGOCIANTE'];
    const pChar = defaultChars[(nextIdx - 1) % 4];
    setPlayers([...players, { nome: `Jogador ${nextIdx}`, personagem: pChar }]);
    setError(null);
  };

  const removePlayer = (index) => {
    if (players.length <= 2) return;
    const updated = players.filter((_, i) => i !== index);
    setPlayers(updated);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const nomes = players.map(p => p.nome.trim());
    const personagens = players.map(p => p.personagem);

    // Validation
    if (nomes.some(n => !n)) {
      setError('Todos os jogadores precisam de um nome preenchido.');
      return;
    }

    const uniqueNames = new Set(nomes);
    if (uniqueNames.size !== nomes.length) {
      setError('Os nomes dos jogadores devem ser únicos.');
      return;
    }

    try {
      await iniciarPartida(nomes, personagens);
    } catch (err) {
      setError(err.message || 'Erro ao iniciar partida.');
    }
  };

  return (
    <div className="setup-container">
      <div className="setup-bg-effect" />

      <div className="setup-card glass-card">
        <header className="setup-header">
          <h1 className="setup-title">Reinos Mitológicos</h1>
          <p className="setup-subtitle">Jogo de Tabuleiro</p>
        </header>

        <form onSubmit={handleSubmit} className="setup-form">
          {players.map((player, index) => {
            const charInfo = PERSONAGEM_INFO[player.personagem];
            return (
              <div key={index} className="setup-player-card">
                <div className={`setup-player-badge p${index + 1}`}>
                  {index + 1}
                </div>
                
                <div className="setup-fields">
                  <div className="setup-row-top">
                    <input
                      type="text"
                      className="setup-input"
                      value={player.nome}
                      onChange={(e) => handleNameChange(index, e.target.value)}
                      placeholder={`Nome do Jogador ${index + 1}`}
                      maxLength={16}
                    />

                    <select
                      className="setup-select"
                      value={player.personagem}
                      onChange={(e) => handleCharChange(index, e.target.value)}
                    >
                      {Object.keys(PERSONAGEM_INFO).map((key) => (
                        <option key={key} value={key}>
                          {PERSONAGEM_INFO[key].nome}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="setup-ability-desc">
                    <span>{charInfo.emoji}</span>
                    <span>{charInfo.desc}</span>
                  </div>
                </div>

                {players.length > 2 && (
                  <span
                    className="setup-remove-btn"
                    onClick={() => removePlayer(index)}
                    title="Remover Jogador"
                    role="button"
                  >
                    ×
                  </span>
                )}
              </div>
            );
          })}

          <div className="setup-actions-row">
            {players.length < 4 ? (
              <button
                type="button"
                className="setup-add-btn"
                onClick={addPlayer}
              >
                + Adicionar Jogador
              </button>
            ) : (
              <span className="text-muted" style={{ fontSize: '0.8rem' }}>
                Máximo de 4 jogadores atingido.
              </span>
            )}
          </div>

          {error && <div className="setup-error">{error}</div>}

          <button type="submit" className="btn-primary setup-submit-btn">
            Iniciar Partida
          </button>
        </form>
      </div>
    </div>
  );
}
