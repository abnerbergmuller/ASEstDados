import { useState, useEffect } from 'react';
import { useGame } from '../../context/GameContext.jsx';
import BoardSquare from './BoardSquare.jsx';
import Dice from '../Dice/Dice.jsx';
import SquareDetailModal from './SquareDetailModal.jsx';
import { iniciarLeilao } from '../../api/gameApi.js';
import './Board.css';

export default function Board() {
  const { 
    tabuleiro, 
    estado, 
    rolling, 
    jogarTurno, 
    imoveis, 
    setActiveAuctionProperty 
  } = useGame();

  const [selectedImovelId, setSelectedImovelId] = useState('');
  const [auctionError, setAuctionError] = useState(null);
  const [selectedSquareForDetail, setSelectedSquareForDetail] = useState(null);

  const getPosition = (index) => {
    // Top side: col 1..8, row 1 (indices 0..7)
    // Right side: col 8, row 2..7 (indices 8..13)
    // Bottom side: col 7..1, row 7 (indices 14..20)
    // Left side: col 1, row 6..3 (indices 21..24)
    if (index <= 7) return { col: index + 1, row: 1 };
    if (index <= 13) return { col: 8, row: index - 6 };
    if (index <= 20) return { col: 8 - (index - 13), row: 7 };
    return { col: 1, row: 7 - (index - 20) };
  };

  const getPlayersHere = (square, squareIndex) => {
    if (!estado?.jogadores) return [];
    return estado.jogadores
      .filter((j) => {
        if (j.status === 'FALIDO') return false;
        
        // Use index-based matching if available (backend restarted)
        if (typeof j.posicaoIndex === 'number') {
          return j.posicaoIndex === squareIndex;
        }
        
        // Fallback to string-based matching if backend hasn't been restarted yet
        const playerPos = j.posicaoCasaNome?.trim().toUpperCase();
        if (square.tipo === 'IMOVEL') {
          const imovelName = square.imovel?.nome?.trim().toUpperCase();
          return playerPos === imovelName;
        }
        
        const squareType = square.tipo?.trim().toUpperCase();
        return playerPos === squareType;
      })
      .map((j) => j.nome);
  };

  const activePlayer = estado?.jogadores?.find(j => j.nome === estado.proximoJogadorNome);
  const isOnLeilao = activePlayer?.posicaoCasaNome === 'Leilão';
  const unownedProperties = imoveis.filter(im => !im.donoNome);

  // Set default selection when unownedProperties updates
  useEffect(() => {
    if (unownedProperties.length > 0 && !selectedImovelId) {
      setSelectedImovelId(unownedProperties[0].nome);
    }
  }, [unownedProperties, selectedImovelId]);

  const handleRoll = () => {
    if (estado?.proximoJogadorNome) {
      jogarTurno(estado.proximoJogadorNome);
    }
  };

  const handleStartAuction = async () => {
    setAuctionError(null);
    const targetImovel = unownedProperties.find(im => im.nome === selectedImovelId);
    if (!targetImovel) {
      setAuctionError('Nenhum imóvel disponível selecionado.');
      return;
    }

    try {
      await iniciarLeilao(targetImovel);
      setActiveAuctionProperty(targetImovel);
    } catch (err) {
      setAuctionError(err.message || 'Erro ao iniciar leilão.');
    }
  };

  return (
    <div className="board-container">
      {tabuleiro.map((casa, index) => {
        const { col, row } = getPosition(index);
        return (
          <BoardSquare
            key={index}
            casa={casa}
            index={index}
            playersHere={getPlayersHere(casa, index)}
            onClick={() => setSelectedSquareForDetail(casa)}
            style={{
              gridColumn: col,
              gridRow: row,
            }}
          />
        );
      })}

      {/* Decorative cell for the empty perimeter slot (row 2, col 1) */}
      <div 
        style={{
          gridColumn: 1,
          gridRow: 2,
          border: '1px solid rgba(255, 255, 255, 0.02)',
          background: 'rgba(0, 0, 0, 0.1)',
          borderRadius: 'var(--radius-sm)'
        }}
        aria-hidden="true"
      />

      <div className="board-center">
        <div className="board-center-glow" />
        
        {estado?.proximoJogadorNome ? (
          <div className="board-next-player">
            Turno de: <span>{estado.proximoJogadorNome}</span>
          </div>
        ) : (
          <div className="board-next-player">Inicializando...</div>
        )}

        <Dice />

        {estado?.proximoJogadorNome && (
          <button
            className="btn-primary board-roll-btn"
            onClick={handleRoll}
            disabled={rolling}
          >
            {rolling ? 'Rolando...' : 'Jogar Dados'}
          </button>
        )}

        {isOnLeilao && unownedProperties.length > 0 && (
          <div className="board-auction-init" style={{ marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '6px', alignItems: 'center', width: '100%' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Parou na Casa de Leilão! Escolha um Imóvel:</span>
            <div style={{ display: 'flex', gap: '8px', width: '100%', justifyContent: 'center' }}>
              <select 
                value={selectedImovelId} 
                onChange={(e) => setSelectedImovelId(e.target.value)}
                style={{ padding: '8px', borderRadius: '4px', background: 'var(--bg-secondary)', color: 'white', border: '1px solid var(--border-color)', flex: 1, maxWidth: '200px' }}
              >
                {unownedProperties.map(im => (
                  <option key={im.nome} value={im.nome}>{im.nome}</option>
                ))}
              </select>
              <button className="btn-primary" onClick={handleStartAuction} style={{ padding: '8px 16px', fontSize: '0.85rem' }}>
                Leiloar
              </button>
            </div>
            {auctionError && <span style={{ fontSize: '0.75rem', color: 'var(--accent-red)' }}>{auctionError}</span>}
          </div>
        )}
      </div>

      {selectedSquareForDetail && (
        <SquareDetailModal 
          casa={selectedSquareForDetail} 
          onClose={() => setSelectedSquareForDetail(null)} 
        />
      )}
    </div>
  );
}
