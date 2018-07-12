/*
* Copyright 2018 Wageningen Environmental Research
*
* For licensing information read the included LICENSE.txt file.
*
* Unless required by applicable law or agreed to in writing, this software
* is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
* ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.servlet;

import agrodatacube.wur.nl.formatter.AdapterTableResultJsonFormatter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/status")
@Api(value = "Provide information about the token")
@Produces({"application/json"})
public class StatusServlet extends Worker {

    public StatusServlet() {
        super();
        setResource("/status");
    }

    /**
     * Handle GET requests
     * @param token
     * @return 
     */
    @GET
    @Path("")
    @ApiOperation(value = "Provide information about the token (usae, requests left etc)")
    public Response getTokenInfoGet(@ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token) {
        return getTokenInfo(token);
    }

    /**
     * Handle post requests.
     * @param token
     * @return 
     */
    @POST
    @Path("")
    @ApiOperation(value = "Provide information about the token (usae, requests left etc)")
    public Response getTokenInfoPost(@ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token) {
        return getTokenInfo(token);
    }

    /**
     * Do the work.
     *
     * @param token
     * @return
     */
    private Response getTokenInfo(String token) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(token);
        return doWorkWithoutTokenValidation("select emailadres\n"
                + ",expires\n"
                + ",request_limit\n"
                + ",requests_issued\n"
                + ",(request_limit-requests_issued) request_left\n"
                + ",area_limit\n"
                + ",area_fetched\n"
                + ",(area_limit-area_fetched) area_left\n"
                + ",issuedon\n"
                + "from registration where token = ?", null, params, new AdapterTableResultJsonFormatter());
    }

}
