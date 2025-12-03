package common;

public class ListNode<T> {
	public T val;
	public ListNode<T> next;

	public ListNode(T x) {
		val = x;
		next = null;
	}

	public T getVal() {
		return val;
	}

	public void setVal(T val) {
		this.val = val;
	}

	public ListNode<T> getNext() {
		return next;
	}

	public void setNext(ListNode<T> next) {
		this.next = next;
	}
	
	
}
