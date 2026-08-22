package com.dcit308.wa;

/**
 * Set Implementation using HashTable
 * No Java Collections used
 */
public class SetImpl<T> implements SetADT<T> {
    
    private HashTableChaining<T, Boolean> table;
    
    public SetImpl() {
        this.table = new HashTableChaining<>();
    }
    
    public SetImpl(int initialCapacity) {
        this.table = new HashTableChaining<>(initialCapacity);
    }
    
    @Override
    public boolean add(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Null elements not allowed");
        }
        if (contains(element)) {
            return false;
        }
        table.put(element, true);
        return true;
    }
    
    @Override
    public boolean remove(T element) {
        if (element == null) {
            return false;
        }
        if (!contains(element)) {
            return false;
        }
        table.remove(element);
        return true;
    }
    
    @Override
    public boolean contains(T element) {
        if (element == null) {
            return false;
        }
        return table.containsKey(element);
    }
    
    @Override
    public int size() {
        return table.size();
    }
    
    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }
    
    @Override
    public void clear() {
        table.clear();
    }
    
    @Override
    public Object[] toArray() {
        return table.keys();
    }
    
    @Override
    public SetADT<T> union(SetADT<T> other) {
        SetImpl<T> result = new SetImpl<>();
        
        Object[] thisElements = this.toArray();
        for (Object obj : thisElements) {
            @SuppressWarnings("unchecked")
            T element = (T) obj;
            result.add(element);
        }
        
        Object[] otherElements = other.toArray();
        for (Object obj : otherElements) {
            @SuppressWarnings("unchecked")
            T element = (T) obj;
            result.add(element);
        }
        
        return result;
    }
    
    @Override
    public SetADT<T> intersection(SetADT<T> other) {
        SetImpl<T> result = new SetImpl<>();
        
        Object[] thisElements = this.toArray();
        for (Object obj : thisElements) {
            @SuppressWarnings("unchecked")
            T element = (T) obj;
            if (other.contains(element)) {
                result.add(element);
            }
        }
        
        return result;
    }
    
    @Override
    public SetADT<T> difference(SetADT<T> other) {
        SetImpl<T> result = new SetImpl<>();
        
        Object[] thisElements = this.toArray();
        for (Object obj : thisElements) {
            @SuppressWarnings("unchecked")
            T element = (T) obj;
            if (!other.contains(element)) {
                result.add(element);
            }
        }
        
        return result;
    }
    
    @Override
    public boolean isSubset(SetADT<T> other) {
        Object[] thisElements = this.toArray();
        for (Object obj : thisElements) {
            @SuppressWarnings("unchecked")
            T element = (T) obj;
            if (!other.contains(element)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Object[] elements = toArray();
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]);
            if (i < elements.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}