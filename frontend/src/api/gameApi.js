const BASE = '/api/jogo';

async function request(url, options = {}) {
  const res = await fetch(`${BASE}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(text || `HTTP ${res.status}`);
  }
  const contentType = res.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return res.json();
  }
  return null;
}

/* ── Setup ─────────────────────────────────── */
export function setupGame(nomes, personagens) {
  return request('/setup', {
    method: 'POST',
    body: JSON.stringify({ nomes, personagens }),
  });
}

/* ── Board & Properties ────────────────────── */
export function getTabuleiro() {
  return request('/tabuleiro');
}

export function getImoveis() {
  return request('/imoveis');
}

/* ── Game State ────────────────────────────── */
export function getEstado() {
  return request('/estado');
}

/* ── Turn ──────────────────────────────────── */
export function jogar(nome) {
  return request(`/jogar/${encodeURIComponent(nome)}`, { method: 'POST' });
}

/* ── Purchase Decision ─────────────────────── */
export function comprarImovel(nome) {
  return request(`/comprar-imovel/${encodeURIComponent(nome)}`, { method: 'POST' });
}

export function pularCompra(nome) {
  return request(`/pular-compra/${encodeURIComponent(nome)}`, { method: 'POST' });
}

/* ── Bankruptcy ────────────────────────────── */
export function declararFalencia(nome) {
  return request(`/falencia/${encodeURIComponent(nome)}`, { method: 'POST' });
}

/* ── History & Report ──────────────────────── */
export function getHistorico() {
  return request('/historico');
}

export function getRelatorio() {
  return request('/relatorio');
}

/* ── Auction ───────────────────────────────── */
export function iniciarLeilao(imovel) {
  return request('/leilao/iniciar', {
    method: 'POST',
    body: JSON.stringify(imovel),
  });
}

export function fazerLance(nomeJogador, valor) {
  return request('/leilao/lance', {
    method: 'POST',
    body: JSON.stringify({ nomeJogador, valor }),
  });
}

export function encerrarLeilao() {
  return request('/leilao/encerrar', { method: 'POST' });
}
