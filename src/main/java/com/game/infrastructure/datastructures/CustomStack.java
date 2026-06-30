package com.game.infrastructure.datastructures;

import java.util.EmptyStackException;

/**
 * Implementação customizada de uma Pilha (LIFO).
 * @param <T> O tipo de dado armazenado.
 */
public class CustomStack<T> {
    private Node<T> top;
    private int size;

    public CustomStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Insere um elemento no topo da pilha.
     * @param data Dado a ser inserido.
     */
    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    /**
     * Remove e retorna o elemento do topo da pilha.
     * @return O dado removido.
     * @throws EmptyStackException se a pilha estiver vazia.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.getData();
        top = top.getNext();
        size--;
        return data;
    }

    /**
     * Retorna o elemento do topo sem removê-lo.
     * @return O dado no topo.
     * @throws EmptyStackException se a pilha estiver vazia.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.getData();
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Limpa a pilha.
     */
    public void clear() {
        top = null;
        size = 0;
    }
}
