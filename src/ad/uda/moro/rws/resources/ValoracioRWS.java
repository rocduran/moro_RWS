package ad.uda.moro.rws.resources;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
}
