import { createContext, useContext, useState, useCallback } from 'react';
import * as api from '../api/gameApi.js';

const GameContext = createContext(null);

const PERSONAGEM_INFO = {
  ESPECULADOR: { nome: 'Especulador', desc: '+20% salário, +10% imposto', emoji: '💰' },
  NEGOCIANTE:  { nome: 'Negociante',  desc: '-10% no aluguel pago',       emoji: '🤝' },
  ADVOGADO:    { nome: 'Advogado',    desc: '1 saída grátis da prisão',   emoji: '⚖️' },
  CONSTRUTOR:  { nome: 'Construtor',  desc: 'Aluguel dos imóveis +15%',   emoji: '🏗️' },
};

export { PERSONAGEM_INFO };

export function GameProvider({ children }) {
  const [phase, setPhase] = useState('SETUP');           // SETUP | PLAYING | GAME_OVER
  const [tabuleiro, setTabuleiro] = useState([]);
  const [imoveis, setImoveis] = useState([]);
  const [estado, setEstado] = useState(null);
  const [turnoResult, setTurnoResult] = useState(null);
  const [historico, setHistorico] = useState([]);
  const [relatorio, setRelatorio] = useState(null);
  const [showPurchasePrompt, setShowPurchasePrompt] = useState(false);
  const [showCardModal, setShowCardModal] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);
  const [rolling, setRolling] = useState(false);
  const [playerPersonagens, setPlayerPersonagens] = useState({});
  const [activeAuctionProperty, setActiveAuctionProperty] = useState(null);

  /* ── Refresh helpers ────────────────────── */
  const refreshEstado = useCallback(async () => {
    const e = await api.getEstado();
    setEstado(e);
    return e;
  }, []);

  const refreshImoveis = useCallback(async () => {
    const im = await api.getImoveis();
    setImoveis(im);
    return im;
  }, []);

  const refreshHistorico = useCallback(async () => {
    const h = await api.getHistorico();
    setHistorico(h);
    return h;
  }, []);

  const refreshTabuleiro = useCallback(async () => {
    const tab = await api.getTabuleiro();
    setTabuleiro(tab);
    return tab;
  }, []);

  const refreshAll = useCallback(async () => {
    await Promise.all([refreshEstado(), refreshImoveis(), refreshHistorico(), refreshTabuleiro()]);
  }, [refreshEstado, refreshImoveis, refreshHistorico, refreshTabuleiro]);

  /* ── Setup ──────────────────────────────── */
  const iniciarPartida = useCallback(async (nomes, personagens) => {
    await api.setupGame(nomes, personagens);

    // Store personagem mapping for display
    const mapping = {};
    nomes.forEach((n, i) => { mapping[n] = personagens[i]; });
    setPlayerPersonagens(mapping);

    const tab = await api.getTabuleiro();
    setTabuleiro(tab);
    await refreshAll();
    setPhase('PLAYING');
  }, [refreshAll]);

  /* ── Turn flow ──────────────────────────── */
  const jogarTurno = useCallback(async (nome) => {
    setRolling(true);

    // Wait for dice animation
    await new Promise(r => setTimeout(r, 1200));

    const result = await api.jogar(nome);
    setTurnoResult(result);
    setRolling(false);

    // Refresh state from backend
    await refreshAll();

    // Check for card event
    if (result.cartaSorteReves) {
      setShowCardModal(true);
    }

    // Check for purchase decision
    if (result.aguardandoDecisaoCompra) {
      setShowPurchasePrompt(true);
    }

    // Check for passive ability toast
    if (result.mensagem) {
      const msg = result.mensagem.toLowerCase();
      const passiveKeywords = ['bônus', 'bonus', 'habilidade', 'passiva', 'especulador', 'negociante', 'advogado', 'construtor', 'grátis', 'gratis'];
      if (passiveKeywords.some(kw => msg.includes(kw))) {
        setToastMessage(result.mensagem);
      }
    }

    return result;
  }, [refreshAll]);

  /* ── Purchase ───────────────────────────── */
  const comprarImovelAction = useCallback(async (nome) => {
    await api.comprarImovel(nome);
    setShowPurchasePrompt(false);
    await refreshAll();
  }, [refreshAll]);

  const pularCompraAction = useCallback(async (nome) => {
    await api.pularCompra(nome);
    setShowPurchasePrompt(false);
    await refreshAll();
  }, [refreshAll]);

  /* ── Bankruptcy ─────────────────────────── */
  const declararFalenciaAction = useCallback(async (nome) => {
    await api.declararFalencia(nome);
    await refreshAll();
  }, [refreshAll]);

  /* ── End game ───────────────────────────── */
  const terminarJogo = useCallback(async () => {
    const rel = await api.getRelatorio();
    setRelatorio(rel);
    setPhase('GAME_OVER');
  }, []);

  /* ── Dismiss modals ─────────────────────── */
  const dismissCardModal = useCallback(() => setShowCardModal(false), []);
  const dismissToast = useCallback(() => setToastMessage(null), []);

  /* ── New game ───────────────────────────── */
  const novaPartida = useCallback(() => {
    setPhase('SETUP');
    setTabuleiro([]);
    setImoveis([]);
    setEstado(null);
    setTurnoResult(null);
    setHistorico([]);
    setRelatorio(null);
    setShowPurchasePrompt(false);
    setShowCardModal(false);
    setToastMessage(null);
    setRolling(false);
    setPlayerPersonagens({});
    setActiveAuctionProperty(null);
  }, []);

  const value = {
    // State
    phase, tabuleiro, imoveis, estado, turnoResult, historico, relatorio,
    showPurchasePrompt, showCardModal, toastMessage, rolling, playerPersonagens,
    activeAuctionProperty, setActiveAuctionProperty,

    // Actions
    iniciarPartida, jogarTurno, comprarImovelAction, pularCompraAction,
    declararFalenciaAction, terminarJogo, dismissCardModal, dismissToast,
    novaPartida, refreshAll,
  };

  return <GameContext.Provider value={value}>{children}</GameContext.Provider>;
}

export function useGame() {
  const ctx = useContext(GameContext);
  if (!ctx) throw new Error('useGame must be used within GameProvider');
  return ctx;
}
