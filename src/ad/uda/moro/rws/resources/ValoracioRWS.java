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

}
