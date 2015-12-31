//Keely Hicks
//COMP 360

package cs360.types;

import cs360.*;

import java.sql.Types;
import java.util.*;

/**
 * A RacketList represents a (linked) list in Racket. Note that our version of
 * Racket does not support pairs that are not lists. So (cons 1 2) is an error.
 */
public class RacketList extends RacketExpression {

	private final RacketExpression car;
	private final RacketList cdr;
	public static final RacketList EMPTY_LIST = new RacketList();

	/**
	 * Create a new empty list. Do not call this when you need an empty list,
	 * instead use RacketList.EMPTY_LIST.
	 */
	private RacketList() {
		this.car = this.cdr = null;
	}

	/**
	 * Create a new list from a CAR and a CDR.
	 */
	public RacketList(RacketExpression car, RacketList cdr) {
		assert car != null;
		assert cdr != null;

		this.car = car;
		this.cdr = cdr;
	}

	/**
	 * Retrieve the CAR of this list.
	 */
	public RacketExpression getCar() {
		return car;
	}

	/**
	 * Retrieve the CDR of this list.
	 */
	public RacketList getCdr() {
		return cdr;
	}

	/**
	 * Evaluate this RacketList *as an expression* rather than as a *literal
	 * list*. This function should handle any expression which takes the form of a
	 * list, which is pretty much everything that's not a boolean, symbol, or
	 * integer. That is, this function handles defines, lambdas, quotes, function
	 * calls, etc.
	 */
	@Override
	public RacketExpression eval(Frame env) {
		printDebuggingString();
		
		if (this.equals(EMPTY_LIST)) {
			throw new InterpreterException("Can't evaluate the empty list: " + this);
		}

		// from here on, we know our expression is a regular racket list
		// that is non-empty.

		if (getCar().equals(new RacketSymbol("quote"))) {
			return evalQuote(env);
		} else if (getCar().equals(new RacketSymbol("define"))) {
			return evalDefine(env);
		} else if (getCar().equals(new RacketSymbol("lambda"))) {
			return evalLambda(env);
		} else if (getCar().equals(new RacketSymbol("if"))) {
			return evalIf(env);
		} else { // must be a function call!
			return evalCall(env);
		}
	}
	
	/**
	 * A function call expression is a list containing any number of
	 * sub-expressions: (e1 e2 ... en)  The first sub-expression, e1, 
	 * when evaluated, must evaluate to a RacketFunction of some kind.  
	 * Assuming it does, evaluate e2 through en, then apply e1 to
	 * the argument values.
	 */
	private RacketExpression evalCall(Frame env) {
		List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);
		RacketExpression funcObj = exprAsList.get(0).eval(env);
		if (!(funcObj instanceof RacketFunction)) {
			throw new InterpreterException("Can't call " + funcObj + " as a function.");
		}
		
		List<RacketExpression> evalArgs = new ArrayList<RacketExpression>(); //store the arguments
		
		for (int i=1; i<exprAsList.size(); i++){
			
			evalArgs.add(exprAsList.get(i).eval(env)); //add each arg to list
		}
		RacketFunction rf = (RacketFunction) funcObj; //cast to a func
		return rf.apply(evalArgs); //apply func on the args
		
	}

	/**
	 * A lambda expression is a list of three items: the symbol LAMBDA, followed
	 * by a RacketList of RacketSymbols (the names of the arguments), followed by
	 * a single expression that constitutes the body of the lambda.
	 */
	private RacketExpression evalLambda(Frame env) {
		List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);
		if (exprAsList.size() <= 2) {
			throw new InterpreterException("Wrong number of parts to lambda: " + exprAsList);
		}
		
		List<RacketSymbol> argNames = new ArrayList<RacketSymbol>(); //hold arguments
		RacketList rl = (RacketList) exprAsList.get(1); //get the list of arguments
		while (rl.car!=null){
			argNames.add((RacketSymbol)rl.car); //add each argument to argNames
			rl=rl.cdr; //pass in the next argument
		}
		return new RacketClosure(argNames, exprAsList.get(2), env);
		
		
		
	}

	/**
	 * A quoted expression is a list of two items: the symbol QUOTE, and an
	 * expression. The result of evaluating the quoted expression is the
	 * expression inside.
	 */
	private RacketExpression evalQuote(Frame env) {
		List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

		if (exprAsList.size() != 2) {
			throw new InterpreterException("Wrong number of parts to quote: " + exprAsList);
		}

		return exprAsList.get(1);
	}

	/**
	 * An if expression is a list of four items: the symbol IF, followed by three
	 * sub-expressions, called expr1, expr2, and expr3. To evaluate an if,
	 * evaluate expr1 and if it evaluates to TRUE, then return the evaluated
	 * expr2. Otherwise, return the evaluated expr3.
	 */
	private RacketExpression evalIf(Frame env) {
		List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

		if (exprAsList.size() != 4) {
			throw new InterpreterException("Wrong number of parts in if: " + exprAsList);
		}
		
		// WRITE ME
		if (exprAsList.get(1).eval(env)==RacketBoolean.TRUE){
			return exprAsList.get(2).eval(env); //if true, return the first option
		}
		else {
			return exprAsList.get(3).eval(env); //return else option
		}
		
	}

	/**
	 * A define expression is a list of three items: the symbol DEFINE, a symbol
	 * to be used as a variable, and an expression. Evaluating the define
	 * expression should evaluate the sub-expression and store the result in the
	 * variable.
	 */
	private RacketExpression evalDefine(Frame env) {
		List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

		if (exprAsList.size() <= 2) {
			throw new InterpreterException("Wrong number of parts to define: " + exprAsList);
		}
		
		// WRITE ME
		RacketSymbol rs = new RacketSymbol(exprAsList.get(1).toString());  //var name
		env.defineVariable(rs, exprAsList.get(2).eval(env)); //define the variable
		
		return exprAsList.get(2).eval(env);
	}

	public String toDisplayString() {
		if (this == EMPTY_LIST) {
			return "()";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getCar().toDisplayString());

		RacketList curr = getCdr();
		while (curr != EMPTY_LIST) {
			sb.append(" ");
			sb.append(curr.getCar().toDisplayString());
			curr = curr.getCdr();
		}
		sb.append(")");

		return sb.toString();
	}

	public String toDetailedString() {
		if (this == EMPTY_LIST) {
			return "[List]";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("[List");
		sb.append(getCar().toDisplayString());

		RacketList curr = getCdr();
		while (curr != EMPTY_LIST) {
			sb.append(" ");
			sb.append(curr.getCar().toDisplayString());
			curr = curr.getCdr();
		}
		sb.append("]");

		return sb.toString();
	}

	@Override
	public int hashCode() {
		return car.hashCode() ^ cdr.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RacketList)) {
			return false;
		}

		RacketList other = (RacketList) o;

		if (this == EMPTY_LIST) {
			return other == EMPTY_LIST;
		} else {
			return this.car.equals(other.car) && this.cdr.equals(other.cdr);
		}
	}
}
