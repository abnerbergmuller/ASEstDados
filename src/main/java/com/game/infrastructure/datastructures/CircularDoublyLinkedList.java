package com.game.infrastructure.datastructures;

/**
 * Implementação de uma Lista Circular Duplamente Ligada.
 * Ideal para representar o tabuleiro do jogo.
 * @param <T> O tipo de dado armazenado.
 */
public class CircularDoublyLinkedList<T> {
    private DoubleNode<T> head;
    private int size;

    public CircularDoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Adiciona um elemento ao final da lista.
     * @param data Dado a ser inserido.
     */
    public void add(T data) {
        DoubleNode<T> newNode = new DoubleNode<>(data);
        if (head == null) {
            head = newNode;
            head.setNext(head);
            head.setPrev(head);
        } else {
            DoubleNode<T> tail = head.getPrev();
            tail.setNext(newNode);
            newNode.setPrev(tail);
            newNode.setNext(head);
            head.setPrev(newNode);
        }
        size++;
    }

    /**
     * Retorna o nó inicial (head).
     * @return O primeiro nó da lista.
     */
    public DoubleNode<T> getHead() {
        return head;
    }

    /**
     * Retorna o tamanho da lista.
     * @return Quantidade de elementos.
     */
    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
