/*
* Copyright 2018 Wageningen Environmental Research
*
* For licensing information read the included LICENSE.txt file.
*
* Unless required by applicable law or agreed to in writing, this software
* is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
* ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.result;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Yke
 */
public class AdapterTableResult extends AdapterResult {

    private String[] columnNames;
    private ArrayList<ArrayList<Object>> columnValues;
    private int geomColumn = -1;
    private int areaColumn = -1;
    private Double area;

    public AdapterTableResult() {
        super();
        area = 0.;
        columnValues = new ArrayList<>();
    }

    public AdapterTableResult(String[] columnNames) {
        this();
        this.columnNames = columnNames;
        for (int i = 0; i < columnNames.length; i++) {
            if ("geom".equalsIgnoreCase(columnNames[i])) {
                geomColumn = i;
                break;
            }
        }

        for (int i = 0; i < columnNames.length; i++) {
            if ("area".equalsIgnoreCase(columnNames[i])) {
                areaColumn = i;
                break;
            }
        }
    }

    @Override
    protected void clear() {
        columnNames = new String[1];
        columnValues = new ArrayList<>();
    }

//    public void setColumnsNames(String[] names) {
//        columnNames = names;
//    }
    public void addRow(Object[] values) {
        addRow(new ArrayList<>(Arrays.asList(values)));
    }

    public void addRow(ArrayList<Object> values) {
        columnValues.add(values);
    }

    /*
    * Build an AdapterTableResult from a sql resultset.
     */
    public static AdapterTableResult fromSQLResulSet(ResultSet rs) {
        AdapterTableResult result = new AdapterTableResult();
        try {

            // set the column names
            String[] columnNames = new String[rs.getMetaData().getColumnCount()];
            int nColumns = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= nColumns; i++) {
                columnNames[i - 1] = rs.getMetaData().getColumnLabel(i);
            }
            result = new AdapterTableResult(columnNames);

            // add the values to a table (no paging yet)
            while (rs.next()) {
                ArrayList<Object> row = new ArrayList<>();
                if (result.areaColumn >= 0) {
                    result.area += (Double) rs.getObject(result.areaColumn+1); // rs index is 1 based not 0 based.
                }
                for (int i = 1; i <= nColumns; i++) {
                    row.add(rs.getObject(i));
                }
                result.columnValues.add(row);
            }
            rs.getStatement().close();
            rs.close(); // Niet bij pagination
        } catch (Exception e) {
            result.setStatus(e.getMessage());
        }
        return result;
    }

    public String getColumnName(int i) {
        return columnNames[i];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public ArrayList<Object> getRow(int i) {
        if (i >= columnValues.size()) {
            return null;
        }
        return columnValues.get(i);
    }

    
//    public void addRow(ResultSet rs) {
//
//    }
//
    public int getGeomIndex() {
        return (geomColumn);
    }

    @Override
    public Double getArea() {
        return area;
    }

    public int getRowCount() {
        return columnValues.size();
    }
}