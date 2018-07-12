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

import agrodatacube.wur.nl.properties.AgroDataCubeProperties;

/**
 *
 * @author rande001
 */
public class Paging {

    private static Paging nopaging;
    public static int MAX_PAGE_LIMIT = 1000; // Properties
    public static int DEFAULT_PAGE_LIMIT = 50; // Properties

    private int size = DEFAULT_PAGE_LIMIT;
    private int offset = 0;
    private boolean executeCount;
    private boolean allData;
    
    public Paging() {
       DEFAULT_PAGE_LIMIT = AgroDataCubeProperties.getPageSizeLimit();
       size  = DEFAULT_PAGE_LIMIT;
    }

    
    public synchronized static Paging NoPaging() {
        if (nopaging == null) {
            nopaging = new Paging();
            nopaging.setSize(0);
        }
        return nopaging;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size <= MAX_PAGE_LIMIT & size > 0) {
            this.size = size;
        }
    }

    /**
     * Offset is set as a pagenumber but Postgres needs it as a row number.
     * 
     * @return 
     */
    public int getOffset() {
        return offset*size; // Tricky size can change between requests.
    }

    public void setOffset(int offset) {
        if (offset >= 0) {
            this.offset = offset;
        } else {
            this.offset = 0;
        }
    }

    public void set(String name, String value) {
        if (name.equalsIgnoreCase("page_size")) {
            setSize(Integer.parseInt(value));
        } else if (name.equalsIgnoreCase("page_offset")) {
            setOffset(Integer.parseInt(value));
        } else {
            throw new RuntimeException("Invalid paging parameter \"" + name + "\" !!!");
        }
    }

    public boolean doExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(boolean executeCount) {
        this.executeCount = executeCount;
    }

    public boolean getAllData() {
        return allData;
    }

    public void setAllData(boolean allData) {
        this.allData = allData;
    }
}
