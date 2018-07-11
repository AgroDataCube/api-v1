/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agrodatacube.wur.nl.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rande001
 */
@Path("/sources")
@Api(value = "Information about available data")
@Produces({"application/json"})
public class SourcesServlet extends Worker {
    
    @GET
    @Path("/")
    @ApiOperation(value="Return all information about availabe data")    
    /**
     * All documentation has been moved to Postman (https://documenter.getpostman.com/view/3862510/RVnSHh76).
     */
    public Response getInformation() {
        return Response.ok().entity("{ \"Please see the information at \" : \"https://documenter.getpostman.com/view/3862510/RVnSHh76\" }").build();
    }    
}
