/*
* Copyright 2018 Wageningen Environmental Research
*
* For licensing information read the included LICENSE.txt file.
*
* Unless required by applicable law or agreed to in writing, this software
* is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
* ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.formatter;

import agrodatacube.wur.nl.result.AdapterResult;
import agrodatacube.wur.nl.result.AdapterTableResult;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author rande001
 */
public class AdapterTableResultGeoJsonFormatter extends AdapterTableResultJsonFormatter {

    protected String getHeader() {
        return "{ \"type\" : \"FeatureCollection\" , \"features\" : [ ";
    }

    protected String getEnd() {
        return "]}";
    }

    /**
     * Format an adapterresult as geojson.
     *
     * <PRE>
     * {
     * "type": "FeatureCollection",
     *      "features": [
     *          {   "type": "Feature",
     *              "geometry": { "type": "Point", "coordinates": [102.0, 0.6] },
     *              "properties": {
     *                  "key": "value"
     *              }
     *          },
     *      ]
     * }
     * </PRE>
     *
     * @param table
     * @param w
     * @throws Exception
     */
    @Override
    public void format(AdapterTableResult table,
            Writer w) throws Exception {

        if (! table.didSucceed()) {
            w.write("{\"status\" : \""+table.getStatus()+"\"}");
            w.flush();
            return;
        }
        String geojsonResult = getHeader();


        String komma = " ";

        //
        // Add the rows from the table, each row is an GeoJson feature.
        //
        int j = 0;
        ArrayList<Object> row;

        int geomIndex = table.getGeomIndex();
        while ((row = table.getRow(j)) != null) {

            //
            // New featre so create header.
            //
            geojsonResult = geojsonResult.concat(komma);

            //
            // If this is next element add a comma for feature separation
            //
            komma = ",";
            geojsonResult = geojsonResult.concat(" { \"type\": \"Feature\" \n");

            // geometry
            geojsonResult = geojsonResult.concat(" , \"geometry\" :  ");
            if (geomIndex >= 0) {
                geojsonResult = geojsonResult.concat((String) row.get(table.getGeomIndex()));
            } else {
                geojsonResult = geojsonResult.concat(" null ");
            }

            // remaining properties
            boolean first = true;
            geojsonResult = geojsonResult.concat(", \"properties\": { ");
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (i != geomIndex) {
                    Object o = row.get(i);
                    if (o != null) {
                        if (! first) {
                            geojsonResult += ",";
                        }
                        first = false;
                        // TODO: Numeriek geen quotes

                        if (row.get(i) instanceof java.lang.String) {
                            geojsonResult += "\"" + table.getColumnName(i) + "\" : " + (row.get(i) == null ? "null" : "\"" + row.get(i) + "\""); 
                        } else if (row.get(i) instanceof java.sql.Date) {
                            geojsonResult += "\"" + table.getColumnName(i) + "\" : " + (row.get(i) == null ? "null" : "\"" + formatDate(row.get(i)) + "\""); // TODO: Formatter ivm datum
                        } else  {
                            geojsonResult += "\"" + table.getColumnName(i) + "\" : " + row.get(i);
                        }
                    }
                }
            }

            //close properties and close feature
            geojsonResult += "}}";
            j++;
        }
        geojsonResult += getEnd();
        w.write(geojsonResult);
        w.flush();
    }

    /**
     *
     * @param tables
     * @param w
     * @throws Exception
     */
    @Override
    public void format(ArrayList<AdapterResult> tables,
            Writer w) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Format een adapterresult als geojson.
     *
     * <PRE>
     * {
     * "type": "FeatureCollection",
     *      "features": [
     *          {
     *              "type": "Feature",
     *              "geometry": {
     *              "type": "Point",
     *              "coordinates": [102.0, 0.6]
     *          },
     *          "properties": {
     *              "prop0": "value0"
     *          }
     *          },
     *      ]
     * }
     * </PRE>
     *
     * @param table
     * @return
     * @throws Exception
     */
    @Override
    public String format(AdapterTableResult table) throws Exception {
        StringWriter w = new StringWriter();
        format(table, w);
        w.flush();
        return w.toString();
    }

}
