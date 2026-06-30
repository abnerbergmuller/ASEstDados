import { useGame } from '../../context/GameContext.jsx';
import './BoardSquare.css';

const TIPO_NAMES = {
  INICIO: 'Início',
  IMOVEL: 'Imóvel',
  IMPOSTO: 'Imposto',
  RESTITUICAO: 'Restituição',
  PRISAO: 'Prisão',
  LEILAO: 'Leilão',
  SORTE_REVES: 'Sorte/Revés',
};

export default function BoardSquare({ casa, index, playersHere, style, onClick }) {
  const { estado, turnoResult } = useGame();

  const isImovel = casa.tipo === 'IMOVEL';
  const name = isImovel ? casa.imovel?.nome : TIPO_NAMES[casa.tipo] || casa.tipo;

  // Determine owner color if any
  let ownerColor = null;
  if (isImovel && casa.imovel?.donoNome) {
    const ownerIdx = estado?.jogadores?.findIndex(j => j.nome === casa.imovel.donoNome);
    if (ownerIdx !== -1) {
      ownerColor = `var(--player-${ownerIdx + 1})`;
    }
  }

  // Highlight if last turn ended here
  const isLandedPosition = turnoResult?.casaFinal?.imovel?.nome === casa.imovel?.nome && 
                           turnoResult?.casaFinal?.tipo === casa.tipo;

  // Header border color
  const typeColor = `var(--color-${casa.tipo.toLowerCase().replace('_', '-')})`;

  // Format currency
  const formatCurrency = (val) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  return (
    <div 
      className={`board-square ${isLandedPosition ? 'landed-highlight' : ''}`}
      onClick={onClick}
      style={{
        ...style,
        borderTop: `4px solid ${typeColor}`
      }}
    >
      {ownerColor && (
        <div 
          className="board-square-bookmark"
          style={{ backgroundColor: ownerColor }}
          title={`Dono: ${casa.imovel.donoNome}`}
        />
      )}

      <div className="board-square-body">
        <span className="board-square-name">{name}</span>

        <div className="board-square-info">
          {isImovel && casa.imovel && (
            <span className="board-square-price">
              {formatCurrency(casa.imovel.precoCompra)}
            </span>
          )}
        </div>
      </div>

      {playersHere.length > 0 && (
        <div className="board-square-players">
          {playersHere.map((playerName) => {
            const pIdx = estado?.jogadores?.findIndex(j => j.nome === playerName);
            const isTurn = playerName === estado?.proximoJogadorNome;
            return (
              <span
                key={playerName}
                className={`board-player-token p${pIdx + 1} ${isTurn ? 'active-player-token' : ''}`}
                title={playerName}
              />
            );
          })}
        </div>
      )}
    </div>
  );
}
