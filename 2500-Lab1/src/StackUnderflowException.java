/*
 * Author: David Jones
 * Date: 10/2/17
 * 
 * Created to represent an exception that is thrown when a peek or pop operation is made on a stack that is empty.
 */
public class StackUnderflowException extends Exception
{
	public StackUnderflowException()
	{
		super("An attempt was made to pop an empty stack");
	}
}
