/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agrodatacube.wur.nl.servlet;

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
 *
 * @author rande001
 */
@Path("/meteostations")
@Api(value = "Provide information for meteostations")
@Produces({"application/json"})
public class MeteoStations extends Worker {

    public MeteoStations() {
        super();
        setResource("/meteostations");
    }

    @GET
    @Path("")
    @ApiOperation(value = "Return the data all meteostations. ")
    public Response getMeteoStations(
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
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
        String query = String.format("select meteostationid,name,wmocode,lon,lat,alt,source,provider,st_asgeojson(%s geom %s ,%d ) as geom from knmi_meteo_station order by meteostationid ", to4326_begin, to4326_end, getNumberOfdecimals());
        return doWorkWithTokenValidation(query, token);
    }

    @GET
    @Path("/{meteostationid}")
    @ApiOperation(value = "Return the data for the given meteostation.")
    public Response getMeteoStations(
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
            @ApiParam(value = "A token allows accesss to resources. ", required = true) @HeaderParam("token") String token,
            @ApiParam(value = "The id off the meteostation for which you want information. ", required = true) @PathParam("meteostationid") Integer stationId,
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
        String query = String.format("select   meteostationid,name,wmocode,lon,lat,alt,source,provider,st_asgeojson(%s geom %s , %d ) as geom from knmi_meteo_station where meteostationid=%d", to4326_begin, to4326_end, getNumberOfdecimals(), stationId);
        return doWorkWithTokenValidation(query, token);
    }
}
