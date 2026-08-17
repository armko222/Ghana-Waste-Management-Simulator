package com.dcit308.wasteops.structures;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Array-backed stack (no java.util.Stack). Powers the undo/audit log --
 * see AuditEvent and AuditEventRepository.
 *
 * Owned by Issue #3.
 */
public class ArrayStack<T> {
    private Object[] elements;
    private int top;
    private  int capacity;

    public ArrayStack(int capacity) {
        if(capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.top = -1;
    }

    public void push(T data){
    if(isFull()){
        throw new IllegalStateException("Stack overflow");
    }
    elements[++top] = data;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
       if(isEmpty()){
           throw new IllegalStateException("Stack is underflow");
       }
       T item = (T) elements[top];
       elements[top--] = null;
       return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if(isEmpty()){
            throw new IllegalStateException("Stack is empty");
        }
        return (T) elements[top];
    }
    @SuppressWarnings("unchecked")
    public List<T> getRecentActions() {
        List<T> list = new ArrayList<>();
        for (int i = top; i >= 0; i--) {
            list.add((T) elements[i]);
        }
        return list;
    }

    public boolean isEmpty() {
       return top == -1;
    }

    public boolean isFull() {
        return top == capacity-1;
    }

    public int getSize() {
    return top+1;
    }

    public int getCapacity() {
        return capacity;
    }
}
