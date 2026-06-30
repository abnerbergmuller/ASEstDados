package com.game.domain.exception;

/**
 * Exceção lançada quando um jogador tenta realizar um pagamento sem saldo suficiente.
 */
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
