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

import agrodatacube.wur.nl.formatter.AdapterTableResultGeoJsonFormatter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Response;

/**
 *
 * @author rande001
 */
@Path("/codes")
@Api(value = "Provide information about codes (cropcodes, soilcodes etc) used in the Agrodatacube. Crops codes are derived and corrected from the originating datasets )")
@Produces({"application/json"})
public class CodesServlet extends Worker {

    public CodesServlet() {
        super();
        setResource("/codes");
    }

    @GET
    @ApiOperation(value = "Provide information for a given cropcode")
    @Path("/cropcodes/{cropcode}")
    public Response getCropcodes(
            @ApiParam(value = "The cropcode you want information for", required = true) @PathParam("cropcode") String cropcode,
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
//            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
//        setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }

        String query = "select * from cropinformation where upper(cropcode) = upper(?) order by cropid";
        ArrayList<Object> params = new ArrayList<>();
        params.add(cropcode);
        return doWorkWithoutTokenValidation(query,null,params, new AdapterTableResultGeoJsonFormatter());
    }

    @GET
    @Path("/cropcodes")
    @ApiOperation(value = "Provide information for all available cropcodes")
    public Response getCropcodes(
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
  //          @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
    //    setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }

        return doWorkWithoutTokenValidation("select * from cropinformation order by cropid",null,new ArrayList<>(), new AdapterTableResultGeoJsonFormatter());
    }

    /**
     *
     * @param soilcode
     * @param page_size
     * @param page_offset
     * @return
     */
    @GET
    @Path("/soilcodes/{soilcode}")
    @ApiOperation(value = "Provide information for a soilcode")
    public Response getSoilcodes(
            @ApiParam(value = "The soilcode for which you want information. ", required = true) @PathParam("soilcode") String soilcode,
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
//            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
   //     setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }

        String query = String.format("select * from soilinformation where upper(soilcode) = upper(?)");
        ArrayList<Object> params = new ArrayList<>();
        params.add(soilcode);
        return doWorkWithoutTokenValidation(query, null,params, new AdapterTableResultGeoJsonFormatter());
    }

    /**
     *
     * @param page_size
     * @param results
     * @param page_offset
     * @return
     */
    @GET
    @Path("/soilcodes")
    @ApiOperation(value = "Provide information for all soilcodes")

    public Response getSoilcodes(
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

        return doWorkWithoutTokenValidation("select * from soilinformation order by soilcode",null,new ArrayList<>(), new AdapterTableResultGeoJsonFormatter());
    }
}
