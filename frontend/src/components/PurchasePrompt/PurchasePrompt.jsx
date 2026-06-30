import { useGame } from '../../context/GameContext.jsx';
import './PurchasePrompt.css';

export default function PurchasePrompt() {
  const { 
    showPurchasePrompt, 
    turnoResult, 
    estado, 
    comprarImovelAction, 
    pularCompraAction 
  } = useGame();

  if (!showPurchasePrompt || !turnoResult?.casaFinal?.imovel) return null;

  const imovel = turnoResult.casaFinal.imovel;
  const jogadorNome = turnoResult.jogadorNome;
  
  // Find current player in state to get their live balance
  const player = estado?.jogadores?.find(j => j.nome === jogadorNome);
  const playerBalance = player ? player.saldo : 0;
  const hasEnoughFunds = playerBalance >= imovel.precoCompra;

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  const handleBuy = () => {
    if (hasEnoughFunds) {
      comprarImovelAction(jogadorNome);
    }
  };

  const handleSkip = () => {
    pularCompraAction(jogadorNome);
  };

  return (
    <div className="overlay" role="dialog" aria-modal="true" aria-labelledby="purchase-title">
      <div className="purchase-card glass-card">
        <div className="purchase-inner">
          <h2 id="purchase-title" className="sr-only" style={{ display: 'none' }}>
            Decisão de Compra
          </h2>

          <div className="purchase-deed">
            <span className="purchase-deed-type">Escritura de Imóvel</span>
            <div className="purchase-deed-header">{imovel.nome}</div>
            
            <hr className="purchase-deed-divider" />

            <div className="purchase-deed-details">
              <div className="purchase-detail-row">
                <span className="purchase-detail-label">Preço de Compra:</span>
                <span className="purchase-detail-value highlight">
                  {formatCurrency(imovel.precoCompra)}
                </span>
              </div>
              <div className="purchase-detail-row">
                <span className="purchase-detail-label">Aluguel Base:</span>
                <span className="purchase-detail-value">
                  {formatCurrency(imovel.aluguelBase)}
                </span>
              </div>
              <div className="purchase-detail-row">
                <span className="purchase-detail-label">Demanda Atual:</span>
                <span className="purchase-detail-value">
                  {(imovel.multiplicadorDemanda || 1.0).toFixed(1)}x
                </span>
              </div>
            </div>
          </div>

          <div className="purchase-player-status">
            <span className="text-muted">Seu Saldo:</span>
            <span className={`purchase-player-balance ${hasEnoughFunds ? 'text-green' : 'insufficient'}`}>
              {formatCurrency(playerBalance)}
            </span>
          </div>

          {!hasEnoughFunds && (
            <div className="text-red text-center" style={{ fontSize: '0.8rem', width: '100%' }}>
              Saldo insuficiente para realizar esta compra.
            </div>
          )}

          <div className="purchase-actions">
            <button
              className="btn-primary"
              onClick={handleBuy}
              disabled={!hasEnoughFunds}
            >
              🏠 Comprar
            </button>
            <button
              className="btn-secondary"
              onClick={handleSkip}
            >
              ⏭️ Passar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
