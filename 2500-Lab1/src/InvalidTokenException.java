/*
 * Author: David Jones
 * Date: 10/2/17
 * 
 * This class is an exception thrown when an infix expression is invalid. It gives a detailed reason why. Created as a class so all InvalidTokenException 
 * contain "The algebraic (infix) expression is invalid: " before the detailed reason
 */
public class InvalidTokenException extends Exception
{
	public InvalidTokenException(String reasonWhy)
	{
		super("The algebraic (infix) expression is invalid: " + reasonWhy);
	}
}
