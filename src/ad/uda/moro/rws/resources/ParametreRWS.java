package ad.uda.moro.rws.resources;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Parametre;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

/**
 * @author rduran &amp; mmiret
 *
 */
@RequestScoped
@Path("parametres")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ParametreRWS {
	
	// RESTful web service Context;
	MoroRWSFactory factory = null;
	boolean readyToProcess = false;
	
	/**
	 * Default constructor. Will be called by the JAX-RS framework.
	 */
	public ParametreRWS() {
		
		// Get Factory:
		factory = MoroRWSFactory.getInstance(); // Get the factory
		readyToProcess = (factory != null) ? true : false; // Set flag according to factory availability
	}
	
	/**
	 * Returns the details of an <code>Parametre</code>
	 * @param idParam El identificador del parametre
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("{idParam}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getParametre(@PathParam("idParam") String idParam) {
		
		System.out.println("getParametre() - idParam: [" + idParam +"]");

		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idParam == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Parametre code not specified").build();
		if (!(Integer.valueOf(idParam)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid parametre code [" + idParam + "]").build();
		
		// Get the Parametre details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Parametre parametre = null;
		try {
			parametre = csr.getParametreById(Integer.valueOf(idParam)); // get the Parametre instance
			if (parametre == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(parametre).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Returns a list of <code>Parametre</code>
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getParametre() {
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		
		// Get the Parametre list and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Parametre[] parametres = null;
		try {
			parametres = csr.getParametreList(); // get the Parametre list
			if (parametres == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(parametres).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Modifies the name of an <code>Parametre</code>, specified in the RESTful request path
	 * @param idParam El identificador del parametre.
	 * @param descripcio The new Parametre name. This must be a valid text and cannot be <b>null</b>
	 * @return  A Standard RESTful response. If OK, it returns the details of the modified Parametre instance.
	 */
	@PUT()
	@Path("{idParam}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@RolesAllowed({"USER","ADMIN"})
	public Response updateDescripcioParametre(@PathParam("idParam") String idParam,
			@FormParam("descripcio") String descripcio,
			@Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("putParametre() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}
		System.out.println("updateDescripcioParametre() - new name: [" + descripcio + "]");
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path and form parameter:
		if (idParam == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idParam not specified").build();
		if (!(Integer.valueOf(idParam)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idParam [" + idParam + "]").build();
		if (descripcio == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del parametre is null").build();
		if (descripcio.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del parametre is empty").build();
		if (descripcio=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del parametre [" + descripcio + "] no es vàlid").build();
		
		System.out.println("updateDescripcioParametre() - descripcio: [" + descripcio + "]");
		
		// Get the Parametre details and modify the name:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Parametre parametre = null;
		try {
			parametre = csr.getParametreById(Integer.valueOf(idParam)); // Get Parametre details
			if (parametre == null) // Not found
				return Response.status(Status.NO_CONTENT).build();

			parametre.setDescripcio(descripcio); // Set new name in Parametre
			csr.updateParametre(parametre); // Persist modification NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok(parametre).build();
	}
	
	/**
	 * Adds a new <code>Parametre</code> instance. The data is received through the RESTful service's <b>Request Body</b> as an XML String.
	 * @param parametre The <code>Parametre</code> instance to add.
	 * @return  A Standard RESTful response. If OK, it returns the text "Succeeded".
	 */
	@POST()
	@Path("")
	@Consumes("application/xml")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response postParametre(Parametre parametre, @Context SecurityContext securityContext) {

		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("postParametre() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check incoming Entity instance:
		if (parametre == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Parametre code not specified").build();
		System.out.println("postParametre() - Parametre details: [" + parametre.toString() + "]");
		if (!parametre.hasValidInformation())
			return Response.status(Status.BAD_REQUEST).header("Message", "Parametre instance contains bad information").build();
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();

		// Add the new Parametre:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		try {
			//FIXME En teoria no necessitariem controlar el id del Parametre
			if (csr.getParametreById(parametre.getId()) != null) // Code of new Parametre already in use
				return Response.status(Status.BAD_REQUEST).header("Message", "Parametre with code [" + parametre.getId() + "] already exists").build();
			csr.addParametre(parametre); // Persist new Parametre NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}

		return Response.ok("Succeeded").build();
	}
	
	/**
	 * Removes an existent <code>Parametre</code> instance. The unique <b>code</b> in the Path parameter will be used to locate
	 * the Parametre to remove.
	 * @param idParam The code of the Parametre instance to remove.
	 * @return A standard <code>Response</code> instance. If its status is OK, it will return the text "Succeeded" in the response body.
	 */
	@DELETE()
	@Path("{idParam}")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response deleteParametre(@PathParam("idParam") String idParam, @Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("deleteParametre() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check environment:
		System.out.println("deleteParametre() - idParam: [" + idParam +"]");
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Krypton service not available").build();
		
		// Check path parameter:
		if (idParam == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idParam not specified").build();
		if (!(Integer.valueOf(idParam)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idParam [" + idParam + "]").build();
		
		// Delete del Parametre (if exists):
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Parametre parametre = null;
		try {
			parametre = csr.getParametreById(Integer.valueOf(idParam)); // Get Parametre details
			if (parametre == null) // Not found
				return Response.status(Status.BAD_REQUEST).header("Message", "Parametre with idParam [" + idParam + "] does not exist").build();
			/* TODO Mirar si fa falta controlar algo.
			 * if (parametre.hasCustomers()) // Parametre has customers. This is not allowed
				return Response.status(Status.BAD_REQUEST).header("Message", "Parametre with code [" + idParam + "] has Customers assigned").build();*/

			csr.deleteParametre(Integer.valueOf(idParam)); // Delete Parametre NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok("Succeeded").build();
	}

}
