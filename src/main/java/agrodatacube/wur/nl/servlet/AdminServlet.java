/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agrodatacube.wur.nl.servlet;

import agrodatacube.wur.nl.formatter.AdapterTableResultGeoJsonFormatter;
import agrodatacube.wur.nl.token.AccessToken;
import agrodatacube.wur.nl.token.AccessTokenFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Return admin information. This is public available so no token needed.
 *
 * @author Rande001
 */
@Path("/regions")

@Api(value = "Information about administrative regions")
@Produces({"application/json"})
public class AdminServlet extends Worker {

    public AdminServlet() {
        super();
        setResource("/regions");
    }

    @GET
    @Path("/municipalities")
    @ApiOperation(value = "Return all municipalities (gemeentes). These are the municipaliteis off 2015")
    public Response getMunicipalities(
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Define the rank of the first resource to be returned. NOTE first is 0 NOT 1 ", required = false) @QueryParam("page_offset") Integer page_offset,
            @ApiParam(value = "Depending on supplied value (nrhits or alldata) return either nrhits or all rows ", required = false) @QueryParam("result") String results,
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg) {
                reset();
        setResults(results);
        if (page_offset != null) {
            setOffset(page_offset);
        }
        if (page_size != null) {
            setPageSize(page_size);
        }
        String to4326_begin = "";
        String to4326_end = "";
        setOutputEpsg(28992);
        if (output_epsg != null) {
            if (output_epsg == 4326) {
                to4326_begin = "st_transform(";
                to4326_end = ",4326)";
                setOutputEpsg(4326);
            }
        }
        
        String query = String.format("select id, name, st_asgeojson(%s geom %s , %d ) as geom from gemeente_2015 order by id", to4326_begin, to4326_end, getNumberOfdecimals());
        return doWorkWithoutTokenValidation(query, null,new ArrayList<>(), new AdapterTableResultGeoJsonFormatter());
    }

    @GET
    @Path("/postalcodes")
    @ApiOperation("Return the dutch postalcode areas as present in 2016")
    public Response getPostalcodes(
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
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
        String query = String.format("select id, postcode, st_asgeojson(%s geom %s , %d ) as geom from pc6_2016 order by id", to4326_begin, to4326_end, getNumberOfdecimals());
        return doWorkWithoutTokenValidation(query, null,new ArrayList<>(), new AdapterTableResultGeoJsonFormatter());
    }

    @GET
    @Path("/provences")
    @ApiOperation(value = "Return the provences. These are the provences from 2015")
    public Response getProvences(
            @ApiParam(value = "Define the number of resources returned min = 0 , max = 50 ", required = false) @QueryParam("page_size") Integer page_size,
            @ApiParam(value = "Output geometry epsg. Default is 28992 only allowed value 4326", required = false) @QueryParam("output_epsg") Integer output_epsg,
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
        String query = String.format("select id, name, st_asgeojson(%s geom %s , %d) as geom, st_area(geom) as area from provincie_2015 order by id", to4326_begin, to4326_end, getNumberOfdecimals());
        return doWorkWithoutTokenValidation(query, null,new ArrayList<>(), new AdapterTableResultGeoJsonFormatter());
    }

}
