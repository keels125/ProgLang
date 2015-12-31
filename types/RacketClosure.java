package cs360.types;

import cs360.*;
import java.util.*;
import java.util.List;

/**
 * A RacketClosure is any kind of RacketFunction defined with a lambda in 
 * the Racket language itself, as opposed to a PrimitiveFunction, which is 
 * coded in Java. 
 */
public class RacketClosure extends RacketFunction {

	private final List<RacketSymbol> argumentNames;
	private final RacketExpression body;
	private final Frame environment;

	/**
	 * Create a closure, given a list of arguments (as symbols), an expression
	 * that is the body, and a Frame that is the environment.
	 */
	public RacketClosure(List<RacketSymbol> args, RacketExpression bod, Frame env) {
		this.argumentNames = args;
		this.body = bod;
		this.environment = env;
	}

	/**
	 * Return the argument names as symbols.
	 */
	public List<RacketSymbol> getArgumentNames() {
		return Collections.unmodifiableList(argumentNames);
	}

	/**
	 * Return the body of this closure.
	 */
	public RacketExpression getBody() {
		return body;
	}

	/**
	 * Return the environment of this closure that the body should be evaluated in.
	 * @return 
	 */
	public Frame getEnvironment() {
		return environment;
	}

	/**
	 * Try to discover the name of this closure.  Does this by searching
	 * the environment and returns 'anonymous' if not found.
	 */
	public String getName() {
		RacketSymbol sym = environment.searchForValue(this);
		if (sym == null) {
			return "anonymous";
		} else {
			return sym.getSymbolName();
		}
	}

	/**
	 * Apply the body of this function to the argument values given.
	 * Return the result.
	 */
	@Override
	public RacketExpression apply(List<RacketExpression> argValues) {
		// verify we have the same number of values as we do argument names
		Utilities.verifyArgumentCount(getName(), argValues, argumentNames.size());

		if (Interpreter.DEBUGGING)
			System.out.println("      Applying: " + this.toDetailedString());

		// WRITE ME
	
		Frame f = new Frame(this.environment); //create a new frame that points to the parent
		for (int i=0; i<argValues.size(); i++){
			
			f.defineVariable(argumentNames.get(i), argValues.get(i)); //define the args with their corresponding values
			
		
		}
		return this.body.eval(f); //return the eval of the body
		
	}

	@Override
	public String toDisplayString() {
		return "#<function:" + getName() + ">";
	}

	@Override
	public String toDetailedString() {
		return "[Func:" + getName() + " " + argumentNames + " " + body + " " + environment + "]";
	}
}
