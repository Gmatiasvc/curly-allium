package common;

public class CircularList<T> {
	private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public CircularList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T val) {
        ListNode<T> newNode = new ListNode<>(val);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.setNext(head); // Point to itself to form the cycle
        } else {
            tail.setNext(newNode);
            tail = newNode;
            tail.setNext(head); 
        }
        size++;
    }

    public boolean remove(T val) {
        if (head == null) {
            return false;
        }

        ListNode<T> current = head;
        ListNode<T> previous = tail;

        for (int i = 0; i < size; i++) {
            if (current.getVal().equals(val)) {
                
                if (head == tail) {
                    head = null;
                    tail = null;
                } 
                else if (current == head) {
                    head = head.getNext();
                    tail.setNext(head); 
                } 
                else if (current == tail) {
                    tail = previous;
                    tail.setNext(head);
                } 

                else {
                    previous.setNext(current.getNext());
                }

                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }

        return false;
    }

    public boolean contains(T val) {
        if (head == null) return false;

        ListNode<T> current = head;
        do {
            if (current.getVal().equals(val)) {
                return true;
            }
            current = current.getNext();
        } while (current != head);

        return false;
    }
}
