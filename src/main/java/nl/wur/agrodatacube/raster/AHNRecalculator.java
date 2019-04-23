/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wur.agrodatacube.raster;

/**
 *
 * @author rande001
 */
public class AHNRecalculator extends Recalculator {

    /**
     * Since the AHN raster already fp no action is needed.
     * 
     * @param b
     * @return 
     */
    @Override
    protected float calculate(byte b) {
        throw new RuntimeException("This raster needs no recalculation so this method should not be called");
    }
    
}
