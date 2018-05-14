/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.exec;

import agrodatacube.wur.nl.result.AdapterTableResult;
import agrodatacube.wur.nl.formatter.AdapterTableResultGeoJsonFormatter;
import agrodatacube.wur.nl.result.Paging;
import agrodatacube.wur.nl.db.PostgresAdapterDataSource;
import agrodatacube.wur.nl.formatter.AdapterTableResultFormatter;
import java.util.ArrayList;

/**
 *
 * @author rande001
 */
public class Executor {

    /**
     * Execute a query that returns a list objects.
     *
     * @param query
     * @return
     * @throws Exception
     */
    public static String execute(String query) throws Exception {
        AdapterTableResult result = new PostgresAdapterDataSource().executeQuery(query + " LIMIT " + Paging.DEFAULT_PAGE_LIMIT + " OFFSET 0", new ArrayList<Object>());
        return new AdapterTableResultGeoJsonFormatter().format(result);

    }

    /**
     * Execute a query that returns and image.
     *
     * @param query
     * @return
     * @throws Exception
     */
    public static byte[] executeForImage(String query) throws Exception {
        return new PostgresAdapterDataSource().executeQuery(query);
    }

    /**
     * Execute the sql statement and apply all the params in params to the query
     * using jdbc.setObject(...).
     *
     * @param query
     * @param params
     * @return
     * @throws Exception
     */
//    public static String execute(String query,
//            ArrayList<Object> params) throws Exception {
//        AdapterTableResult result = new PostgresAdapterDataSource().executeQuery(query + " LIMIT " + Paging.DEFAULT_PAGE_LIMIT + " OFFSET 0", params);
//        return new AdapterTableResultGeoJsonFormatter().format(result);
//    }

    public static ExecutorResult execute(String query,ArrayList<Object> params, Paging paging) throws Exception {
        return execute(query, params, paging, new AdapterTableResultGeoJsonFormatter());
    }

    public static ExecutorResult execute(String query,ArrayList<Object> params, Paging paging, AdapterTableResultFormatter formatter) throws Exception {
        String pagingString = "";
        
        //
        // Default geojson however on some cases different formatters needed or preferred.
        //
        
        if (paging != null) {
            if (paging.doExecuteCount()) {
                // formatter = new AdapterTableResultJsonSingleValueFormatter();
                // DOES NOT WORK WHEN WITH CLAUSE query = "select count(*) nrhits from (" + query + ") as nrhits";
            } else if (paging.getAllData()) {
                pagingString = ""; // no action
            } else {
                pagingString = " limit "+paging.getSize() + " offset " + paging.getOffset();
            }
        }
        AdapterTableResult result = new PostgresAdapterDataSource().executeQuery(query + pagingString, params);

        //
        // Save area and result.
        //
        
        ExecutorResult res = new ExecutorResult(formatter.format(result), result.getArea());
        return res;
    }

}
