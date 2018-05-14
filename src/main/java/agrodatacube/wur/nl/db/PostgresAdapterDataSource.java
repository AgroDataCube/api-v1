/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.db;

import agrodatacube.wur.nl.result.AdapterTableResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Yke
 */
public class PostgresAdapterDataSource {

    private Connection connection = null;
    private static AgroDataCubePostgresPool pool = null;

    static {
          pool = AgroDataCubePostgresPool.getSingleton();
    }

    public PostgresAdapterDataSource() {
    }

    /**
     *
     * @param queryString
     * @param params
     * @return
     * @throws Exception
     */
    public AdapterTableResult executeQuery(String queryString,
            List<Object> params) throws Exception {
        connection = getConnection();
        AdapterTableResult result;
        try (PreparedStatement ps = connection.prepareStatement(queryString, ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY)) {
            ps.setFetchSize(1000);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            result = AdapterTableResult.fromSQLResulSet(ps.executeQuery());
            result.setQueryString(queryString);
        } finally {
            connection.close();
        }
        return result;
    }

    /**
     * Used to create rasters. TODO finish
     *
     * @param queryString
     * @return
     */
    public byte[] executeQuery(String queryString) throws Exception {
        connection = getConnection();
        byte[] result = new byte[0];
        try (PreparedStatement ps = connection.prepareStatement(queryString, ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getBytes(1);
            }
            rs.close();
            ps.close();
        } finally {
            connection.close();
        }
        return result;
    }

    /**
     * For analytics predefined queries are used.
     *
     * @param name
     * @return
     */
    public String getPredefinedQuery(String name) {
        String result = "";
        try {
            connection = getConnection();
            try (PreparedStatement ps = connection.prepareStatement("select query from config where lower(code) = lower(?)")) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    result = rs.getString(1);
                }
                rs.close();
            }
            connection.close();
            if (result.length() == 0) {
                throw new RuntimeException("predefined query\"" + name.toLowerCase() + "\" not found in config tabel");
            }
        } catch (Exception e) {
            try {
                connection.close();
            } catch (SQLException ee) {;
            }
            throw new RuntimeException("PostgresAdapterDataSource.getPredefinedQuery : " + e.getMessage());
        }
        return result;
    }

    private Connection getConnection() throws Exception {
        return pool.getConnection();
    }
}
