package ad.uda.moro.rws;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author rduran &amp; mmiret
 *
 */
@RequestScoped
@Path("dossiers")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class DossierRWS {
	
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

}
