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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Analytics are predefined optimized queries and they have fixed results so no
 * further actions needed.
 *
 * @author rande001
 */
@Path("/analytics")
@Api(value = "Specific request for specific projects")
@Produces({"application/json"})

public class AnalyticsServlet extends Worker {

    @GET
    @Path("/{analytics_context}/{analytics_topic}")
    @ApiOperation(value = "Return all the information for the given soilparamid.")
    public Response ExecuteAnalyticsQuery(
            @ApiParam(value = "A token allows accesss to resources. ", required = false) @HeaderParam("token") String token,
            // @ApiParam(value = "Define the number of resources returned min = 0 , max = 250 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Analytics topic", required = true) @QueryParam("analytics_topic") String topic,
            @ApiParam(value = "Analytics context (e.g. a specific project)", required = true) @QueryParam("analytics_context") String context,
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A valid WKT geometry (point or polygon). Default in epsg 28992 when not please supply epsg", required = false) @QueryParam("geometry") String geom,
            @ApiParam(value = "The epsg for the supplied geomery. Default 28992 (RD). Currently 4326 (WGS84) and 28992 (RD) are supported", required = false) @QueryParam("epsg") Integer epsg
            // @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset
    ) {
        reset();
        
        //
        // Fetch the query from the config table and execute that.
        //
        
        String query = "todo";
        return doWorkWithTokenValidation(query, token);
    }
}
