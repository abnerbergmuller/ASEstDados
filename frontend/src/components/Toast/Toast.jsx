import { useEffect, useState, useCallback } from 'react';
import { useGame } from '../../context/GameContext.jsx';
import './Toast.css';

function formatLogMessage(text) {
  if (!text) return '';
  return text.replace(/\b(\d+(?:\.\d+)?)\b/g, (match) => {
    const num = parseFloat(match);
    if (num >= 1000) {
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
        maximumFractionDigits: 0
      }).format(num);
    }
    return match;
  });
}

export default function Toast() {
  const { toastMessage, dismissToast } = useGame();
  const [exiting, setExiting] = useState(false);

  const handleDismiss = useCallback(() => {
    setExiting(true);
    setTimeout(() => {
      dismissToast();
      setExiting(false);
    }, 350);
  }, [dismissToast]);

  useEffect(() => {
    if (!toastMessage) return;

    setExiting(false);
    const timer = setTimeout(() => {
      handleDismiss();
    }, 3000);

    return () => clearTimeout(timer);
  }, [toastMessage, handleDismiss]);

  if (!toastMessage) return null;

  return (
    <div
      className={`toast-container ${exiting ? 'toast-exit' : 'toast-enter'}`}
      role="alert"
      aria-live="assertive"
    >
      <div className="toast-card glass-card">
        <div className="toast-accent-bar" aria-hidden="true" />

        <div className="toast-body">
          <span className="toast-icon" aria-hidden="true">⚡</span>
          <p className="toast-message">{formatLogMessage(toastMessage)}</p>
        </div>

        <button
          className="toast-close"
          onClick={handleDismiss}
          aria-label="Fechar notificação"
          title="Fechar"
        >
          ×
        </button>

        <div className="toast-progress">
          <div className={`toast-progress-bar ${exiting ? 'paused' : ''}`} />
        </div>
      </div>
    </div>
  );
}
