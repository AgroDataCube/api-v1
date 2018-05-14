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
import java.util.Iterator;

/**
 *
 * @author Yke
 */
public class AdapterTableResultJsonFormatter extends AdapterTableResultFormatter {

    public AdapterTableResultJsonFormatter() {
        super();

    }

    /**
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

    @Override
    public void format(AdapterTableResult table,
            Writer w) throws Exception {

        //
        // If it did not succeed return an error
        //
        // Perceel info
        if (!table.didSucceed()) {
            w.write(" { \"status\" : " + JSONizer.toJson(table.getStatus()) + "}"); //todo
            return;
        }

        //
        // Valid result.
        //
        w.write("{ ");

        //
        // add the properties (all parameters etc so NOT the results)
        //
        Iterator<Object> iterator = table.getProps().keySet().iterator();

        String komma = ",";
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            w.write("\"" + name + "\" : \"");
            w.write(table.getProps().getProperty(name));
            w.write("\"");
            w.write(komma);
        }

        if (table.getQueryString() != null) {
            w.write("   \"query\" : " + JSONizer.toJson(table.getQueryString()) + ", ");
        }
        w.write("   \"status\" : \"ok\" , \"data\" : [ ");
        String jsonRow = "";
        komma = " ";

        int j = 0;
        ArrayList<Object> row;
        while ((row = table.getRow(j)) != null) {
            w.write(komma);
            komma = ",";
            jsonRow = "{ ";
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (i > 0) {
                    jsonRow += ",";
                }
                Object o = row.get(i);
                jsonRow += "\"" + table.getColumnName(i) + "\" : " + (row.get(i) == null ? "null" : "\"" + row.get(i) + "\""); // TODO: Formatter ivm datum
            }
            jsonRow += "}";
            w.write(jsonRow);
            j++;
            w.flush();
        }
        w.write("]}");
        w.flush();
    }

    @Override
    public void format(ArrayList<AdapterResult> tables,
            Writer w) throws Exception {
        String komma = "";
        for (AdapterResult t : tables) {
            w.write(komma);
            komma = ",";
            format((AdapterTableResult) t, w);
        }
    }

    public String formatValue(Object o) {
        if (o != null) {
            if (o instanceof Double) {
                return ""+o;
            } else if (o instanceof Integer) {
                return ""+o;
            } else if (o instanceof java.sql.Date) {
                return (o == null ? "null" : formatDate(o)); 
            } else {
                return (o == null ? "null" : "\"" + o+ "\""); // TODO: Formatter ivm datum
            }
        }
        return null;
    }
}
