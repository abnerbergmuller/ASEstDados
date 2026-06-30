package com.game.infrastructure.datastructures;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Representa um nó duplo para a Lista Circular Duplamente Ligada (Tabuleiro).
 * @param <T> O tipo de dado armazenado no nó.
 */
public class DoubleNode<T> {
    private T data;
    private DoubleNode<T> next;
    private DoubleNode<T> prev;

    public DoubleNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public DoubleNode<T> getNext() {
        return next;
    }

    public void setNext(DoubleNode<T> next) {
        this.next = next;
    }

    @JsonIgnore
    public DoubleNode<T> getPrev() {
        return prev;
    }

    public void setPrev(DoubleNode<T> prev) {
        this.prev = prev;
    }
}
