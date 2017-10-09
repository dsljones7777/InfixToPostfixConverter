/*
 * Author: David Jones
 * Date: 10/7/17
 * 
 * This generic class is a First in Last out Stack of a generic type. Created as a class to show an understanding of how stacks work. Allows peeking, popping, pushing,
 * clearing and determining if the stack is empty.
 */
public class Stack <T> 
{
	void clear()
	{	
		//Removes all items from the stack by removing the top level node
		this.topNode = null;
	}
	
	void push(T what)
	{
		//Pushes an item onto the top of the stack
		Node newNode = new Node(what,this.topNode);
		this.topNode = newNode;
	}
	T peek() throws StackUnderflowException
	{
		//Returns the top node or throws an exception if the top node is null
		if(this.topNode == null)	throw new StackUnderflowException();
		return this.topNode.getItem();
	}
	T pop() throws StackUnderflowException
	{
		//Pops the top level item from the stack. Throws an exception if the stack is empty
		if(this.topNode == null)	throw new StackUnderflowException();
		T poppedItem = this.topNode.getItem();
		this.topNode = this.topNode.getNext();
		return poppedItem;
	}
	boolean isEmpty()
	{
		//Returns whether the stack has any items
		return (this.topNode == null);
	}
	
	/*
	 * Inner class that represents a stack node
	 */
	protected class Node
	{
		protected Node(T what, Node linkToNode)
		{
			//Assign the node's item and link the node to the node specified in the argument list
			this.item = what;
			this.next = linkToNode;
		}
		protected Node getNext()
		{
			//Returns the next node. The next node is whatever node that is linked from this node
			return next;
		}
		protected void link(Node toWhat)
		{
			//Assigns the next node. Effectively links the toWhat to this node
			this.next = toWhat;
		}
		protected T getItem()
		{
			//Returns the item in the node
			return this.item;
		}
		private T item;					//The object of the node
		private Node next;				//The link to another node
	}

	protected Node topNode;
}
