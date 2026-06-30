import { useState } from 'react';
import { useGame } from '../../context/GameContext.jsx';
import { fazerLance, encerrarLeilao } from '../../api/gameApi.js';
import './Auction.css';

export default function Auction({ imovel, onClose }) {
  const { estado, refreshAll } = useGame();
  const [bids, setBids] = useState({});
  const [highestBid, setHighestBid] = useState(0);
  const [highestBidder, setHighestBidder] = useState(null);
  const [error, setError] = useState(null);

  if (!imovel) return null;

  const activePlayers = estado?.jogadores?.filter(j => j.status !== 'FALIDO') || [];

  const handleBidChange = (playerNome, val) => {
    setBids({
      ...bids,
      [playerNome]: val
    });
  };

  const submitBid = async (playerNome) => {
    setError(null);
    const val = parseFloat(bids[playerNome]) || 0;

    if (val <= highestBid) {
      setError('O lance deve ser maior do que o lance atual.');
      return;
    }

    const player = activePlayers.find(j => j.nome === playerNome);
    if (player && player.saldo < val) {
      setError(`Saldo insuficiente para ${playerNome}.`);
      return;
    }

    try {
      const success = await fazerLance(playerNome, val);
      if (success) {
        setHighestBid(val);
        setHighestBidder(playerNome);
        // Clear input for all players to avoid confusion
        setBids({});
      } else {
        setError('O servidor rejeitou o lance.');
      }
    } catch (err) {
      setError(err.message || 'Erro ao enviar lance.');
    }
  };

  const handleClose = async () => {
    try {
      await encerrarLeilao();
      await refreshAll();
      onClose();
    } catch (err) {
      setError(err.message || 'Erro ao encerrar leilão.');
    }
  };

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  return (
    <div className="overlay" role="dialog" aria-modal="true" aria-labelledby="auction-title">
      <div className="auction-card glass-card">
        <div className="auction-inner">
          <header className="auction-header">
            <h2 id="auction-title" className="auction-title">
              <span>🔨</span> Leilão de Imóvel
            </h2>
            <div className="auction-property-badge">{imovel.nome}</div>
            <p className="auction-subtitle">
              Valor de Referência: {formatCurrency(imovel.precoCompra)}
            </p>
          </header>

          <div className="auction-bid-display">
            <span className="auction-bid-label">Maior Lance Atual</span>
            <span className="auction-bid-value">
              {formatCurrency(highestBid)}
            </span>
            {highestBidder && (
              <span className="auction-bid-winner">
                Ofertado por: {highestBidder}
              </span>
            )}
          </div>

          <div className="auction-bidders">
            {activePlayers.map((player) => (
              <div key={player.nome} className="auction-bidder-row">
                <span className="auction-bidder-name">{player.nome}</span>
                
                <div className="auction-bidder-actions">
                  <input
                    type="number"
                    className="auction-bidder-input"
                    placeholder="Valor"
                    value={bids[player.nome] || ''}
                    onChange={(e) => handleBidChange(player.nome, e.target.value)}
                    min={highestBid + 1}
                    max={player.saldo}
                  />
                  <button
                    className="btn-primary auction-bidder-btn"
                    onClick={() => submitBid(player.nome)}
                  >
                    Ofertar
                  </button>
                </div>
              </div>
            ))}
          </div>

          {error && (
            <div className="text-red text-center" style={{ fontSize: '0.82rem' }}>
              {error}
            </div>
          )}

          <button 
            className="btn-danger auction-close-btn"
            onClick={handleClose}
          >
            Finalizar Leilão
          </button>
        </div>
      </div>
    </div>
  );
}
