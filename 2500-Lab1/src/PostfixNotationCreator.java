/*
 * Author: David Jones 
 * Date: 10/2/17
 * This class creates converts a infix expression to post-fix and evaluates it
 * 
 */
public class PostfixNotationCreator 
{
	public PostfixNotationCreator()
	{
		this.operatorStack = new Stack<Character>();		//Create the stack
		this.postFixExpression = "";						//Initialize the post-fix to an empty string
	}
	
	public void processInfixExpression(String infixExpression) throws InvalidTokenException
	{
		/*
		 * -Processes an infix expression and creates the equivalent post-fix expression
		 * -Caller requirements: This function expects a non-null (may be empty) string that contains an infix expression
		 * -Caller expectations: If the infix string is invalid an InvalidTokenException will be thrown with the reason the expression is invalid contained in the message, otherwise
		 * 		a post-fix expression is created that can be retrieved using getPostfixExpression().
		 * -Operation:
		 *  	The string is processed character by character. All grouped characters that represent digits are appended to the post-fix string immediately. If a character is an operator
		 *  it is processed. When an opening parenthesis '(' is found it is pushed onto the stack. When a closed parenthesis '(' is found the stack is popped until an opening '('
		 *  parenthesis is popped from the stack, if a '(' is not found on the stack then the expression is invalid. 
		 *  	Operators are identified as unary or binary operators which require 1 or 2 operands. All operators require a value after them. Unary operators (C and Q) require an operator
		 *  to proceed them, while binary operator (+,-,*,/,<,>, etc) require a value to proceed them. Parenthesis are treated as 'containers' of values.
		 */
		
		int tokenStartIndex, tokenEndIndex;		//Used to represent the current token start index and end of token index
		boolean needsValue = true;				//If true a value (operand) is needed, if false an operator is needed. To begin the loop a value is needed
		
		//Process each character in the string. Append values(operands) immediately to the post-fix expression. Push '(' on the stack, process operators and ')'
		for(tokenStartIndex = tokenEndIndex = 0; tokenEndIndex < infixExpression.length(); tokenEndIndex ++)
		{
			char currentChar = infixExpression.charAt(tokenEndIndex);		//Get the current character. It may be a token by itself (operator) or part of a token (value)
			if(Character.isDigit(currentChar))	continue;					//IS the character is a digit? If so then loop until a non-digit character is found.
			if (tokenStartIndex != tokenEndIndex)							//Is the token start index is not equal to the token end index? Is so a value (operand) exists, append it to the post fix string
			{
				String numericValue = infixExpression.substring(tokenStartIndex, tokenEndIndex) + " ";	//Get the sequence of chars that represent a value(operand) and append a space to the end of it
				if(!needsValue)												//Is an operator needed? If so throw an exception stating so
					throw new InvalidTokenException("An operator must proceed the value " + numericValue);
				this.postFixExpression += numericValue; 					//Append the value to the post-fix string
				needsValue = false;											//An operator is now needed since a value was just added
			}
			if(this.isOperator(currentChar))								//Is the current character an operator? If so process the operator
			{
				int operandCount = this.processOperator(infixExpression,tokenEndIndex);	//Process the operator and get whether it is binary(2) or unary(1) operator
				if(operandCount == 2)										//Is the operator a binary operator? If so make sure it's okay to accept an operator
				{	
					if(needsValue)											//Is a value(operand) needed? If so then the infix expression is malformed
						throw new InvalidTokenException("The operator ( " + currentChar + " ) needs a value preceeding it");
				}
				else if(!needsValue)										//Since the operator is unary is an operator needed? If so then the infix expression is malformed
					throw new InvalidTokenException("An operator must preceed the unary operator " + currentChar);
				needsValue = true;
			}
			else if(currentChar == '(')										//Is the character a left parenthesis? If so push it onto the stack and indicate a value is needed next
			{
				if(!needsValue)												//Is an operator needed? If so the expression is malformed
					throw new InvalidTokenException("An opening parenthesis is used when a operator is needed");
				this.operatorStack.push('(');									
				needsValue = true;						
			}
			else if(currentChar == ')')										//Is the character a right parenthesis? If so then process the parenthesis
			{
				if(needsValue)												//Is a value needed? If so then the previous operator does not have an operand associated with it
					throw new InvalidTokenException("A value is needed before a closing parenthesis");
				this.processRightParenthesis();									
			}
			else if(currentChar != ' ')										//Is the token not a space? If not a space then the token is invalid
				throw new InvalidTokenException("An unrecognized character (" + currentChar + ") was entered");
			tokenStartIndex = tokenEndIndex + 1;							//Reset the token start index
		}
		//Is the token start index is not equal to the token end index? If so a value(operand) exists,  append it to the post-fix string. This would be in the case that a value is the last thing in the infix expression
		if(tokenStartIndex != tokenEndIndex)									
		{
			if(!needsValue)													//Is an operator needed? If so then a value(operand) was given without an associated operand
				throw new InvalidTokenException("An operator must proceed the value " + infixExpression.substring(tokenStartIndex, tokenEndIndex));
			this.postFixExpression += infixExpression.substring(tokenStartIndex, tokenEndIndex) + " ";
			needsValue = false;												
		}
		//Is a value needed? This would occur if the infix expression was ended with an operator without a value(operand) associated with it. The exception is malformed
		if(needsValue)														
			throw new InvalidTokenException("An operand is needed at the end of the expression");
		
		//Pop any remaining operators off the stack and append to the post-fix expression. If a ( is found then the expression does not contain a corresponding starting parenthesis.
		try
		{
			while(true)
			{
				char operator = this.operatorStack.pop();
				if(operator == '(')
					throw new InvalidTokenException("A opening parenthesis ( was found without a closing parenthesis");
				this.postFixExpression += operator + " ";
			}
		}
		catch(StackUnderflowException e)
		{	//An underflow exception will be thrown when the stack is empty, ignore it
		}
	}
	
	public int evaluate() throws ArithmeticException
	{
		/*
		 * Evaluates the post-fix expression that is contained in this object.
		 * 
		 * Caller Requirements: A call to processInfixExpression must have completed without any exceptions
		 * Caller Expectations: An integer is returned representing the result of the post-fix expression evaluation
		 *  
		 * If an operand is outside the range of an integer or while performing calculations an intermediate value is outside of the range of an integer
		 * 		an Arithmetic Exception is thrown
		 * 
		 * The post-fix expression is evaluated by immediately pushing values onto a stack. When an operator is found one or two values (depending if the operator
		 * 		is unary or binary) are popped from the stack, the calculation is done and the result is pushed back onto the stack. When the post-fix expression has been
		 * 		evaluated the result is on the stack. It is popped and returned.
		 * 
		 */
		
		Stack<Long> valueStack = new Stack<Long>();											//Use a Stack of Longs to store operands and results
		long number = 0, result = 0;														//number is long equivalent of an operand. result is the intermediate result of an operator
		
		//Go through each token in the postfix string. Push operands onto the value stack, and pop operands from the value stack when operators are found
		for(int i = 0; i < this.postFixExpression.length(); i ++)							
		{
			char currentChar = this.postFixExpression.charAt(i);							//Get the current character
			if(Character.isDigit(currentChar))												//Is the character a digit? If so adjust the current 'number' and add the digit to it
			{
				number *= 10;																//Adjust the current 'number'
				number += Character.getNumericValue(currentChar);							//Add the digit to it
				if(number > Integer.MAX_VALUE)												//Make sure the current 'number' is in a valid range
					throw new ArithmeticException("Error: An operand is too large");
				continue;
			}
			//If a space is encountered then 'number' contains and operand. Push number onto the value stack and reset the number
			if(currentChar == ' ')															
			{
				valueStack.push(number);
				number = 0;
				continue;
			}
			
			//At this point an operator has been detected. Get the second operand(top of stack) for binary operators  which would be the first and only operand for unary operators
			long secondOperand = 0;
			try
			{
				secondOperand = valueStack.pop();
			}
			catch(StackUnderflowException e)
			{
			}
			
			//Determine the operator and perform the calculation
			if(currentChar == 'Q')											//Square root. Unary operator so only one operator is used
				result = (long)Math.pow(secondOperand,0.5);
			else if(currentChar == 'C')										//Cube root. Unary operator so only one operator is used
				result = (long)Math.pow(secondOperand, 1.0/3.0);
			else
			{
				//At this point the operator will be a binary operator. Get the first operand off the top of the stack
				long firstOperand = 0;
				try
				{
					firstOperand = valueStack.pop();
				}
				catch(StackUnderflowException e)
				{
				}
				
				//Determine which binary operator and perform the calculation
				switch(currentChar)
				{
				case '-':										//Subtracting is the same thing as adding the negative of the second operand
					secondOperand *= -1;
				case '+':	
					result = firstOperand + secondOperand;
					break;
				case '*':	
					result = firstOperand * secondOperand;
					break;
				case '/':	
					result = firstOperand / secondOperand;
					break;
				case '^':	
					result = (long)Math.pow(firstOperand, secondOperand );
					break;
				case '<': 	
					result = firstOperand  << secondOperand;
					break;
				case '>':	
					result = firstOperand >> secondOperand;
					break;
				case '%':	
					result = firstOperand % secondOperand;
					break;
				}
			}
			//Make sure the intermediate result is within the range of an integer
			if(result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
				throw new ArithmeticException("Error: The expression caused an overflow when performing " + currentChar);
			valueStack.push(result);		//Save the result onto the stack
			i++; 							//Skip the space that follows the operator
		}
		//Pop the result and return it
		try
		{
			result = valueStack.pop();
		}
		catch(StackUnderflowException e)
		{
		}
		return (int)result;
	}
	
	public String getPostfixExpression()
	{
		/*
		 * Returns the converted infix expression as a post fix string
		 * Caller Requirements: A call to processInfixExpression must have completed without any exceptions for this function to return valid data
		 * Caller Expectations: String is returned representing the post-fix expression. Each operand and operator is seperated by a space
		*/
		return this.postFixExpression;
	}
	public void clearExpression()
	{
		// This function resets the class. Should be called when a new infix expression is going to be processed
		this.operatorStack.clear();
		this.postFixExpression = "";
	}
	
	protected boolean isOperator(char what)
	{
		//Determines if the specified character is an operator. Valid operators are (+,-,*,/,^,Q,C,<,>,%)
		switch(what)
		{
		case '+':	return true;
		case '-':	return true;
		case '*':	return true;
		case '/':	return true;
		case '^':	return true;
		case 'Q': 	return true;
		case 'C':	return true;
		case '<': 	return true;
		case '>':	return true;
		case '%':	return true;
		default :	return false;
		}
	}
	
	protected int processOperator(String infixExpression, int operatorIndex) throws InvalidTokenException
	{
		/*
		 * Processes an operator in an infix expression. 
		 * 
		 * Caller Requirements: A valid operator must exist at operatorIndex in infixExpression. 
		 * Caller Expectations: The function returns how many operands are required are for the operator. 1 for unary, 2 for binary operators.
		 * 		Upon return all operators of higher precedence are popped from the operator stack until a starting parenthesis '(' is found.
		 * 		An exception is thrown if a unary operator does not contain a '(' following it (ignores whitespace)
		 * 
		 */
		char operatorToken = infixExpression.charAt(operatorIndex);						//Get the operator
		
		//Our unary operators have the highest precedence so do not worry about popping the stack but check to make sure the beginning parenthesis exists
		if(operatorToken == 'Q' || operatorToken == 'C')								//Is the operator unary? If so make sure a beginning parenthesis exists
		{
			int openingParenIndex = infixExpression.indexOf('(', operatorIndex + 1);	//Get the beginning parenthesis index
			if(openingParenIndex == -1)													//Is the beginning parenthesis index invalid? If so the expression is invalid
				throw new InvalidTokenException("The operator ( " + operatorToken + " ) does not contain a starting parenthesis");
			//Do non-whitespace characters exist between the operator and beginning parenthesis? If so the expression is invalid
			if(openingParenIndex != operatorIndex + 1 && !infixExpression.substring(operatorIndex + 1,openingParenIndex).trim().isEmpty())
				throw new InvalidTokenException("The operator ( " + operatorToken + " ) does not contain a starting parenthesis");
		}
		//The operator is a binary operator at this point. Pop all operators off the stack and append to the post fix expression until a '(' is found
		else
			try
			{
				while(this.operatorStack.peek() != '(')
					this.postFixExpression += this.operatorStack.pop() + " ";
			}
			catch(StackUnderflowException e)
			{
				
			}
		//Push the operator onto the stack
		operatorStack.push(operatorToken);
		if(operatorToken == 'Q' || operatorToken == 'C')		//Is the operator a unary operator? If so return 1
			return 1;
		return 2;												//Operator was binary
	}
	
	protected void processRightParenthesis() throws InvalidTokenException
	{
		/*
		 * Processes a right parenthesis. Pops all operators off the stack and appends to the post fix string until a beginning parenthesis '(' is found.
		 * 
		 * Caller requirements: None
		 * Caller expectations: The function will append all operators on the stack to the post-fix string until a '(' is found. If a '(' is not found the 
		 * 		infix expression is invalid and an exception will be thrown.
		 */
		
		//Get the top token and see if it is a '('. If so then don't pop anymore operators from the stack
		char token;									
		try 
		{
			token = this.operatorStack.pop();
		} 
		catch (StackUnderflowException e) 
		{
			throw new InvalidTokenException("A right parenthesis was found without a beginning left parenthesis");
		}
		if(token == '(')									//Is the top operator a beginning parenthesis? If so don't pop anymore items
			return;
		
		//Loop until the stack is empty or a beginning parenthesis is found. Append every popped operator to the string
		while(!this.operatorStack.isEmpty())	
		{	
			postFixExpression += token + " ";
			try
			{
				token = this.operatorStack.pop();
			}
			catch(StackUnderflowException e)
			{//This part of the code should never be reached because we made sure the stack was not empty
			}
			if(token == '(')								//If the operator is a beginning parenthesis stop looping and the expression is valid		
				return;
		}
		
		//At this point the stack id not contain a beginning parenthesis so the expression is invalid
		throw new InvalidTokenException("A right parenthesis was found without a beginning left parenthesis");
	}
	
	protected Stack<Character> operatorStack;		//A stack of operators used for converting an infix expression to a post-fix expression
	protected String postFixExpression;				//The string of the post-fix expression
}
