/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 */
package nl.wur.agrodatacube.resource;

/**
 *
 * @author rande001
 */
public class AdapterQueryResource extends AdapterPostgresResource {
    
    private String query;
    
    public AdapterQueryResource(String name) {
        super(name);
    }

    public void setQuery(String query) {
        this.query = query;
    }
        
    @Override
    public String getBaseQuery() {
        return query;
    }
}
