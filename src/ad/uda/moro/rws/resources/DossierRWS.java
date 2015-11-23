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
import ad.uda.moro.ejb.entity.Dossier;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

/**
 * @author rduran &amp; mmiret
 *
 */
@RequestScoped
@Path("dossiers")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class DossierRWS {
	
	// RESTful web service Context;
	MoroRWSFactory factory = null;
	boolean readyToProcess = false;
	
	/**
	 * Default constructor. Will be called by the JAX-RS framework.
	 */
	public DossierRWS() {
		
		// Get Factory:
		factory = MoroRWSFactory.getInstance(); // Get the factory
		readyToProcess = (factory != null) ? true : false; // Set flag according to factory availability
	}
	
	@GET()
	@Produces("text/plain")
	@Path("hellotest")
	public String sayHello(){
		System.out.println("Somebody called sayHello()!");
		return "HelloTest: Hello World!";
	}
	
	@GET()
	@Produces("text/plain")
	@Path("anothertest")
	public String anotherTest(){
		System.out.println("Somebody called anotherTest()!");
		return "HelloTest: Another Test!";
	}
	
	
	/**
	 * Returns the details of an <code>Dossier</code>
	 * @param idDossier El identificador del dossier
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("{idDossier}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getDossier(@PathParam("idDossier") String idDossier) {
		
		System.out.println("getDossier() - idDossier: [" + idDossier +"]");

		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Dossier code not specified").build();
		if (!(Integer.valueOf(idDossier)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid dossier code [" + idDossier + "]").build();
		
		// Get the Dossier details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Dossier dossier = null;
		try {
			dossier = csr.getDossierById(Integer.valueOf(idDossier)); // get the Dossier instance
			if (dossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(dossier).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Returns a list of <code>Dossier</code>
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getDossier() {
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		
		// Get the Dossier details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Dossier[] dossiers = null;
		try {
			dossiers = csr.getDossierList(); // get the Dossier instance
			if (dossiers == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(dossiers).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Modifies the name of an <code>Dossier</code>, specified in the RESTful request path
	 * @param idDossier El identificador del dossier.
	 * @param descripcio The new Dossier name. This must be a valid text and cannot be <b>null</b>
	 * @return  A Standard RESTful response. If OK, it returns the details of the modified Dossier instance.
	 */
	@PUT()
	@Path("{idDossier}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@RolesAllowed({"USER","ADMIN"})
	public Response updateDescripcioDossier(@PathParam("idDossier") String idDossier,
			@FormParam("descripcio") String descripcio,
			@FormParam("preu") String preu,
			@Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("putDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}
		System.out.println("updateDescripcioDossier() - new name: [" + descripcio + "]");
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path and form parameter:
		if (idDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "iDDossier not specified").build();
		if (!(Integer.valueOf(idDossier)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idDossier [" + idDossier + "]").build();
		if (descripcio == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del dossier is null").build();
		if (descripcio.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del dossier is empty").build();
		if (descripcio=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "Descripcio del dossier [" + descripcio + "] no es vàlid").build();
		
		System.out.println("updateDescripcioDossier() - descripcio: [" + descripcio + "]");
		
		// Get the Dossier details and modify the name:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Dossier dossier = null;
		try {
			dossier = csr.getDossierById(Integer.valueOf(idDossier)); // Get Dossier details
			if (dossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();

			dossier.setDescripcio(descripcio); // Set new name in Dossier
			dossier.setPreu(Integer.valueOf(preu));
			csr.updateDossier(dossier); // Persist modification NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok(dossier).build();
	}
	
	/**
	 * Adds a new <code>Dossier</code> instance. The data is received through the RESTful service's <b>Request Body</b> as an XML String.
	 * @param dossier The <code>Dossier</code> instance to add.
	 * @return  A Standard RESTful response. If OK, it returns the text "Succeeded".
	 */
	@POST()
	@Path("")
	@Consumes("application/xml")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response postDossier(Dossier dossier, @Context SecurityContext securityContext) {

		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("postDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check incoming Entity instance:
		if (dossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Dossier code not specified").build();
		System.out.println("postDossier() - Dossier details: [" + dossier.toString() + "]");
		if (!dossier.hasValidInformation())
			return Response.status(Status.BAD_REQUEST).header("Message", "Dossier instance contains bad information").build();
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();

		// Add the new Dossier:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		try {
			//FIXME En teoria no necessitariem controlar el id del Dossier
			if (csr.getDossierById(dossier.getId()) != null) // Code of new Dossier already in use
				return Response.status(Status.BAD_REQUEST).header("Message", "Dossier with code [" + dossier.getId() + "] already exists").build();
			csr.addDossier(dossier); // Persist new Dossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}

		return Response.ok("Succeeded").build();
	}
	
	/**
	 * Removes an existent <code>Dossier</code> instance. The unique <b>code</b> in the Path parameter will be used to locate
	 * the Dossier to remove.
	 * @param idDossier The code of the Dossier instance to remove.
	 * @return A standard <code>Response</code> instance. If its status is OK, it will return the text "Succeeded" in the response body.
	 */
	@DELETE()
	@Path("{idDossier}")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response deleteDossier(@PathParam("idDossier") String idDossier, @Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("deleteDossier() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check environment:
		System.out.println("deleteDossier() - idDossier: [" + idDossier +"]");
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Krypton service not available").build();
		
		// Check path parameter:
		if (idDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier not specified").build();
		if (!(Integer.valueOf(idDossier)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idDossier [" + idDossier + "]").build();
		
		// Delete del Dossier (if exists):
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Dossier dossier = null;
		try {
			dossier = csr.getDossierById(Integer.valueOf(idDossier)); // Get Dossier details
			if (dossier == null) // Not found
				return Response.status(Status.BAD_REQUEST).header("Message", "Dossier with idDossier [" + idDossier + "] does not exist").build();
			/* TODO Mirar si fa falta controlar algo.
			 * if (dossier.hasCustomers()) // Dossier has customers. This is not allowed
				return Response.status(Status.BAD_REQUEST).header("Message", "Dossier with code [" + idDossier + "] has Customers assigned").build();*/

			csr.deleteDossier(Integer.valueOf(idDossier)); // Delete Dossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok("Succeeded").build();
	}

}
