/*
* Copyright 2018 Wageningen Environmental Research
*
* For licensing information read the included LICENSE.txt file.
*
* Unless required by applicable law or agreed to in writing, this software
* is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
* ANY KIND, either express or implied.
 */
package nl.wur.agrodatacube.servlet;

import java.util.Properties;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This servlet returns information for the soil physical units.
 *
 * @author rande001
 */
@Path("/soilparams")
@Produces({"application/json"})
public class BofekServlet extends Worker {

    public BofekServlet() {
        setResource("soilparams");
    }

    
    @GET
    @Path("/{soilparamid}")
    public Response getSoilParamsInfoGet(@Context UriInfo uriInfo, @HeaderParam("token") String token) {
        Properties props = parametersToProperties(uriInfo);
        return getResponse(props, token);
    }
    
    @POST
    @Path("/{soilparamid}")
    public Response getSoilParamsInfoPost(@Context UriInfo uriInfo, @HeaderParam("token") String token) {
        Properties props = parametersToProperties(uriInfo);
        return getResponse(props, token);
    }
    
    @GET
    @Path("/")
    public Response getSoilParamsInfoGetAll(@Context UriInfo uriInfo, @HeaderParam("token") String token) {
        Properties props = parametersToProperties(uriInfo);
        return getResponse(props, token);
    }
    
    @POST
    @Path("/")
    public Response getSoilParamsInfoPostAll(@Context UriInfo uriInfo, @HeaderParam("token") String token) {
        Properties props = parametersToProperties(uriInfo);
        return getResponse(props, token);
    }

//        String query = String.format("select bofek2012 soilparamid\n"
//                + ",opp_aandeel\n"
//                + ",bodem_nr\n"
//                + ",eenheid\n"
//                + ",gebruik\n"
//                + ",laag_nr layer_nr\n"
//                + ",hor_code\n"
//                + ",diepte_b start_depth\n"
//                + ",diepte_o end_depth \n"
//                + ",orgstof\n"
//                + ",orgstof_p10\n"
//                + ",orgstof_p90\n"
//                + ",lutum\n"
//                + ",lutum_p10\n"
//                + ",lutum_p90\n"
//                + ",silt\n"
//                + ",leem\n"
//                + ",leem_p10\n"
//                + ",leem_p90\n"
//                + ",m50\n"
//                + ",m50_p10\n"
//                + ",m50_p90\n"
//                + ",ph_kcl\n"
//                + ",ph_p10\n"
//                + ",ph_p90\n"
//                + ",cac03\n"
//                + ",dichtheid\n"
//                + ",materiaal\n"
//                + ",a_waarde\n"
//                + ",staring_bouwsteen\n"
//                + ",opmerking from bofek2012_lookup where bofek2012=%d order by laag_nr", soilparamid);
//        return doWorkWithTokenValidation(query, token);
//    }
}
