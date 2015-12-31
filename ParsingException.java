package cs360;

/**
 * A ParsingException is thrown whenever the parser detects an error in
 * syntax, like mismatched parentheses.
 */
public class ParsingException extends RuntimeException {
	
	public ParsingException() { }
	
	public ParsingException(String message) { super(message); }
}
