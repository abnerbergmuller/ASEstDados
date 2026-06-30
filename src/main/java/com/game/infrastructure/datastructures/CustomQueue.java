package com.game.infrastructure.datastructures;

import java.util.NoSuchElementException;

/**
 * Implementação customizada de uma Fila (FIFO).
 * Suporta limite de capacidade opcional (para histórico).
 * @param <T> O tipo de dado armazenado.
 */
public class CustomQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;
    private int capacity;

    public CustomQueue() {
        this(-1); // Sem limite
    }

    public CustomQueue(int capacity) {
        this.front = null;
        this.rear = null;
        this.size = 0;
        this.capacity = capacity;
    }

    /**
     * Adiciona um elemento ao final da fila.
     * Se a capacidade for excedida, o elemento mais antigo (início) é removido.
     * @param data Dado a ser inserido.
     */
    public void enqueue(T data) {
        if (capacity != -1 && size >= capacity) {
            dequeue(); // Remove o mais antigo
        }

        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    /**
     * Remove e retorna o elemento do início da fila.
     * @return O dado removido.
     * @throws NoSuchElementException se a fila estiver vazia.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("A fila está vazia.");
        }
        T data = front.getData();
        front = front.getNext();
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Retorna o elemento do início sem removê-lo.
     * @return O dado no início.
     * @throws NoSuchElementException se a fila estiver vazia.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("A fila está vazia.");
        }
        return front.getData();
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        // Ajusta o tamanho se a nova capacidade for menor que o tamanho atual
        while (capacity != -1 && size > capacity) {
            dequeue();
        }
    }

    /**
     * Retorna todos os elementos da fila em uma lista, sem removê-los.
     */
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        Node<T> current = front;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }
}
