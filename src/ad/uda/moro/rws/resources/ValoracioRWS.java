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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Dossier;
import ad.uda.moro.ejb.entity.Parametre;
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.entity.Valoracio;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;
import ad.uda.moro.rws.MoroRWSFactory;

@RequestScoped
@Path("valoracions")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ValoracioRWS {
/*TODO Pa que te hagas una idea:
 * Copiarpegar del (por ejemplo) ActivitatDossierRWS
 * Fas un buscar i canvies tots els ActivitatDossier per Valoracio (activa el case sensitive)
 * Fas un buscar i canvies tots els activitatDossier per valoracio (por eso el case sensitive)
 * Despres busques si hi ha algun error i acabes de corretgir alguna cosa que falti, com funcions.
 * Has d'editar el updateValoracio perque insereixi tots els parametres de valoracio.
 * i ya esta amigos, ya tenemos un bizcocho de chocolate y crema para estas navidades.  
	*/
	// RESTful web service Context;
	MoroRWSFactory factory = null;
	boolean readyToProcess = false;
			
	/**
	* Default constructor. Will be called by the JAX-RS framework.
	*/
	public ValoracioRWS() {	
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
	@Path("{idVal}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getActivitatDossier(@PathParam("idVal") String idVal) {
		
		System.out.println("getValoracioById() - idVal: [" + idVal +"]");

		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idVal == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier code not specified").build();
		if (!(Integer.valueOf(idVal)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid activitatDossier code [" + idVal + "]").build();
		
		// Get the ActivitatDossier details and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Valoracio valoracio = null;
		try {
			valoracio = csr.getValoracioById(Integer.valueOf(idVal)); // get the ActivitatDossier instance
			if (valoracio == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(valoracio).build();
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}	
	
	/**
	 * Returns a list of <code>Valoracio</code>
	 * @return A Standard RESTful response.
	 */
	@GET()
	@Path("")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@PermitAll
	public Response getValoracions() {
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		
		// Get the ActivitatDossier list and return the response:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Valoracio[] valoracions = null;
		try {
			valoracions = csr.getValoracioList(); // get the ActivitatDossier list
			if (valoracions == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			return Response.ok(valoracions).build();
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
	@Path("{idVal}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@RolesAllowed({"USER","ADMIN"})
	public Response updateDescripcioActivitatDossier(@PathParam("idVal") String idVal,
			@FormParam("idDossier")String idDossier,
			@FormParam("idServei") String idServei,
			@FormParam("idParam") String idParam,
			@FormParam("valor") String valor,
			@Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("putValoracio() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}
		System.out.println("updateValoracio() - new idServei: [" + idServei + "] - new idParam: [" + idParam  + "] - new valor: [" + valor + "]");
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path and form parameter:
		if (idVal == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idVal not specified").build();
		if (!(Integer.valueOf(idVal)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idVal [" + idVal + "]").build();
		
		if (idDossier == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier de valoracio is null").build();
		if (idDossier.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier de valoracio is empty").build();
		if (idDossier=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "idDossier de valoracio [" + idDossier + "] no es vàlid").build();
		
		if (idServei == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei de valoracio is null").build();
		if (idServei.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei de valoracio is empty").build();
		if (idServei=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "idServei de valoracio [" + idServei + "] no es vàlid").build();
		
		if (idParam == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idParam de valoracio is null").build();
		if (idParam.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "idParam  de valoracio is empty").build();
		if (idParam=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "idParam de valoracio [" + idParam + "] no es vàlid").build();
		
		if (valor == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "valor of valoracio is null").build();
		if (valor.length() == 0)
			return Response.status(Status.BAD_REQUEST).header("Message", "valor  of valoracio is empty").build();
		if (valor=="")
			return Response.status(Status.BAD_REQUEST).header("Message", "valor of valoracior [" + valor + "] no es vàlid").build();
		
		System.out.println("updateValoracio() - isServei: [" + idServei + "] - idParam: [" + idParam + "] - valor: [" + valor + "]");
		
		// Get the ActivitatDossier details and modify the name:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Valoracio valoracio = null;
		try {
			valoracio = csr.getValoracioById(Integer.valueOf(idVal)); // Get ActivitatDossier details
			if (valoracio == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			
			Dossier dossier = csr.getDossierById(Integer.valueOf(idDossier)); // get the Dossier instance
			if (dossier == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			valoracio.setIdDossier(dossier); // Set new dossier in ActivitatDossier
			
			Servei servei = csr.getServeiById(Integer.valueOf(idServei)); // get the Dossier instance
			if (servei == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			valoracio.setIdDossier(dossier); // Set new dossier in ActivitatDossier
			
			Parametre parametre = csr.getParametreById(Integer.valueOf(idParam)); // get the Servei instance
			if (parametre == null) // Not found
				return Response.status(Status.NO_CONTENT).build();
			valoracio.setIdParam(parametre); // Set new servei in ActivitatDossier
			
			valoracio.setValor(Integer.parseInt(valor));
			
			csr.updateValoracio(valoracio); // Persist modification NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok(valoracio).build();
	}
	
	/**
	 * Adds a new <code>Valoracio</code> instance. The data is received through the RESTful service's <b>Request Body</b> as an XML String.
	 * @param valoracio The <code>ActivitatDossier</code> instance to add.
	 * @return  A Standard RESTful response. If OK, it returns the text "Succeeded".
	 */
	@POST()
	@Path("")
	@Consumes("application/xml")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response postValoracio(Valoracio valoracio, @Context SecurityContext securityContext) {

		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("postValoracio() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check incoming Entity instance:
		if (valoracio == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Valoracio code not specified").build();
		System.out.println("postValoracio() - Valoracio details: [" + valoracio.toString() + "]");
		if (!valoracio.hasValidInformation())
			return Response.status(Status.BAD_REQUEST).header("Message", "Valoracio instance contains bad information").build();
		
		// Check environment:
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();

		// Add the new ActivitatDossier:
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		try {
			//FIXME En teoria no necessitariem controlar el id del ActivitatDossier
			if (csr.getValoracioById(valoracio.getId()) != null) // Code of new ActivitatDossier already in use
				return Response.status(Status.BAD_REQUEST).header("Message", "Valoracio with code [" + valoracio.getId() + "] already exists").build();
			csr.addValoracio(valoracio); // Persist new ActivitatDossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}

		return Response.ok("Succeeded").build();
	}
	
	/**
	 * Removes an existent <code>Valoracio</code> instance. The unique <b>code</b> in the Path parameter will be used to locate
	 * the Valoracio to remove.
	 * @param idVal The code of the Valoracio instance to remove.
	 * @return A standard <code>Response</code> instance. If its status is OK, it will return the text "Succeeded" in the response body.
	 */
	@DELETE()
	@Path("{idVal}")
	@Produces("text/plain")
	@RolesAllowed("ADMIN")
	public Response deleteValoracio(@PathParam("idVal") String idVal, @Context SecurityContext securityContext) {
		
		// Display security information if available:
		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null) 
				System.out.println("deleteValoracio() - Authenticated user: [" + securityContext.getUserPrincipal().getName() + "]");
		}

		// Check environment:
		System.out.println("deleteValoracio() - idVal: [" + idVal +"]");
		if (!readyToProcess) // Cannot process requests.
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Moro service not available").build();
		
		// Check path parameter:
		if (idVal == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "idVal not specified").build();
		if (!(Integer.valueOf(idVal)>=0))
			return Response.status(Status.BAD_REQUEST).header("Message", "Invalid idVal [" + idVal + "]").build();
		
		// Delete del ActivitatDossier (if exists):
		EnquestesServiceRemote csr = factory.getEnquestesService(); // Get the Session Interface reference
		Valoracio valoracio = null;
		try {
			valoracio = csr.getValoracioById(Integer.valueOf(idVal)); // Get ActivitatDossier details
			if (valoracio == null) // Not found
				return Response.status(Status.BAD_REQUEST).header("Message", "Valoracio with idAd [" + idVal + "] does not exist").build();
			/* TODO Mirar si fa falta controlar algo.
			 * if (activitatDossier.hasCustomers()) // ActivitatDossier has customers. This is not allowed
				return Response.status(Status.BAD_REQUEST).header("Message", "ActivitatDossier with code [" + idAd + "] has Customers assigned").build();*/

			csr.deleteValoracio(Integer.valueOf(idVal)); // Delete ActivitatDossier NOW
		} catch (MoroException ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
		
		return Response.ok("Succeeded").build();
	}
	
}
