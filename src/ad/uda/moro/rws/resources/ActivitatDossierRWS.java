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
import ad.uda.moro.ejb.entity.ActivitatDossier;
import ad.uda.moro.ejb.entity.Dossier;
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

/**
 * @author rduran &amp; mmiret
 *
 */
@RequestScoped
@Path("activitatDossiers")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ActivitatDossierRWS {
	
	// RESTful web service Context;
	MoroRWSFactory factory = null;
	boolean readyToProcess = false;
	
	/**
	 * Default constructor. Will be called by the JAX-RS framework.
	 */
	public ActivitatDossierRWS() {
		
		// Get Factory:
		factory = MoroRWSFactory.getInstance(); // Get the factory
		readyToProcess = (factory != null) ? true : false; // Set flag according to factory availability
	}
	
	/**
	 * Returns the details of an <code>ActivitatDossier</code>
	 * @param idAd El identificador del activitatDossier
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("{idAd}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getActivitatDossier(@PathParam("idAd") String idAd) {
		
		System.out.println("getActivitatDossier() - idAd: [" + idAd +"]");

		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idAd == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier code not specified").build();
		if (!(Integer.valueOf(idAd)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid activitatDossier code [" + idAd + "]").build();
		
		// Get the ActivitatDossier details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		ActivitatDossier activitatDossier = null;
		try {
			activitatDossier = csr.getActivitatDossierById(Integer.valueOf(idAd)); // get the ActivitatDossier instance
			if (activitatDossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(activitatDossier).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Returns a list of <code>ActivitatDossier</code>
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getActivitatDossier() {
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		
		// Get the ActivitatDossier list and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		ActivitatDossier[] activitatDossiers = null;
		try {
			activitatDossiers = csr.getActivitatDossierList(); // get the ActivitatDossier list
			if (activitatDossiers == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(activitatDossiers).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Modifies the name of an <code>ActivitatDossier</code>, specified in the RESTful request path
	 * @param idAd El identificador del activitatDossier.
	 * @param descripcio The new ActivitatDossier name. This must be a valid text and cannot be <b>null</b>
	 * @return  A Standard RESTful response. If OK, it returns the details of the modified ActivitatDossier instance.
	 */
	@PUT()
	@Path("{idAd}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@RolesAllowed({"USER","ADMIN"})
	public Response updateDescripcioActivitatDossier(@PathParam("idAd") String idAd,
			@FormParam("idDossier") String idDossier,
			@FormParam("idServei") String idServei,
			@Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("putActivitatDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}
		System.out.println("updateDescripcioActivitatDossier() - new idDossier: [" + idDossier + "] - new idServei: [" + idServei + "]");
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path and form parameter:
		if (idAd == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idAd not specified").build();
		if (!(Integer.valueOf(idAd)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idAd [" + idAd + "]").build();
		if (idDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier del activitatDossier is null").build();
		if (idDossier.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier del activitatDossier is empty").build();
		if (idDossier=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier del activitatDossier [" + idDossier + "] no es vàlid").build();
		if (idServei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei del activitatDossier is null").build();
		if (idServei.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei del activitatDossier is empty").build();
		if (idServei=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei del activitatDossier [" + idServei + "] no es vàlid").build();
		
		System.out.println("updateDescripcioActivitatDossier() - idDossier: [" + idDossier + "] - idServei: [" + idServei + "]");
		
		// Get the ActivitatDossier details and modify the name:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		ActivitatDossier activitatDossier = null;
		try {
			activitatDossier = csr.getActivitatDossierById(Integer.valueOf(idAd)); // Get ActivitatDossier details
			if (activitatDossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			Dossier dossier = csr.getDossierById(Integer.valueOf(idDossier)); // get the Dossier instance
			if (dossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			activitatDossier.setIdDossier(dossier); // Set new dossier in ActivitatDossier
			Servei servei = csr.getServeiById(Integer.valueOf(idServei)); // get the Servei instance
			if (servei == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			activitatDossier.setIdServei(servei); // Set new servei in ActivitatDossier
			csr.updateActivitatDossier(activitatDossier); // Persist modification NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok(activitatDossier).build();
	}
	
	/**
	 * Adds a new <code>ActivitatDossier</code> instance. The data is received through the RESTful service's <b>Request Body</b> as an XML String.
	 * @param activitatDossier The <code>ActivitatDossier</code> instance to add.
	 * @return  A Standard RESTful response. If OK, it returns the text "Succeeded".
	 */
	@POST()
	@Path("")
	@Consumes("application/xml")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response postActivitatDossier(ActivitatDossier activitatDossier, @Context SecurityContext securityContext) {

		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("postActivitatDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check incoming Entity instance:
		if (activitatDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier code not specified").build();
		System.out.println("postActivitatDossier() - ActivitatDossier details: [" + activitatDossier.toString() + "]");
		if (!activitatDossier.hasValidInformation())
			return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier instance contains bad information").build();
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();

		// Add the new ActivitatDossier:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		try {
			//FIXME En teoria no necessitariem controlar el id del ActivitatDossier
			if (csr.getActivitatDossierById(activitatDossier.getId()) != null) // Code of new ActivitatDossier already in use
				return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier with code [" + activitatDossier.getId() + "] already exists").build();
			csr.addActivitatDossier(activitatDossier); // Persist new ActivitatDossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}

		return Response.ok("Succeeded").build();
	}
	
	/**
	 * Removes an existent <code>ActivitatDossier</code> instance. The unique <b>code</b> in the Path parameter will be used to locate
	 * the ActivitatDossier to remove.
	 * @param idAd The code of the ActivitatDossier instance to remove.
	 * @return A standard <code>Response</code> instance. If its status is OK, it will return the text "Succeeded" in the response body.
	 */
	@DELETE()
	@Path("{idAd}")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response deleteActivitatDossier(@PathParam("idAd") String idAd, @Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("deleteActivitatDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check environment:
		System.out.println("deleteActivitatDossier() - idAd: [" + idAd +"]");
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Krypton service not available").build();
		
		// Check path parameter:
		if (idAd == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idAd not specified").build();
		if (!(Integer.valueOf(idAd)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idAd [" + idAd + "]").build();
		
		// Delete del ActivitatDossier (if exists):
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		ActivitatDossier activitatDossier = null;
		try {
			activitatDossier = csr.getActivitatDossierById(Integer.valueOf(idAd)); // Get ActivitatDossier details
			if (activitatDossier == null) // Not found
				return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier with idAd [" + idAd + "] does not exist").build();
			/* TODO Mirar si fa falta controlar algo.
			 * if (activitatDossier.hasCustomers()) // ActivitatDossier has customers. This is not allowed
				return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier with code [" + idAd + "] has Customers assigned").build();*/

			csr.deleteActivitatDossier(Integer.valueOf(idAd)); // Delete ActivitatDossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok("Succeeded").build();
	}

}
