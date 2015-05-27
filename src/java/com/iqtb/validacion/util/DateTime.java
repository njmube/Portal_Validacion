package com.iqtb.validacion.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author danielromero
 */
public class DateTime {

    public static Date sumarRestarDiasFecha(Date fecha, int dias) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);

        calendar.add(Calendar.DAY_OF_YEAR, dias);

        return calendar.getTime();

    }

    public static Timestamp getTimestamp() {
        Date fReg = new Date();
        long fecha = fReg.getTime();
        Timestamp timestamp = new Timestamp(fecha);
        
        return timestamp;
    }

}
