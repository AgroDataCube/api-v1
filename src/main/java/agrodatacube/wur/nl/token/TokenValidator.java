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
package agrodatacube.wur.nl.token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author rande001
 */
public class TokenValidator {

    //
    // Alles static zodat het session onafhankelijk is.
    //    
    private static Calendar lastDate = null;
    private static final Map<String, Integer> calls = new HashMap<>();
    private static final Map<String, AccessToken> clients = new HashMap<>();

    /**
     * Return if the supplied String is a valid AgroDataCube token.
     *
     * @param tokenString
     * @return
     */
    public static boolean tokenIsValid(String tokenString) {
        try {
            AccessToken t = AccessTokenFactory.getInstance().decodeToken(tokenString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate if token allows access to the resource (identified by a path).
     *
     * @param t
     * @param path
     * @return
     */
    public static TokenValidationResult tokenAllowsAccess(AccessToken t,
            String path) {

        if (t.isExpired()) {
            return new TokenValidationResult("This token is expired, please request a new token !");
        }

        if (!t.allowsAccessTo(path)) {
            return new TokenValidationResult("This token does not allow access to the requested resource(s)");
        }

//        if (t.hasRequestLimit()) {
//            return checkLimits(t);
//        }

        return new TokenValidationResult("");
    }

    /**
     * See if two calendars have the same date (ignore time) part.
     *
     * @param c1
     * @param c2
     * @return
     */
    private static boolean sameDay(Calendar c1,
            Calendar c2) {
        if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) {
            return false;
        }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

//    /**
//     * Check if this tokens has not exceeded the number of requests allowed.
//     * When token.limit = <0 then no limits.
//     *
//     * @param t
//     * @return
//     */
//    private synchronized static TokenValidationResult checkLimits(AccessToken t) {
//        if (lastDate == null) {
//            lastDate = toDay();
//        }
//
//        //
//        // See if current date == lastDate if so check limit, if not clear Map. (reset)
//        //
//        if (sameDay(toDay(), lastDate)) {
//            if (calls.containsKey(t.toString())) {
//                int n = calls.get(t.toString());
//                calls.put(t.toString(), n + 1);
//            } else {
//                calls.put(t.toString(), 1);
//            }
//        } else {
//            lastDate = toDay();
//            calls.clear();
//            calls.put(t.toString(), 1);
//        }
//
//        if (calls.get(t.toString()) > t.getRequestLimit()) {
//            return new TokenValidationResult(String.format("This token (issued to %s) has reached its daily limit of (%d) requests !!!", t.getIssuedTo(), t.getRequestLimit()));
//        }
//        return new TokenValidationResult("");
//    }
//
//    /**
//     * Return a date which is noon of the date.
//     *
//     * @return
//     */
//    private static Calendar toDay() {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(System.currentTimeMillis());
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DATE);
//        c = Calendar.getInstance();
//        c.set(year, month, day, 0, 0, 0);
//        return c;
//    }

//    /**
//     * Remove all tokens so everybody can continue.
//     */
//    public static void reset() {
//        synchronized (clients) {
//            clients.clear();
//        }
//    }
}
