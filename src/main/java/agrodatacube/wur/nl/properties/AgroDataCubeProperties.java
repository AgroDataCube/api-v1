/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.properties;

import agrodatacube.wur.nl.result.Paging;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author Rande001
 */
public class AgroDataCubeProperties {

    private static final String PAGE_SIZE_LIMIT = "paging.size_limit";

    private static final String DB_USERNAME = "db.username";        // Username to connect to the postgres db
    private static final String DB_PASSWORD = "db.password";       // Password to connect to the postgres db
    private static final String DB_HOST = "db.host";          // Host to connect to the postgres db
    private static final String DB_PORT = "db.port";                // Port to connect to the postgres db
    private static final String DB_MAX_CONNECTIONS = "db.max_connections";     // Port to connect to the postgres db
    private static final String DB_DATABASE = "db.database";     // Database to connect to the postgres db 
    private static final String MAIL_SENDER = "email.sender_address";
    private static final String MAIL_SMTPSERVER = "email.smtpserver";

    private static java.util.Properties props = null;

    /**
     * Retrieve a property from the properties map.
     *
     * @param key name of the key for which we want to retrieve the value
     * @param defaultValue the default value if the key is not present (requires
     * JDK >= 1.7)
     *
     * The file should reside on the tomcat folder (parent of webapps folder).
     *
     * @return
     */
    private static String getValue(String key,
            String defaultValue) {
        String[] filenames = new String[3];
        filenames[0] = "/var/lib/tomcat/webapps/agrodatacube.properties";           // Productie WENR
        filenames[1]= "/opt/tomcat/agrodatacube/webapps/agrodatacube.properties";   // Test site WENR
        filenames[2] = "agrodatacube.properties";                                   // Development WENR

        int i = 0;
        boolean ok = false;
        if (props == null) {
            while (i < filenames.length) {
                File file = new File(filenames[i]);
                try {
                    props = new java.util.Properties();
                    props.load(new FileInputStream(file));
                    System.out.println(String.format("Loaded properties from file %s", file.getAbsoluteFile()));
                    ok = true;
                    break;
                } catch (Exception e) {
                    System.out.println(String.format("Unable to open properties file %s, %s", file.getAbsoluteFile(), e.getMessage()));
                }
                i++;
            }
            if (! ok) {
                throw new RuntimeException(("Unable to open properties file !!!!!!!!"));
            }
        }

        return props.getProperty(key, defaultValue);
    }

    public static Integer getPageSizeLimit() {
        return Integer.parseInt(getValue(PAGE_SIZE_LIMIT, "" + Paging.DEFAULT_PAGE_LIMIT));
    }

    public static Integer getDBPort() {
        return Integer.parseInt(getValue(DB_PORT, "5432"));
    }

    public static Integer getDBMaxConnections() {
        return Integer.parseInt(getValue(DB_MAX_CONNECTIONS, "100"));
    }

    public static String getDBUsername() {
        return getValue(DB_USERNAME, "");
    }

    public static String getDBPassword() {
        return getValue(DB_PASSWORD, "");
    }

    public static String getDBHost() {
        return getValue(DB_HOST, "localhost");
    }

    public static String getDBDatabase() {
        return getValue(DB_DATABASE, "agrodatacube");
    }

    public static String getEmailSender() {
        return getValue(MAIL_SENDER, "");
    }

    public static String getSMTPServer() {
        return getValue(MAIL_SMTPSERVER, "");
    }
}
