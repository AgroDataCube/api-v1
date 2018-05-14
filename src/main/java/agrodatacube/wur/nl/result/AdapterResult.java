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

import java.util.Properties;

/**
 * * * @author Yke
 */
public abstract class AdapterResult {

    private String queryStrng;
    private java.util.Properties props;

    public AdapterResult() {
        status = "ok";
        props = new java.util.Properties();
    }
    private String status; // Ok of errormessage     

    public String getStatus() {
        return status;
    }

    public boolean didSucceed() {
        return "ok".equalsIgnoreCase(status);
    }

    public void setStatus(String status) {
        this.status = status;
        if (!didSucceed()) {
            clear();
        }
    }

    protected abstract void clear();

    public String getQueryString() {
        return queryStrng;
    }

    public void setQueryString(String queryStrng) {
        this.queryStrng = queryStrng;
    }

    public void addProperty(String name,
            String value) {
        if (value == null) {
            props.put(name, "unknown");
        } else {
            props.put(name, value);
        }
    }

    public Properties getProps() {
        return props;
    }

    public abstract Double getArea();
}
