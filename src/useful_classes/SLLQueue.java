package useful_classes;

/**
 * @author P. Rivera
 * @edited_by Angel G. Carrillo Laguna
 *
 * @param <E> Element type.
 */
public class SLLQueue<E> implements Queue<E>, Cloneable {
    // inner class for nodes in singly linked lists
	
	protected static class SNode<T>{
		private T element; 
		private SNode<T> next; 
		public SNode() { 
			element = null; 
			next = null; 
		}
		public SNode(T data, SNode<T> next) { 
			this.element = data; 
			this.next = next; 
		}
		public SNode(T data)  { 
			this.element = data; 
			next = null; 
		}
		public T getElement() {
			return element;
		}
		public void setElement(T data) {
			this.element = data;
		}
		public SNode<T> getNext() {
			return next;
		}
		public void setNext(SNode<T> next) {
			this.next = next;
		}
		public void clean() { 
			element = null; 
			next = null; 
		}
	}
	
	private SNode<E> first, last;   // references to first and last node
	private int size; 
	
	public SLLQueue() {           // initializes instance as empty queue
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
			SNode<E> next = first.getNext();
			first.clean();
			first = next;
		}
		size--;
		return etr;
	}
	
	public void enqueue(E e) {
		if (size == 0) { 
			first = last = new SNode<>(e);
		}
		else { 
			SNode<E> node = last;
			last = new SNode<>(e);
			node.setNext(last);
		}
		size++; 
	}
	
	/**
	 * Creates a new instance of the SLLQueue which is a copy of the original.
	 * @return new instance copy of original.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public SLLQueue<E> copy() throws InstantiationException, IllegalAccessException{
		SLLQueue<E> copy = this.getClass().newInstance();
		SLLQueue<E> temp = this.getClass().newInstance();
		while(!this.isEmpty())
			temp.enqueue(this.dequeue());
		while(!temp.isEmpty()) {
			E e = temp.dequeue();
			this.enqueue(e);
			copy.enqueue(e);
		}	
		return copy;
	}
}