import { useGame } from '../../context/GameContext.jsx';
import './CardModal.css';

export default function CardModal() {
  const { turnoResult, showCardModal, dismissCardModal } = useGame();

  if (!showCardModal || !turnoResult?.cartaSorteReves) return null;

  const carta = turnoResult.cartaSorteReves;
  const isPositive = [
    'RECEBER_BANCO',
    'RECEBER_JOGADORES',
    'AVANCAR_CASAS',
    'IR_INICIO'
  ].includes(carta.tipoEfeito);

  const formattedValue = new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    maximumFractionDigits: 0
  }).format(carta.valor);

  return (
    <div className="overlay" role="dialog" aria-modal="true" aria-labelledby="card-title">
      <div className={`card-modal__card ${isPositive ? 'card-modal__card--positive' : 'card-modal__card--negative'} glass-card`}>
        <div className="card-modal__inner">
          <div className="card-modal__shimmer" />
          
          <div className="card-modal__particles">
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
            <span className={`card-modal__particle ${isPositive ? 'card-modal__particle--positive' : 'card-modal__particle--negative'}`} />
          </div>

          <span 
            className={`card-modal__label ${isPositive ? 'card-modal__label--positive' : 'card-modal__label--negative'}`}
          >
            {isPositive ? 'Sorte' : 'Revés'}
          </span>

          <span className="card-modal__icon" aria-hidden="true">
            {isPositive ? '🍀' : '💀'}
          </span>

          <hr className={`card-modal__divider ${isPositive ? 'card-modal__divider--positive' : 'card-modal__divider--negative'}`} />

          <h2 id="card-title" className="sr-only" style={{ display: 'none' }}>
            Carta de {isPositive ? 'Sorte' : 'Revés'}
          </h2>
          
          <p className="card-modal__description">{carta.descricao}</p>

          {carta.valor > 0 && (
            <p className={`card-modal__value ${isPositive ? 'card-modal__value--positive' : 'card-modal__value--negative'}`}>
              {isPositive ? '+' : '-'} {formattedValue}
            </p>
          )}

          <button 
            className="btn-primary card-modal__close-btn" 
            onClick={dismissCardModal}
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
