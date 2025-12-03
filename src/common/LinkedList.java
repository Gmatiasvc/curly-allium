package common;

public class LinkedList<T> {

    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T val) {
        ListNode<T> newNode = new ListNode<>(val);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ListNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getVal();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T val) {
        ListNode<T> current = head;
        while (current != null) {
            if (current.getVal().equals(val)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public boolean remove(T val) {
        if (head == null) {
            return false;
        }
        if (head.getVal().equals(val)) {
            head = head.getNext();
            size--;
            if (head == null) {
                tail = null;
            }
            return true;
        }
        ListNode<T> current = head;
        while (current.getNext() != null) {
            if (current.getNext().getVal().equals(val)) {
                current.setNext(current.getNext().getNext());
                size--;
                if (current.getNext() == null) {
                    tail = current;
                }
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
}
