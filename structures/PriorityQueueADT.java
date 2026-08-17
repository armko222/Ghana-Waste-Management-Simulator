package structures;

public interface PriorityQueueADT<T extends Comparable<T>> {
    void insert(T item);
    T extractMin();
    T peekMin();
    boolean isEmpty();
}