package agrodatacube.wur.nl.token;
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
import agrodatacube.wur.nl.mail.SMTPClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import agrodatacube.wur.nl.db.AgroDataCubePostgresPool;
import agrodatacube.wur.nl.properties.AgroDataCubeProperties;
import java.util.Calendar;
import java.util.Date;

/**
 * Create or maintain a registration for this user, very simple initial version.
 * The registration information is stored in a table
 *
 * <PRE>
 * agrodatacube=> \d registration
 *                                       Table "public.registration"
 *      Column      |            Type             |                       Modifiers
 * -----------------+-----------------------------+--------------------------------------------------------
 *  id              | regclass                    | not null default nextval('registration_seq'::regclass)
 *  emailadres      | text                        | not null
 *  token           | text                        | not null
 *  expires         | timestamp without time zone |
 *  request_limit   | integer                     |
 *  area_limit      | double precision            |
 *  requests_issued | integer                     |
 *  area_fetched    | double precision            |
 *  issuedon        | timestamp with time zone    | default now()
 *
 * </pre>
 *
 * @author Rande001
 */
public class Registration {

    private String email;
    private static PreparedStatement ps_update_reg = null;  // Update token in registration table after usage
    private static PreparedStatement ps_log_req = null;     // Add row to request log

    public Registration(String email) {
        this.email = email;
    }

    /**
     * Free registration is available for everyone but has a limit of
     * AccessToken.FREE_MAX_AREA ha en AccessToken.FREE_MAX_REQUESTS requests.
     *
     * @return
     */
    public String saveFreeRegistration() {
        return saveRegistration(AccessToken.FREE_MAX_REQUESTS, AccessToken.FREE_MAX_AREA);
    }

    /**
     * Save the registration information in the database.
     *
     * @param requestLimit
     * @param araLimit
     * @return
     */
    private synchronized String saveRegistration(int requestLimit,
            double areaLimit) {
        Connection con = null;
        try {
            //
            // Get the token
            //

            String[] resources = new String[1];
            resources[0] = "*";
            AccessToken t = AccessTokenFactory.getInstance().createToken(resources, AccessToken.getNextJanuary1St(), requestLimit, areaLimit, email);

            //
            // Get connection and create sql statement.
            //
            con = AgroDataCubePostgresPool.getSingleton().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into registration (emailadres, token, expires, request_limit , area_limit, issuedon ) values (lower(?), ?,?,?,?, now())");
            ps.setString(1, email);
            ps.setString(2, t.toString());
            ps.setDate(3, new java.sql.Date(t.getExpireDate().getTime())); // util.date to sql.date
            ps.setInt(4, t.getRequestLimit());
            ps.setDouble(5, t.getAreaLimit());
            ps.execute();

            con.close();

            //
            // Now email the token 
            //
            SMTPClient s = new SMTPClient();
            s.setServer(AgroDataCubeProperties.getSMTPServer());
            s.setSenderAddress(AgroDataCubeProperties.getEmailSender());
            //s.setServer("your smtp server);
            s.addCCRecipient(AgroDataCubeProperties.getEmailSender()); // CC the sender.
            s.addRecipient(email);
            String message = "Access to AgroDataCube\n\n"
                    + "You have been granted access to the AgroDataCube using the token included in this mail. This token must be supplied as a http "
                    + "header parameter (name=token) in the HTTP(S) Get requests.\n\n"
                    + " Token\n" + t.toString()
                    + " \n\nIssued to            : " + t.getIssuedTo()
                    + " \nExpires                : " + t.getExpireDate()
                    + " \nRequest Limit (annual) : " + (t.getRequestLimit() == 0 ? "no limit" : t.getRequestLimit())
                    + " \nArea limit (annual)    : " + (t.getAreaLimit() > 0 ? t.getAreaLimit() : " no limit")
                    + "\n\nIf you need further information, please contact us at info.agrodatacube(at)wur.nl";

            s.sendMessage("Your request for access to AgroDataCube", message);
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception q) {
                ; // no action needed.
            }
            System.out.println("ERROR in registration "+e.getMessage());
            e.printStackTrace(System.out);
            
            return "Something when wrong while saving your registration. Please contact us at info.agrodatacube(at)wur.nl or look the the faq\'s at https://agrodatacube.wur.nl\nException : "+e.getMessage();
        }
        return "An email has been send to your emailadres informing you how you can access the data in the AgroDataCube.";
    }

    /**
     * Update usage information. Add the area and add one request.
     *
     * @param t
     * @param area
     * @param remoteIp
     */
    public static synchronized void updateUsageInformation(AccessToken t,
            double area, String remoteIp) {
        Connection con = null;
        int n = 0;
        try {
            con = AgroDataCubePostgresPool.getSingleton().getConnection();
            if (ps_update_reg == null) {
                ps_update_reg = con.prepareStatement("update registration set area_fetched = coalesce(area_fetched,0) + ?, requests_issued = coalesce(requests_issued,0) +1 where token = ?");
            }
            ps_update_reg.setDouble(1, area);
            ps_update_reg.setString(2, t.toString());
            n = ps_update_reg.executeUpdate();

            if (ps_log_req == null) {
                ps_log_req = con.prepareStatement("insert into request_log values (now(), ?,?)");

            }
            ps_log_req.setDouble(2, area);
            ps_log_req.setString(1, remoteIp);
            n = ps_log_req.executeUpdate();
            con.close();
            ps_update_reg.close();
            ps_update_reg = null;
            ps_log_req.close();
            ps_log_req = null;
        } catch (Exception e) {
            try {
                ps_update_reg = null;
                con.close();
            } catch (Exception q) {
                ;// No action needed 
            };
            throw new RuntimeException("ERROR Updating usage information in REGISTRATION table : " + e.getMessage());
        }

        //
        // If n = 0 then the token was not known in the registration table so fake token or IP based ? These are only available until may 1st 2018.
        //
        if (n == 0) {
            if (t.getIssuedTo().indexOf("@") > 0) {
                throw new RuntimeException("This token was not found in the token registration !!!!!!!!!");
            } else {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.MILLISECOND, 0);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, 4); // jAN == 0
                c.set(Calendar.YEAR, 2018);
                if (c.getTime().before(new Date(System.currentTimeMillis()))) {
                    throw new RuntimeException("This token was not found in the token registration, ip based tokens no longer supported after May 1st 2018 !!!!!!!!!");
                }
            }
        }
    }

    /**
     * Check if this token is known and valid. Perhaps improve this and first
     * see in cache. If not in cache see in db and if found add to cache.
     *
     * @param token
     * @return
     */
    public synchronized static boolean tokenIsKnown(String token) {
        Connection con = null;
        PreparedStatement ps_select = null;  // select token in registration before usage (CACHE ???)
        boolean result = false;
        try {
            //
            // Get connection and create sql statement.
            //
            con = AgroDataCubePostgresPool.getSingleton().getConnection();
            ps_select = con.prepareStatement("select 1 from registration where token = ? and coalesce(request_limit,0) > coalesce(requests_issued,0) ");
            // disabled and coalesce(area_limit,0) > coalesce(area_fetched,0)
            ps_select.setString(1, token);
            ResultSet rs = ps_select.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
            ps_select.close();
            con.close();

        } catch (Exception e) {
            result = false;
            try {
                con.close();
            } catch (Exception q) {;
                throw new RuntimeException("Token not registered or usage exceeded limits ! Please contact info.agrodatacube(at)wur.nl\nException detected was " + e.getMessage());
            }
        }

        if (!result) {
            throw new RuntimeException("Token not registered or usage exceeded limits ! Please contact info.agrodatacube(at)wur.nl");
        }
        
        return result;
    }
}
