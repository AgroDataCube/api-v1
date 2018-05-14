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
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Retrieve data from the AHN (Dutch Raster Height Map).  
 * @author Rande001
 */
@Path("/ahn")
@Api(value = "Provide information about height for the fields. Height is from the AHN 25m rasterdataset containg averge height in a gridcell in cm compared to NAP")
@Produces({"application/json"})
public class AHNServlet extends Worker {

   
    public AHNServlet() {
        super();
        setResource("/ahn");
    }
    
    /**
     * Retrieve the zonal statistics for the given geometry. 
     * @param geom   valid WKT Geometry (must be readable by Postgis.
     * @param epsg   valid epsg (default 28992, the dutch standard.
     * @param token  valid token that allows access to this resource.
     * @return 
     */
    @GET
    @Path("")
    @ApiOperation(value = "Return the zonal statistics for height using the supplied geometry. If no epsg is provided epsg = 28992 (RD) is assumed. Currently only epsg's 28992 and 4326 (WGS 84) are supported")
    public Response getAHNForGeometry(
            @ApiParam(value = "A valid WKT geometry (point or polygon). Default in epsg 28992 when not please supply epsg", required =true) @QueryParam("geometry") String geom,
            @ApiParam(value="The epsg for the supplied geomery. Default 28992 (RD). Currently 4326 (WGS84) and 28992 (RD) are supported" , required = false) @QueryParam("epsg") Integer epsg,
            @ApiParam(value = "A token allows accesss to resources. !", required = false) @HeaderParam("token") String token) {               
        ArrayList<Object> params = new ArrayList<>();
        String query = "";
        reset();
        //
        // We need at least a geometry. 
        //
        if (geom != null) {
            //
            // If an epsg is supplied then probably a transformation is needed. 
            //
            if (epsg != null) {
                query = "With foo as (select st_transform(st_geomfromewkt(?),28992) as geom) "
                        + "select  sum(d.count) count,  max(st_area(geom)) area, sum(d.count*d.mean)/sum(d.count)  mean  ,   min( d.min),max(  d.max)"
                        + " from ("
                        + "SELECT (ST_SummaryStats(ST_Clip(r.rast,foo.geom))).*, foo.geom geom"
                        + " FROM ahn r, foo"
                        + " WHERE st_intersects(r.rast, foo.geom)) as d";
                params.add("srid=" + epsg + ";" + geom);
            } else {
                query = "With foo as (select st_geomfromewkt(?) as geom) "
                        + "select  sum(d.count) count,  max(st_area(geom)) area,  sum(d.count*d.mean)/sum(d.count)  mean  ,   min( d.min),max(  d.max)"
                        + " from ("
                        + "SELECT (ST_SummaryStats(ST_Clip(r.rast,foo.geom))).*, foo.geom geom"
                        + " FROM ahn r, foo"
                        + " WHERE st_intersects(r.rast, foo.geom)) as d";
                params.add("srid=28992;" + geom);
            }
        }
        else {
            return createErrorResponse(413, geom);
        }
        return doWorkWithTokenValidation(query, token,params);
    }
}
