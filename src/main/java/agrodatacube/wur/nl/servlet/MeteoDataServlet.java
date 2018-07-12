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

import agrodatacube.wur.nl.result.DateExpression;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Rande001
 */
@Path("/meteodata")
@Api(value = "Provide meteodata on station level or on field level. Fields have meteostations assigned to them (based on nearest distance).")
@Produces({"application/json"})
public class MeteoDataServlet extends Worker {

    public MeteoDataServlet() {
        super();
        setResource("/meteodata");
    }

    @GET
    @Path("")
    @ApiOperation(value = "Return meteodata. This can be queried by period (fromyear, toyear) and meteostationid. For dates ducktyping is applied. Dates with 8 chars are considered to be in yyyymmdd format, dates with 6 chars are considered to be in yyyymm format and dates with 4 chars are considered to be in the yyyy format. Supplied dates are included in the resukt set.")
    public Response getMeteoDataForStation(@Context UriInfo uriInfo,
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
            @ApiParam(value = "The id of te meteostation for which you want meteodata", required = true) @QueryParam("meteostation") String stationid,
            @ApiParam(value = "Start date (included) from which you want meteo information for the field", required = false) @QueryParam("fromdate") Integer fromdate,
            @ApiParam(value = "End date (included) up until which you want meteo information for the field", required = false) @QueryParam("todate") Integer todate,
            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset) {
        reset();
        setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }
        String where = " where ";
        String query = "select k.*, (select st_asgeojson(geom) "
                                   + " from knmi_meteo_station s "
                                   + "where s.meteostationid=k.meteostationid) as geom "
                       + "from knmi_meteo_values k ";
        if (stationid != null) {
            query = query.concat(String.format("where meteostationid= %s ", stationid));
            where = " and ";
        }
        if (fromdate != null) {
            query = query.concat(where).concat(DateExpression.create("datum", DateExpression.DateTypeDate, ">=", fromdate));
            where = " and ";
        }
        if (todate != null) {
            query = query.concat(where).concat(DateExpression.create("datum", DateExpression.DateTypeDate, "<=", todate));
        }
        query = query.concat(" order by meteostationid, datum");
        return doWorkWithTokenValidation(query, token);
    }

    @GET
    @Path("/{stationid}/{date}")
    @ApiOperation(value = "Return meteodata for a given station. This can be queried by date and duck typing is applied.")
    public Response getMeteoDataForStationForDate(
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
            @ApiParam(value = "The id off the meteostation for which you want information. ") @PathParam("stationid") String fieldid,
            @ApiParam(value = "The date for which you want information. Date is processed using duck typing ") @PathParam("date") Integer aDate,
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
        String query = String.format("select * from knmi_meteo_values where meteostationid= %s and %s order by datum", fieldid, DateExpression.create("datum", DateExpression.DateTypeDate, "=", aDate));
        return doWorkWithTokenValidation(query, token);
    }
}
