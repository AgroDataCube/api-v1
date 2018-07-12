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

import agrodatacube.wur.nl.exec.Executor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Still under development.
 * @author Rande001
 */
@Path("/image")
@Api(value = "Return a raster for the supplied field")
@Produces({"application/json"})
public class RasterServlet extends Worker {

    @GET
    @Path("/{fieldid}/ahn")
    @ApiOperation("Provide a raster for AHN for this field")
    public Response getAHNForField(
            @ApiParam(value = "Id off the field you want height information for", required = true) @PathParam("fieldid") Integer fieldid,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token) {

        byte[] result = new byte[0];
        try {
            
            String query = String.format("SELECT ST_AsGDALRaster(rast, 'GTiff') "
                    + "FROM (SELECT st_union(ST_Clip(r.rast,p.geom)) as rast "
                    + "                             FROM ahn2_5m r"
                    + "                                , gewaspercelen p "
                    + "                            WHERE p.fieldid = %d "
                    + "                              AND st_intersects(r.rast, p.geom)"
                    + "                          ) as foo", fieldid);
            result = Executor.executeForImage(query);
        } catch (Exception e) {
            result = e.getMessage().getBytes();
        }
        // todo count cells for area (how to get celsize ???) or use area of polygon
        return Response.status(200).header("Content-type", "application/json").entity(result).build();
    }

    /**
     * Create an image for a provided geometry. 
     * 
     * Causes runtime gdal errors and stangely large polygons (e.g. 4014086) don't cause errors. 
     * 
     * http://localhost:8084/api/v1/rest/image/ahn?geometry=polygon((100000%20400000,100000%20401000,101000%20401000,101000%20400000,100000%20400000)) returns ERROR: rt_raster_to_gdal_mem: Could not create a GDALDataset to convert into
     * 
     * To be investigated.
     * 
     * @param geom
     * @param epsg
     * @param token
     * @return 
     */
    @GET
    @Path("/ahn")
    @ApiOperation("Provide a raster for AHN for this geometry")
    public Response getAHNForGeometry(
            @ApiParam(value = "A valid WKT geometry (point or polygon). Default in epsg 28992 when not please supply epsg", required = false) @QueryParam("geometry") String geom,
            @ApiParam(value = "The epsg for the supplied geomery. Default 28992 (RD). Currently 4326 (WGS84) and 28992 (RD) are supported", required = false) @QueryParam("epsg") Integer epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token) {

        byte[] result = new byte[0];
        try {
            String query = "";
            if (epsg == null) { // default 28992
                query = String.format("with foo as (select st_geomfromewkt('srid=28992;%s') as geom)", geom);
            } else if (28992 == epsg) {
                query = String.format("with foo as (select st_geomfromewkt('srid=28992;%s') as geom)", geom);
            } else {
                query = String.format("with foo as (select st_trasnform(st_geomfromewkt('srid=%d;%s') as geom)", epsg,geom);
            }
            query = query.concat(" SELECT ST_AsGDALRaster(rast, 'GTiff') "
                    + "FROM (SELECT ST_Clip(r.rast,FOO.geom) as rast "
                    + "                             FROM ahn2_5m r, foo"
                    + "                            WHERE st_intersects(r.rast, foo.geom)"
                    + "                          ) as ahn_foo");
            result = Executor.executeForImage(query);
        } catch (Exception e) {
            result = e.getMessage().getBytes();
        }
        // todo count cells for area (how to get celsize ???)
        return Response.status(200).header("Content-type", "application/json").entity(result).build();
    }

}
