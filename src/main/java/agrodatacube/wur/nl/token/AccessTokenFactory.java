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

import agrodatacube.wur.nl.token.sample.AccessTokenFactorySampleImplementation;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Rande001
 */
public abstract class AccessTokenFactory {

    /*
     Create using injection or ...
     */
    private static AccessTokenFactory instance = null;

    /**
     * Return a singleton for the TokenFactory to be used. You can use
     * AccessTokenFactorySampleImplementation.getInstance() or create your own.
     *
     * @return
     */
    public static AccessTokenFactory getInstance() {
        if (instance == null) {
            instance = AccessTokenFactorySampleImplementation.getInstance(); // 
        }
        return instance;
    }

    /**
     * Create a token based on the string supplied by the user (provided it in
     * the http header probably).
     *
     * @param tokenAsString
     * @return
     */
    public abstract AccessToken decodeToken(String tokenAsString);

    public abstract AccessToken createToken(String[] resources,
            Date expireDate,
            int requestLimit,
            double areaLimit,
            String issuedTo) throws Exception;

    public abstract AccessToken createTokenForIp(String ip) throws Exception;

    protected static Date getNextJanuary1St() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR) + 1, 0, 1, 0, 0, 0); // Next 1st january
        return new Date(c.getTimeInMillis());

    }

    /**
     * Return the data of tomorrow late at night so token stops 1 ms before
     * tomorrow ends.
     *
     * @return
     */
    protected static Date toMorrow() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_WEEK) + 1, 23, 59, 59);
        return new Date(c.getTimeInMillis());
    }

}
