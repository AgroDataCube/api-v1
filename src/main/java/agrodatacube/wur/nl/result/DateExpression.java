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

/**
 *
 * @author rande001
 */
public class DateExpression {
 
    public final static Integer DateTypeDate = 1;
    public final static Integer DateTypeDay = 2;
    
    /**
     * Create a valid 
     * @param fieldName
     * @param theDate any date in forma yyyy , yyyymm, yyyymmdd
     * @return
     */
    public static String create(String fieldName,Integer dateType, String operator,  Integer theDate) {
        if (dateType == DateTypeDate) {
            return createDateExpression(fieldName, operator, theDate);
        }
        return " 1 =2";
    }

    private static String createDateExpression(String fieldName, String operator, Integer theDate) {
        String dateFormat = "yyyymmdd";
        switch (theDate.toString().length()) {
            case 4 : dateFormat = "yyyy"; break;
            case 6 : dateFormat = "yyyymm"; break;
            case 8 : dateFormat = "yyyymmdd"; break;               
        }
        return "cast (to_char("+fieldName+",'"+dateFormat+"') as integer)" +operator+ " " +theDate;
            
    }
}
