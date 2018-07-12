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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * This servlet returns information for the soil physical units.
 *
 * @author rande001
 */
@Path("/soilparams")
@Api(value = "Provide information about soil physical parameters (for more detailed information see http://content.alterra.wur.nl/Webdocs/PDFFiles/Alterrarapporten/AlterraRapport2387.pdf)")
@Produces({"application/json"})
public class BofekServlet extends Worker {

    @GET
    @Path("/{soilparamid}")
    @ApiOperation(value = "Return all the information for the given soilparamid.")
    public Response getSoilDataForEntiry(
            @ApiParam(value = "A token allows accesss to resources. ", required = false) @HeaderParam("token") String token,
            @ApiParam(value = "The soilparamid (id of area in soilparameters) for which you want information. ", required = true) @PathParam("soilparamid") Integer soilparamid,
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
        String query = String.format("select bofek2012 soilparamid\n"
                + ",opp_aandeel\n"
                + ",bodem_nr\n"
                + ",eenheid\n"
                + ",gebruik\n"
                + ",laag_nr layer_nr\n"
                + ",hor_code\n"
                + ",diepte_b start_depth\n"
                + ",diepte_o end_depth \n"
                + ",orgstof\n"
                + ",orgstof_p10\n"
                + ",orgstof_p90\n"
                + ",lutum\n"
                + ",lutum_p10\n"
                + ",lutum_p90\n"
                + ",silt\n"
                + ",leem\n"
                + ",leem_p10\n"
                + ",leem_p90\n"
                + ",m50\n"
                + ",m50_p10\n"
                + ",m50_p90\n"
                + ",ph_kcl\n"
                + ",ph_p10\n"
                + ",ph_p90\n"
                + ",cac03\n"
                + ",dichtheid\n"
                + ",materiaal\n"
                + ",a_waarde\n"
                + ",staring_bouwsteen\n"
                + ",opmerking from bofek2012_lookup where bofek2012=%d order by laag_nr", soilparamid);
        return doWorkWithTokenValidation(query, token);
    }

}
