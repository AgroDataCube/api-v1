/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 *
 * STILL UNDER DEVELOPMENT
 */
package agrodatacube.wur.nl.servlet;

import agrodatacube.wur.nl.exec.Executor;
import agrodatacube.wur.nl.exec.ExecutorResult;
import agrodatacube.wur.nl.token.Registration;
import agrodatacube.wur.nl.token.AccessToken;
import agrodatacube.wur.nl.token.TokenValidationResult;
import agrodatacube.wur.nl.token.TokenValidator;
import agrodatacube.wur.nl.result.AdapterTableResult;
import agrodatacube.wur.nl.result.Paging;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import agrodatacube.wur.nl.formatter.AdapterTableResultFormatter;
import agrodatacube.wur.nl.formatter.AdapterTableResultGeoJsonFormatter;
import agrodatacube.wur.nl.token.AccessTokenFactory;

/**
 * This class is the base of all servlets. It verifies tokens, executes queries
 * and returns results.
 *
 * @author rande001
 */
public class Worker {

    @Context
    private HttpServletRequest httpServletRequest;
    private final Paging paging;
    private Integer outputEpsg = 28992;

    private String resource;

    protected final void setResource(String s) {
        resource = s;
    }

    public Worker() {
        paging = new Paging();
    }

    /**
     * Execute and use the default formatters.
     *
     * @param query
     * @param token
     * @return
     */
    protected Response doWorkWithTokenValidation(String query,
            String token) {
        return Worker.this.doWorkWithTokenValidation(query, token, new ArrayList<>());
    }

    protected Response doWorkWithTokenValidation(String query,
            String token,
            Class<AdapterTableResultFormatter> c) throws IllegalAccessException, InstantiationException {
        return doWorkWithTokenValidation(query, token, new ArrayList<>(), c.newInstance());
    }

    protected Response doWorkWithTokenValidation(String query,
            String token,
            ArrayList<Object> params) {
        return doWorkWithTokenValidation(query, token, params, new AdapterTableResultGeoJsonFormatter());
    }

    protected Response doWorkWithTokenValidation(String query,
            String token,
            ArrayList<Object> params,
            AdapterTableResultFormatter f) {
        try {

            //
            // Until may 1st 2018 no license (== token) is needed. So of none provided create an opendatatoken.
            //
            if (token == null) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.MILLISECOND, 0);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.DAY_OF_MONTH, 5);
                c.set(Calendar.MONTH, 4); // Jan = 0 so May = 4.
                c.set(Calendar.YEAR, 2018);

                if (c.getTime().after(new Date(System.currentTimeMillis()))) {
                    token = AccessTokenFactory.getInstance().createTokenForIp(getRemoteIP()).toString();
                }
            } else if (Registration.tokenIsKnown(token)) {

            }

            //
            // After may 1st 2018 no ip based tokens are generated.
            //
            if (token == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("{ \"status\" : \"No token supplied, see https://agrodatacube.wur.nl/api/register.jsp !!!!!!!\"}").build();
            }

            //
            // Retrieve the token from the token registration table to see if it is a valid token we issued.
            //            
            // token = TokenValidator.getTokenForIp(getRemoteIP());
            AccessToken accessToken = AccessTokenFactory.getInstance().decodeToken(token);
            try {
                TokenValidationResult tokenAllowsAccess = TokenValidator.tokenAllowsAccess(accessToken, resource);
                if (!tokenAllowsAccess.isOk()) {
                    return Response.status(403).entity(tokenAllowsAccess.getError()).build();
                }
            } catch (Exception ex) {
                return Response.status(500).entity("Token validation failed !").build();
            }

            //
            // Now do the work without further validation.
            //
            return doWorkWithoutTokenValidation(query, accessToken, params, f);

        } catch (Exception e) {
            AdapterTableResult t = new AdapterTableResult();
            t.setStatus(e.getMessage());
            try {
                return Response.status(500).entity(f.format(t)).build();
            } catch (Exception q) {
                return Response.status(500).entity(q.getMessage()).build();
            }

        }
    }

    /**
     * Execute the work without any token validation. However token is needed
     * for logging of requests.
     *
     * @param query
     * @param accessToken
     * @param params
     * @param f
     * @return
     */
    protected Response doWorkWithoutTokenValidation(String query,
            AccessToken accessToken,
            ArrayList<Object> params,
            AdapterTableResultFormatter f) {
        try {
            ExecutorResult result = Executor.execute(query, params, paging);
            if (accessToken != null) {
                Registration.updateUsageInformation(accessToken, result.getArea(), getRemoteIP());
            }
            
            // TODO: AJAX CROSS DOMAIN 
            return Response.status(200).header("Content-type", "application/json").entity(result.getResult())
//                                       .header("Access-Control-Allow-Origin", "*")
//                                       .header("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE")
//                                       .header("Access-Control-Allow-Headers", "Content-type,token")
//                                       .header("Access-Control-Max-Age", "86400")
                    .build();
        } catch (Exception e) {
            AdapterTableResult t = new AdapterTableResult();
            t.setStatus(e.getMessage());
            try {
                return Response.status(500).entity(f.format(t)).build();
            } catch (Exception q) {
                return Response.status(500).entity(q.getMessage()).build();
            }
        }
    }

    protected void setPageSize(int pagesize) {
        paging.setSize(pagesize);
    }

    protected void setOffset(int offset) {
        paging.setOffset(offset);
    }

    /**
     * Check if the token allows access.
     *
     * @param urlString
     * @param token
     * @return
     */
    protected Response accessAllowed(String urlString,
            AccessToken token) {
        if (token == null) {
            return Response.status(401).entity("Authentication required").build(); // 401 Requiers authentication
        }

        if (!token.allowsAccessTo(urlString)) {
            return Response.status(403).entity("Forbidden").build(); // 403 Forbidden
        }

        // TODO: See if token still usable (date, limits etc)
        return null; // Ok zit nu in TokenValidator
    }

    /**
     * Check whether the token allows access to the resource.
     *
     * @param urlString
     * @param token
     * @return
     */
    protected Response accessAllowed(String urlString,
            String token) {
        try {
            if (token == null) {
                return Response.status(401).entity("Authentication required").build();
            }
            return accessAllowed(urlString, AccessTokenFactory.getInstance().decodeToken(token));
        } catch (Exception e) {
            return Response.status(400).build();
        }
    }

    /**
     * Get the ip adres of the client.
     *
     * @return
     * @throws Exception
     */
    protected String getRemoteIP() throws Exception {

//        Enumeration headerNames = httpServletRequest.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String nextName = (String) headerNames.nextElement();
//            System.out.println(nextName + " -> " + httpServletRequest.getHeader(nextName));
//        }
        String ipAddress = httpServletRequest.getHeader("x-forwarded-for");
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        // System.out.println("IP Adres -> " + ipAddress);
        return ipAddress;
    }

    /**
     * Create an internal server error response.
     *
     * @param s
     * @return
     */
    protected Response createErrorResponse(String s) {
        return createErrorResponse(500, resource);
    }

    protected Response createErrorResponse(int status,
            String message) {
        return Response.status(status).entity("{ \"status\" : \"" + message + "\"").build();
    }

    /**
     * Determine the number of decimals in the geojeson output. This depends on
     * the requested output format.
     *
     * @param epsg
     * @return
     */
    protected int getNumberOfdecimals() {
//        if (outputEpsg == null) {
//            return 3; // 28992 Dutch national system in meters.
//        }
        if (outputEpsg == 28992) {
            return 3; // 28992 Dutch national system in meters.
        }

        //
        // Only other currently excepted is wgs84 (epsg 4326) so 5 decimals.
        //
        return 7;
    }

    protected void setOutputEpsg(int epsg) {
        outputEpsg = epsg;
    }

    protected void reset() {
        paging.setAllData(false);
        paging.setExecuteCount(false);
        setOffset(0);
        setPageSize(Paging.DEFAULT_PAGE_LIMIT);
    }

    /**
     * Set the optional value form the result query parameter.
     *
     * @param s
     */
    protected void setResults(String s) {

        paging.setAllData(false);
        paging.setExecuteCount(false);

        //
        // disable because it does not work with SQL WITH
        //
//        if (s != null) {
//            paging.setAllData(s.equalsIgnoreCase("alldata"));     // Return all data
//            paging.setExecuteCount(s.equalsIgnoreCase("nrhits")); // Only return nr of hits
//        }
    }
}
