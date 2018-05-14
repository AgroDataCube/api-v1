/*
 * Copyright 2018 Wageningen Environmental Research
 *
 * For licensing information read the included LICENSE.txt file.
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 */
package agrodatacube.wur.nl.exec;

/**
 *
 * @author rande001
 */
public class ExecutorResult { // split into two classes one for geojson on for image.
    
    byte[] image;
    String result;
    Double area;

    public ExecutorResult(String result,
            Double area) {
        this.result = result;
        this.area = area;
    }

    /**
     * This the result for raster queries.
     * 
     * @param result
     * @param area 
     */
    public ExecutorResult(byte[] result,
            Double area) {
        this.image = result;
        this.area = area;
    }

    public String getResult() {
        return result;
    }

    public Double getArea() {
        return area;
    }
    
    public byte[] getImage() {
        return image;
    }
}
