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

import agrodatacube.wur.nl.properties.AgroDataCubeProperties;
import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.ds.PGPoolingDataSource;

/**
 * Create a class that implements the methods in this interface and add to the
 * war.
 *
 * @author Rande001
 */
public class AgroDataCubePostgresPool {

    private static PGPoolingDataSource pool = null;
    private static AgroDataCubePostgresPool instance = null;

    private AgroDataCubePostgresPool() {
        pool = new PGPoolingDataSource();
        try {
            pool.setDatabaseName(AgroDataCubeProperties.getDBDatabase());
            pool.setUser(AgroDataCubeProperties.getDBUsername());
            pool.setServerName(AgroDataCubeProperties.getDBHost());
            pool.setPassword(AgroDataCubeProperties.getDBPassword());
            pool.setPortNumber(AgroDataCubeProperties.getDBPort());
            pool.setMaxConnections(AgroDataCubeProperties.getDBMaxConnections());
        } catch (Exception e) {
            pool = null;
            throw new RuntimeException("Error connecting to db : ".concat(e.getMessage()));
        }
    }

    public synchronized static AgroDataCubePostgresPool getSingleton() {

        if (instance == null) {
            instance = new AgroDataCubePostgresPool();
        }
        return instance;
    }
    
    public synchronized Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to get db connection from pool ("+ex.getMessage()+")");
        }
    }
}
