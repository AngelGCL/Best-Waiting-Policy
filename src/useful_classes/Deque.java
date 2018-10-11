package useful_classes;


/**
 * Double Ended Queue Class made to facilitate the access of the last element in a Queue.
 * @author angel.carrillo1
 *
 * @param <E>
 */
public class Deque<E> implements Queue<E> {


	protected static class DNode<T>{
		private T element; 
		private DNode<T> next, prev; 
		public DNode() { 
			element = null; 
			next = setPrev(null); 
		}
		public DNode(T data, DNode<T> next, DNode<T> prev) { 
			this.element = data; 
			this.next = next;
			this.setPrev(prev);
		}
		public DNode(T data)  { 
			this.element = data; 
			next = setPrev(null); 
		}
		public T getElement() {
			return element;
		}
		public void setElement(T data) {
			this.element = data;
		}
		public DNode<T> getNext() {
			return next;
		}
		public void setNext(DNode<T> next) {
			this.next = next;
		}
		public void clean() { 
			element = null; 
			next = prev = null; 
		}
		public DNode<T> getPrev() {
			return prev;
		}
		public DNode<T> setPrev(DNode<T> prev) {
			this.prev = prev;
			return prev;
		}
	}
	
	private DNode<E> first, last;   // references to first and last node
	private int size; 
	
	public Deque() {           // initializes instance as empty queue
		first = last = null; 
		size = 0; 
	}
	public int size() {
		return size;
	}
	public boolean isEmpty() {
		return size == 0;
	}
	public E first() {
		if (isEmpty()) 
			return null;
		
		return first.getElement(); 
	}
	public E dequeue() {
		E etr = first();
		if (isEmpty()) 
			return null;
		
		else {
			DNode<E> next = first.getNext();
//			next.setPrev(null);
			first.clean();
			first = next;
		}
		size--;
		return etr;
	}
	
	public void enqueue(E e) {
		if (size == 0) { 
			first = last = new DNode<>(e);
		}
		else { 
			DNode<E> node = last;
			last = new DNode<>(e);
			node.setNext(last);
			last.setPrev(node);
		}
		size++; 
	}
	
	/**
	 * Used to look at the last element in the Queue
	 * @return the last element, null if empty.
	 */
	public E last() {
		if(isEmpty())
			return null;
		
		return last.getElement();
	}
	
	/**
	 * Same as last() but removes the element as well.
	 * @return last element in the Queue
	 */
	public E removeLast() {
		E etr = last();
		if(isEmpty())
			return null;
		
		else {
			DNode<E> prev = last.getPrev();
			last.clean();
			prev.setNext(null);
			last = prev;
		}
		size--;
		return etr;
	}
	/**
	 * Creates new instance of Queue which is an exact copy of original.
	 * @return new instance copy of original
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public Deque<E> copy() throws InstantiationException, IllegalAccessException{
		Deque<E> copy = this.getClass().newInstance();
		Deque<E> temp = this.getClass().newInstance();
		while(!this.isEmpty())
			temp.enqueue(this.dequeue());
		while(!temp.isEmpty()) {
			E e = temp.dequeue();
			this.enqueue(e);
			copy.enqueue(e);
		}	
		return copy;
	}
	//////////////////////////////////////FOR TESTING PURPOSES////////////////////////////////////////////////////////////////
	public String toString() {
		if(isEmpty())
			return null;
		
		String s = "[";
		DNode<E> node = first;
		while(node.getNext() != null) {
			s = s + node.getElement() + ", ";
			node = node.getNext();
		}
		s = s + last.getElement() + "]";
		return s;
	}
}
