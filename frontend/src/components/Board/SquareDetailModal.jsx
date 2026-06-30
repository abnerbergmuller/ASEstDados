import { useGame, PERSONAGEM_INFO } from '../../context/GameContext.jsx';
import './SquareDetailModal.css';

const SQUARES_DESC = {
  INICIO: {
    titulo: 'Portal de Início',
    desc: 'O ponto de partida da jornada mitológica. Cada vez que um jogador completa uma volta e cruza o Início, ele recebe o seu salário base do banco.',
    icone: '⛩️',
    border: 'linear-gradient(135deg, var(--accent-gold), #fefcf3)'
  },
  IMPOSTO: {
    titulo: 'Tribunal de Impostos',
    desc: 'O reino cobra tributos dos ricos. O jogador que parar aqui deve pagar obrigatoriamente 5% do seu patrimônio total (saldo + valor de imóveis comprados) ao banco.',
    icone: '⚖️',
    border: 'linear-gradient(135deg, var(--accent-red), #ff6b5a)'
  },
  RESTITUICAO: {
    titulo: 'Fonte de Restituição',
    desc: 'Uma bênção divina devolve recursos aos aventureiros. Ao parar aqui, o jogador recebe uma restituição direta de R$ 20.000 do banco.',
    icone: '⛲',
    border: 'linear-gradient(135deg, var(--accent-green), #58d68d)'
  },
  PRISAO: {
    titulo: 'Muralha da Prisão',
    desc: 'O jogador entra na fila de espera da prisão. Ficará detido por até 3 turnos, a menos que pague uma fiança de R$ 50.000 no início do seu turno, tire uma dupla nos dados, ou use a habilidade grátis de Advogado.',
    icone: '⛓️',
    border: 'linear-gradient(135deg, var(--accent-purple), #af7ac5)'
  },
  LEILAO: {
    titulo: 'Mercado de Leilão',
    desc: 'Uma oportunidade de mercado! O jogador que parar aqui pode escolher qualquer imóvel atualmente sem dono (vago) e abrir um leilão público para todos ofertarem lances.',
    icone: '🔨',
    border: 'linear-gradient(135deg, var(--accent-orange), #f5b041)'
  },
  SORTE_REVES: {
    titulo: 'Oráculo da Sorte ou Revés',
    desc: 'O destino decide! O jogador compra uma carta do baralho místico que pode conter efeitos positivos (ganhar dinheiro, avançar casas) ou negativos (pagar taxas, ir para a prisão).',
    icone: '🔮',
    border: 'linear-gradient(135deg, var(--accent-cyan), #5dade2)'
  }
};

export default function SquareDetailModal({ casa, onClose }) {
  const { playerPersonagens } = useGame();

  if (!casa) return null;

  const isImovel = casa.tipo === 'IMOVEL';

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0
    }).format(val);
  };

  // Rent calculation including Construtor bonus
  const getRentValue = (imovel) => {
    let aluguel = imovel.aluguelBase * (imovel.multiplicadorDemanda || 1.0);
    if (imovel.donoNome) {
      const ownerChar = playerPersonagens[imovel.donoNome];
      if (ownerChar === 'CONSTRUTOR') {
        aluguel *= 1.15;
      }
    }
    return aluguel;
  };

  let cardStyle = {};
  let title = '';
  let desc = '';
  let icon = '🏠';

  if (isImovel && casa.imovel) {
    title = casa.imovel.nome;
    desc = 'Uma propriedade imobiliária do reino que gera receita por aluguel de outros jogadores.';
    cardStyle = { background: 'linear-gradient(135deg, var(--accent-gold), var(--accent-purple))' };
  } else {
    const special = SQUARES_DESC[casa.tipo] || { titulo: casa.tipo, desc: '', icone: '❔', border: 'linear-gradient(135deg, var(--border-color), transparent)' };
    title = special.titulo;
    desc = special.desc;
    icon = special.icone;
    cardStyle = { background: special.border };
  }

  return (
    <div className="overlay" role="dialog" aria-modal="true" aria-labelledby="detail-title">
      <div className="detail-modal-card glass-card" style={cardStyle}>
        <div className="detail-modal-inner">
          <header className="detail-modal-header">
            <span className="detail-modal-icon" aria-hidden="true">{icon}</span>
            <h2 id="detail-title" className="detail-modal-title">{title}</h2>
            <span className="detail-modal-badge">
              {isImovel ? 'Propriedade' : 'Casa Especial'}
            </span>
          </header>

          <hr className="detail-modal-divider" />

          <p className="detail-modal-description">{desc}</p>

          {isImovel && casa.imovel && (
            <div className="detail-modal-rows">
              <div className="detail-modal-row">
                <span className="detail-modal-label">Preço de Compra:</span>
                <span className="detail-modal-val highlight-gold">
                  {formatCurrency(casa.imovel.precoCompra)}
                </span>
              </div>
              <div className="detail-modal-row">
                <span className="detail-modal-label">Aluguel Base:</span>
                <span className="detail-modal-val">
                  {formatCurrency(casa.imovel.aluguelBase)}
                </span>
              </div>
              <div className="detail-modal-row">
                <span className="detail-modal-label">Multiplicador Demanda:</span>
                <span className="detail-modal-val">
                  {(casa.imovel.multiplicadorDemanda || 1.0).toFixed(1)}x
                </span>
              </div>
              <div className="detail-modal-row">
                <span className="detail-modal-label">Aluguel Cobrado:</span>
                <span className="detail-modal-val highlight-green">
                  {formatCurrency(getRentValue(casa.imovel))}
                </span>
              </div>
              <div className="detail-modal-row">
                <span className="detail-modal-label">Proprietário:</span>
                <span className="detail-modal-val">
                  {casa.imovel.donoNome ? (
                    <span style={{ color: 'var(--accent-gold)' }}>
                      👑 {casa.imovel.donoNome}
                    </span>
                  ) : (
                    <span className="text-muted">Livre para Aquisição</span>
                  )}
                </span>
              </div>
            </div>
          )}

          {!isImovel && (
            <div className="detail-modal-rows">
              <div className="detail-modal-row">
                <span className="detail-modal-label">Tipo de Casa:</span>
                <span className="detail-modal-val highlight-gold">{casa.tipo}</span>
              </div>
              <div className="detail-modal-row">
                <span className="detail-modal-label">Ação ao Parar:</span>
                <span className="detail-modal-val highlight-green">Executar Efeito</span>
              </div>
            </div>
          )}

          <button 
            className="btn-primary detail-modal-close-btn" 
            onClick={onClose}
          >
            Fechar Detalhes
          </button>
        </div>
      </div>
    </div>
  );
}
