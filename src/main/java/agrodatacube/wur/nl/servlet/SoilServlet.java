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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rande001
 */
@Path("/soiltypes")
@Api(value = "Provide information about soils")
@Produces({"application/json"})
public class SoilServlet extends Worker {

    public SoilServlet() {
        super();
        setResource("/soiltypes");
    }

    @GET
    @Path("/")
    @ApiOperation(value = "Return the intersections of the supplied geometry and soilmap. If no epsg is provided epsg = 28992 (RD) is assumed. Currently only epsg's 28992 and 4326 (WGS 84) are supported")
    public Response getSoilDataForArea(
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
            @ApiParam(value = "A valid WKT geometry (point or polygon). Default in epsg 28992 when not please supply epsg", required = false) @QueryParam("geometry") String geom,
            @ApiParam(value = "The epsg for the supplied geomery. Default 28992 (RD). Currently 4326 (WGS84) and 28992 (RD) are supported", required = false) @QueryParam("epsg") Integer epsg,
            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
        setResults(results);
        //
        // Paging parameters
        //
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }
        //
        // The query
        //
        setOutputEpsg(28992);
        String to4326_begin = "";
        String to4326_end = "";
        if (output_epsg != null) {
            if (output_epsg == 4326) {
                to4326_begin = "st_transform(";
                to4326_end = ",4326)";
                setOutputEpsg(4326);
            }
        }
        ArrayList<Object> params = new ArrayList<>();
        String query = String.format(
                "select id as entityid"
                  + " , soilcode"
                  + " , soilname"
                  + " , soiltype"
                  + " , st_asgeojson(%s geom %s , %d) as geom"
                  + " , st_area(geom) as area "
                  + " , st_perimeter(geom) as perimeter "
                + "from bod50000 order by id ", to4326_begin, to4326_end, getNumberOfdecimals());
        if (geom != null) {
            String theGeom = transformTo28992EWKT(epsg, geom);
                query = String.format(
                       "with foo as (select st_geomfromewkt(?) as geom) "
                     + "select * from (select id as entityid"
                           + ", soilcode"
                           + ", soilname"
                           + ", soiltype"
                           + ", st_asgeojson(%s st_intersection(foo.geom, bod50000.geom) %s , %d ) as geom "
                           + ", st_area(st_intersection(foo.geom, bod50000.geom)) as area"
                           + ", st_perimeter(st_intersection(foo.geom, bod50000.geom)) as perimeter "
                        + "from bod50000 , foo where st_intersects(foo.geom,bod50000.geom)) as not_foo order by area desc", to4326_begin, to4326_end, getNumberOfdecimals());
                params.add(theGeom);            
        }
        return doWorkWithTokenValidation(query, token, params);
    }

    @GET
    @Path("/{entityid}")
    @ApiOperation(value = "Return all the information for the given soilmap entityid.")
    public Response getSoilDataForEntiry(
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
            @ApiParam(value = "The entityid (id of area in soilmap) for which you want information. ", required = true) @PathParam("entityid") Integer entityid,
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
        setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }
        setOutputEpsg(28992);
        String to4326_begin = "";
        String to4326_end = "";
        if (output_epsg != null) {
            if (output_epsg == 4326) {
                to4326_begin = "st_transform(";
                to4326_end = ",4326)";
                setOutputEpsg(4326);
            }
        }
        String query = String.format("select id as entityid, soilcode, soilname, soiltype, st_asgeojson(%s geom %s , %d ) as geom,  st_area(geom) as area , st_perimeter(geom) as perimeter  from bod50000 where id=%d", to4326_begin, to4326_end, getNumberOfdecimals(), entityid);
        return doWorkWithTokenValidation(query, token);
    }

}
