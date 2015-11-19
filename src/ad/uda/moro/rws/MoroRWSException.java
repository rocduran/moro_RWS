/**
 * 
 */
package ad.uda.moro.rws;

/**
 * This is the main Exception for all KryptonRESTful web service errors.
 * @author rduran &amp; mmiret
 */
public class MoroRWSException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public MoroRWSException() { super(); }
	public MoroRWSException(String message) { super(message); }
}
