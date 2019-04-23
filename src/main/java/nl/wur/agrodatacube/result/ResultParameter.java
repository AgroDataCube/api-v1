/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wur.agrodatacube.result;

/**
 *
 * @author rande001
 */
public class ResultParameter {
    
    private static final String[] knownResultParameters = { "result","page_size","page_offset","output_epsg" };
    
    public static boolean isResultParameter(String name ) {
        for (String s : knownResultParameters) {
            if (s.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
