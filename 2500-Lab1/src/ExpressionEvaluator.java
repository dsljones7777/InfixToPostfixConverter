/*
 * Author: David Jones 
 * Date : 10/2/17
 * This project prompts a user for an infix expression and displays the corresponding post-fix expression and evaluates it.  
 * Project was made as a school project to practice using stacks and to demonstrate our knowledge of how to use a stack to solve
 * a problem.
 */
import java.util.Scanner;
public class ExpressionEvaluator 
{
	public static void main(String[] args) 
	{
		
		Scanner inputScanner = new Scanner(System.in);								//Keyboard input scanner
		PostfixNotationCreator postfixExpression = new PostfixNotationCreator();	//Object that converts infix expression to post-fix and evaluates them
		String inputExpression;														//String input by user of the post fix expression
		
		System.out.println("Please enter an algebraic (infix) expression:");		//Prompt the user once
		while(!(inputExpression = inputScanner.nextLine()).isEmpty())				//Get input from the user. If the user enters a blank line then exit the loop
		{
			postfixExpression.clearExpression();									//Clear the old expression
			try
			{
				postfixExpression.processInfixExpression(inputExpression);			//Convert the infix expression the user has given
				System.out.println(postfixExpression.getPostfixExpression());		//Get the post-fix equivalent
				System.out.println(postfixExpression.evaluate() + "\n");			//Display the result
			}
			catch(Exception e)														//If an error occurs then print the error message and a new line
			{
				System.out.println(e.getMessage() + "\n");
			}
		}
		inputScanner.close();		//Cleanup
		System.exit(0); 			//Added to avoid AGENT_ERROR_NO_JNI_ENV(183) exception
	}

}
