package exception;

/** Occurs when a bad file name or a bad directory is supplied via settings.txt. */
public class BadFileNameException extends Exception {
  /** Serial number for this Exception. */
  private static final long serialVersionUID = 5970444272365657584L;

  /** An empty constructor for BadFileNameException. */
  public BadFileNameException() {}

  /** An constructor that can recieve a message for BadFileNameException. 
   * 
   * @param message
   *    The message to read when the error occurs.
   */
  public BadFileNameException(String message)
  {
     super(message);
  }
}
