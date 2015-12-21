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
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

/**
 * @author rduran &amp; mmiret
 *
 */
@RequestScoped
@Path("serveis")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ServeiRWS {
	
	// RESTful web service Context;
	MoroRWSFactory factory = null;
	boolean readyToProcess = false;
	
	/**
	 * Default constructor. Will be called by the JAX-RS framework.
	 */
	public ServeiRWS() {
		
		// Get Factory:
		factory = MoroRWSFactory.getInstance(); // Get the factory
		readyToProcess = (factory != null) ? true : false; // Set flag according to factory availability
	}
	
	/**
	 * Returns the details of an <code>Servei</code>
	 * @param idServei El identificador del servei
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("{idServei}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getServei(@PathParam("idServei") String idServei) {
		
		System.out.println("getServei() - idServei: [" + idServei +"]");

		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idServei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Servei code not specified").build();
		if (!(Integer.valueOf(idServei)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid servei code [" + idServei + "]").build();
		
		// Get the Servei details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Servei servei = null;
		try {
			servei = csr.getServeiById(Integer.valueOf(idServei)); // get the Servei instance
			if (servei == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(servei).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Returns a list of <code>Servei</code>
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getServei() {
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		
		// Get the Servei list and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Servei[] serveis = null;
		try {
			serveis = csr.getServeiList(); // get the Servei list
			if (serveis == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(serveis).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Modifies the name of an <code>Servei</code>, specified in the RESTful request path
	 * @param idServei El identificador del servei.
	 * @param descripcio The new Servei name. This must be a valid text and cannot be <b>null</b>
	 * @return  A Standard RESTful response. If OK, it returns the details of the modified Servei instance.
	 */
	@PUT()
	@Path("{idServei}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes(MediaType.APPLICATION_XML)
//	@RolesAllowed({"USER","ADMIN"})
	@PermitAll
	public Response updateDescripcioServei(@PathParam("idServei") String idServei,
			Servei servei,
			@Context SecurityContext securityContext) {
		int idTipus = servei.getIdTipus();
		String descripcio = servei.getDescripcio();
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("putServei() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}
		System.out.println("updateDescripcioServei() - ID: [" + idServei + "]");
		System.out.println("updateDescripcioServei() - new descripcio: [" + descripcio + "]");
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path and form parameter:
		if (idServei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei not specified").build();
		if (!(Integer.valueOf(idServei)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idServei [" + idServei + "]").build();
		if (descripcio == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del servei is null").build();
		if (descripcio.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del servei is empty").build();
		if (descripcio=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del servei [" + descripcio + "] no es vàlid").build();
		
		System.out.println("updateDescripcioServei() - new idTipus: [" + idTipus + "]");
		
		// Get the Servei details and modify the name:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Servei newServei = null;
		try {
			newServei = csr.getServeiById(Integer.valueOf(idServei)); // Get Servei details
			if (newServei == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			
			newServei.setIdTipus(idTipus);// Set new idTipus in Servei
			newServei.setDescripcio(descripcio); // Set new descripcio in Servei
			csr.updateServei(newServei); // Persist modification NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok(newServei).build();
	}
	
	/**
	 * Adds a new <code>Servei</code> instance. The data is received through the RESTful service's <b>Request Body</b> as an XML String.
	 * @param servei The <code>Servei</code> instance to add.
	 * @return  A Standard RESTful response. If OK, it returns the text "Succeeded".
	 */
	@POST()
	@Path("{idServei}")
	@Produces("text/plain")
	@Consumes(MediaType.APPLICATION_XML)
	@PermitAll
	public Response postServei(@PathParam("idServei") String idServei, Servei servei, @Context SecurityContext securityContext) {

		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("postServei() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check incoming Entity instance:
		if (servei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Servei code not specified").build();
		System.out.println("postServei() - Servei details: [" + servei.toString() + "]");
		if (!servei.hasValidInformation())
			return Response.status(Status.BAD_REQUEST).header("Message", "Servei instance contains bad information").build();
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();

		// Add the new Servei:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		try {
			//FIXME En teoria no necessitariem controlar el id del Servei
			if (csr.getServeiById(Integer.valueOf(idServei)) != null) // Code of new Servei already in use
				return Response.status(Status.BAD_REQUEST).header("Message", "Servei with code [" + servei.getId() + "] already exists").build();
			csr.addServei(servei); // Persist new Servei NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}

		return Response.ok("Succeeded").build();
	}
	
	/**
	 * Removes an existent <code>Servei</code> instance. The unique <b>code</b> in the Path parameter will be used to locate
	 * the Servei to remove.
	 * @param idServei The code of the Servei instance to remove.
	 * @return A standard <code>Response</code> instance. If its status is OK, it will return the text "Succeeded" in the response body.
	 */
	@DELETE()
	@Path("{idServei}")
	@Produces("text/plain")
	@PermitAll
	public Response deleteServei(@PathParam("idServei") String idServei, @Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("deleteServei() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check environment:
		System.out.println("deleteServei() - idServei: [" + idServei +"]");
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Krypton service not available").build();
		
		// Check path parameter:
		if (idServei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei not specified").build();
		if (!(Integer.valueOf(idServei)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idServei [" + idServei + "]").build();
		
		// Delete del Servei (if exists):
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Servei servei = null;
		try {
			servei = csr.getServeiById(Integer.valueOf(idServei)); // Get Servei details
			if (servei == null) // Not found
				return Response.status(Status.BAD_REQUEST).header("Message", "Servei with idServei [" + idServei + "] does not exist").build();
			/* TODO Mirar si fa falta controlar algo.
			 * if (servei.hasCustomers()) // Servei has customers. This is not allowed
				return Response.status(Status.BAD_REQUEST).header("Message", "Servei with code [" + idServei + "] has Customers assigned").build();*/

			csr.deleteServei(Integer.valueOf(idServei)); // Delete Servei NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok("Succeeded").build();
	}

}
