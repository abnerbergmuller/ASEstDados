import { useMemo } from 'react';
import { useGame } from '../../context/GameContext.jsx';
import './Dice.css';

/**
 * Pip layout map for a standard die.
 * Each value (1-6) maps to which cells in a 3×3 grid should show a pip.
 *
 * Grid positions:
 *   0 1 2
 *   3 4 5
 *   6 7 8
 */
const PIP_POSITIONS = {
  1: [4],
  2: [2, 6],
  3: [2, 4, 6],
  4: [0, 2, 6, 8],
  5: [0, 2, 4, 6, 8],
  6: [0, 2, 3, 5, 6, 8],
};

/**
 * Random pip positions shown while dice are rolling (visual only).
 * Cycles through to create an animated "tumbling" effect.
 */
const ROLLING_FACES = [3, 5, 1, 6, 2, 4];

function DieFace({ value, rolling, variant }) {
  // During rolling, cycle through random values for visual effect
  const displayValue = rolling ? null : value;

  // Blank / unknown state
  if (!rolling && !displayValue) {
    return (
      <div
        className="die die--blank"
        role="img"
        aria-label="Dado aguardando"
      >
        <span className="die__question">?</span>
      </div>
    );
  }

  // Rolling state — show all 6 pips with flashing animation
  if (rolling) {
    const rollingValue = ROLLING_FACES[Math.floor(Math.random() * ROLLING_FACES.length)];
    const activePips = PIP_POSITIONS[rollingValue] || [];
    return (
      <div
        className={`die ${variant === 'alt' ? 'die--rolling-alt' : 'die--rolling'}`}
        role="img"
        aria-label="Dado rolando"
      >
        {Array.from({ length: 9 }, (_, i) => (
          <span
            key={i}
            className={`die__pip ${
              activePips.includes(i) ? 'die__pip--visible' : 'die__pip--hidden'
            }`}
          />
        ))}
      </div>
    );
  }

  // Result state — show actual pips
  const activePips = PIP_POSITIONS[displayValue] || [];
  return (
    <div
      className="die die--landed"
      role="img"
      aria-label={`Dado mostrando ${displayValue}`}
    >
      {Array.from({ length: 9 }, (_, i) => (
        <span
          key={i}
          className={`die__pip ${
            activePips.includes(i) ? 'die__pip--visible' : 'die__pip--hidden'
          }`}
        />
      ))}
    </div>
  );
}

export default function Dice() {
  const { rolling, turnoResult } = useGame();

  const dado1 = turnoResult?.dado1 ?? null;
  const dado2 = turnoResult?.dado2 ?? null;
  const soma = turnoResult?.somaDados ?? null;

  const isDouble = useMemo(
    () => dado1 != null && dado2 != null && dado1 === dado2,
    [dado1, dado2]
  );

  return (
    <div className="dice-container" aria-live="polite">
      {/* Dice pair */}
      <div className="dice-row">
        <DieFace value={dado1} rolling={rolling} variant="main" />
        <span className="dice-plus" aria-hidden="true">+</span>
        <DieFace value={dado2} rolling={rolling} variant="alt" />
      </div>

      {/* Sum display */}
      <div
        className={`dice-sum ${rolling ? 'dice-sum--rolling' : ''} ${
          isDouble && !rolling ? 'dice-sum--double' : ''
        }`}
      >
        <span className="dice-sum__label">Total</span>
        <span className="dice-sum__value">
          {rolling ? '…' : soma != null ? soma : '–'}
        </span>
        {isDouble && !rolling && (
          <span className="dice-sum__double-badge">Dupla!</span>
        )}
      </div>
    </div>
  );
}
