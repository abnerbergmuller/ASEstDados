package com.game.infrastructure.datastructures;

/**
 * Representa um nó simples para estruturas de dados lineares (Pilha, Fila).
 * @param <T> O tipo de dado armazenado no nó.
 */
public class Node<T> {
    private T data;
    private Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
