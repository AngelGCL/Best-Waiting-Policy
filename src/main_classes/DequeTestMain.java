package main_classes;

import useful_classes.Deque;

/**
 * Main class made for testing the Double Ended Queue Implementation
 * @author Angel G. Carrillo Laguna
 *
 */
public class DequeTestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Deque<Integer> deque = new Deque<Integer>();
		for(int i=0; i<21; i++) {
			deque.enqueue(i);
			System.out.println(deque);
		}
		
		System.out.println("This is the last node being removed: " + deque.removeLast());
		System.out.println("This is the new last node: " + deque.last());
	}

}
