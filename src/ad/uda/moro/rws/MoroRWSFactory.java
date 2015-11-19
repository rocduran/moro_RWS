/**
 * 
 */
package ad.uda.moro.rws;

import java.io.Serializable;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.naming.Context;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import ad.uda.moro.rws.MoroRWSFactory;
import ad.uda.moro.CommonUtilities;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

/**
 * @author rduran &amp; mmiret
 *
 */
public class MoroRWSFactory implements ServletContextListener, Serializable {
	
	private static final long serialVersionUID = 1L;
    private static MoroRWSFactory INSTANCE = null; // This object will contain the unique instance of this factory
	private static final String LOGPREFIX = "MoroRWSFactory -- ";
    
    // Remoting context:
    private Context initialContext;
    private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
    private EnquestesServiceRemote enquestesService; // Reference to EnquestesServiceRemote
    
    
    /**
	 * Default constructor. Will be called by the web application startup process, so it must be <b>public</b>.
	 */
	public MoroRWSFactory() {
		System.out.println(LOGPREFIX + "Instanciat");
	}
	
	/**
	 * Returns the instance of this class, created by the web application environment
	 * @return The instance. If the web application context is not yet initialized, this method will return <b>null</b>.
	 */
	public static MoroRWSFactory getInstance() {
		return INSTANCE;
	}
	
	public EnquestesServiceRemote getEnquestesService() {
		return this.enquestesService;
	}
	
	/**
	 * <code>ServletContextListener</code> interface implementation.<br/>
	 * This method is called by WildFly's Servlet engine when this RESTful web service is destroyed and no longer available to consumers.
	 * No real actions will be performed at this moment. Just a log entry about the event will be visible.
	 * @param servletContext The servlet context instance
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.out.println(LOGPREFIX + "Servet context DESTROYED");
	}


	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		// Prepare the initial context for remote EJB access at WildFly server:
        Properties properties = new Properties();
        properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
        try {
        	initialContext = new InitialContext(properties);
		} catch (NamingException ex) {
			System.out.println("Failed to obtain the EJB references. Details: " + ex.getMessage());
			return;
		}
		System.out.println(LOGPREFIX + "Initial context created");
		
		// Get the reference to the EJBs:
		try {
			
			System.out.println(LOGPREFIX + "Look up EnquestesServiceRemote EJB...");
			enquestesService = (EnquestesServiceRemote)initialContext.lookup(CommonUtilities.getLookupEJBName("EnquestesServiceBean", EnquestesServiceRemote.class.getName()));
			System.out.println(LOGPREFIX + "EnquestesServiceRemote EJB TROBAT");

		} catch (NamingException ex) {
			System.out.println(LOGPREFIX + "ERROR: " + ex.getMessage());
			return;
		}
		
		// Store the reference to this instance to make it available via method getInstance():
		INSTANCE = this; // Set the instance to this one.
		System.out.println(LOGPREFIX + "RESTful web service INITIALIZED");

	}

}
