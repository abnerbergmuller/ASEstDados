import { useGame } from './context/GameContext.jsx'
import Setup from './components/Setup/Setup.jsx'
import Board from './components/Board/Board.jsx'
import PlayerPanel from './components/PlayerPanel/PlayerPanel.jsx'
import History from './components/History/History.jsx'
import CardModal from './components/CardModal/CardModal.jsx'
import PurchasePrompt from './components/PurchasePrompt/PurchasePrompt.jsx'
import Toast from './components/Toast/Toast.jsx'
import GameOver from './components/GameOver/GameOver.jsx'
import Auction from './components/Auction/Auction.jsx'
import './App.css'

export default function App() {
  const { 
    phase, 
    turnoResult, 
    terminarJogo, 
    estado, 
    activeAuctionProperty, 
    setActiveAuctionProperty 
  } = useGame()

  if (phase === 'SETUP') return <Setup />
  if (phase === 'GAME_OVER') return <GameOver />

  // Check if game should end (only 1 active player)
  const activePlayers = estado?.jogadores?.filter(j => j.status !== 'FALIDO') || []
  const totalPlayers = estado?.jogadores?.length || 0
  const shouldShowEndButton = totalPlayers > 1 && activePlayers.length <= 1

  return (
    <div className="game-layout">
      <PlayerPanel />

      <div className="game-main">
        <Board />
        {shouldShowEndButton && (
          <div className="game-end-bar">
            <p>Apenas {activePlayers.length} jogador(es) restante(s).</p>
            <button className="end-game-btn" onClick={terminarJogo}>
              Ver Relatorio Final
            </button>
          </div>
        )}
      </div>

      <History />

      {/* Overlays */}
      {turnoResult?.cartaSorteReves && <CardModal />}
      <PurchasePrompt />
      <Toast />
      {activeAuctionProperty && (
        <Auction 
          imovel={activeAuctionProperty} 
          onClose={() => setActiveAuctionProperty(null)} 
        />
      )}
    </div>
  )
}
