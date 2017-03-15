package src.com.hoho.android.usbserial.examples;

import android.util.Log;

import com.hoho.android.usbserial.examples.SerialConsoleActivity;

import java.awt.font.TextAttribute;

/**
 * Created by jmsmuy on 01/01/16.
 */
public class DtoDataMessage {

    // engine data
    private int rpm;
    private float bank1;
    private float bank2;
    private float afm;
    private float cts;
    private float ait;
    private float o2;
    private float volt;

    // gps data
    private float speedInKph;
    private float height;
    private float flat;
    private float flon;

    private ThrottleStatus throttleStatus;

    private static final String TAG = DtoDataMessage.class.getSimpleName();


    public static DtoDataMessage parseReceivedValues(String received, SerialConsoleActivity serialConsoleActivity) {
        DtoDataMessage toReturn = null;

        if (valid(received)) {
            toReturn = new DtoDataMessage();
            // empezamos a parsear la entrada entonces
            try {
                toReturn.setRpm(Integer.valueOf(getVal(0, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor RPM: " + getVal(0, received));
            }
            try {
                toReturn.setBank1(Float.valueOf(getVal(1, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor BNK1: " + getVal(1, received));
            }
            try {
                toReturn.setBank2(Float.valueOf(getVal(2, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor BNK2: " + getVal(2, received));
            }
            try {
                toReturn.setSpeedInKph(Float.valueOf(getVal(3, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor KPH: " + getVal(3, received));
            }
            try {
                toReturn.setHeight(Float.valueOf(getVal(4, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor HGHT: " + getVal(4, received));
            }
            try {
                toReturn.setFlat(Float.valueOf(getVal(5, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor FLAT: " + getVal(5, received));
            }
            try {
                toReturn.setFlon(Float.valueOf(getVal(6, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor FLON: " + getVal(6, received));
            }
            try {
                toReturn.setAfm(Float.valueOf(getVal(7, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor AFM: " + getVal(7, received));
            }
            try {
                toReturn.setCts(Float.valueOf(getVal(8, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor CTS: " + getVal(8, received));
            }
            try {
                toReturn.setAit(Float.valueOf(getVal(9, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor AIT: " + getVal(9, received));
            }
            try {
                toReturn.setO2(Float.valueOf(getVal(10, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor O2: " + getVal(10, received));
            }
            try {
                toReturn.setVolt(Float.valueOf(getVal(11, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor Volt: " + getVal(11, received));
            }
            try {
                toReturn.setThrottleStatus(ThrottleStatus.toValue(getVal(12, received)));
            } catch (Exception e) {
                Log.e(TAG, "Error en lectura de valor Throttle: " + getVal(12, received));
            }
        } else {
            Log.e(TAG, "Dto invalido: " + received);
        }
        return toReturn;
    }

    /**
     * this method returns the i position value in the string received
     * returns -1 if invalid data is passed (so as not to crash the app)
     *
     * @param i
     * @param received
     * @return
     */
    private static String getVal(int i, String received) {
        String[] splittedString = received.split(";");
        if (splittedString.length > i) {
            return splittedString[i];
        }
        return "-1";
    }

    /**
     * this method returns true if received has the format VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL;VAL
     *
     * @param received
     * @return
     */
    private static boolean valid(String received) {
        int cantSemiColons = 0;
        boolean lastCharWasSemiColon = true;
        for (String c : received.split("")) {
            if (!c.equals("")) {
                // Ahora discrimino si es dato o ";"
                if (c.equals(";")) {
                    cantSemiColons++;
                    if (lastCharWasSemiColon) {
                        return false;
                    }
                    lastCharWasSemiColon = true;
                } else {
                    // es dato, por tanto reseteo la flag lastCharWasSemiColon
                    lastCharWasSemiColon = false;
                }
            }
        }
        // verifico que el mensaje haya terminado con 12 semiColons y no termine en semicolon
        return !(lastCharWasSemiColon || cantSemiColons != 12);
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public float getBank1() {
        return bank1;
    }

    public void setBank1(float bank1) {
        this.bank1 = bank1;
    }

    public float getBank2() {
        return bank2;
    }

    public void setBank2(float bank2) {
        this.bank2 = bank2;
    }

    public float getAfm() {
        return afm;
    }

    public void setAfm(float afm) {
        this.afm = afm;
    }

    public float getCts() {
        return cts;
    }

    public void setCts(float cts) {
        this.cts = cts;
    }

    public float getAit() {
        return ait;
    }

    public void setAit(float ait) {
        this.ait = ait;
    }

    public float getO2() {
        return o2;
    }

    public void setO2(float o2) {
        this.o2 = o2;
    }

    public float getSpeedInKph() {
        return speedInKph;
    }

    public void setSpeedInKph(float speedInKph) {
        this.speedInKph = speedInKph;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getFlat() {
        return flat;
    }

    public void setFlat(float flat) {
        this.flat = flat;
    }

    public float getFlon() {
        return flon;
    }

    public void setFlon(float flon) {
        this.flon = flon;
    }

    public void setVolt(Float volt) {
        this.volt = volt;
    }

    public float getVolt() {
        return volt;
    }

    public ThrottleStatus getThrottleStatus() {
        return throttleStatus;
    }

    public void setThrottleStatus(ThrottleStatus throttleStatus) {
        this.throttleStatus = throttleStatus;
    }

    @Override
    public String toString() {
        return "\nDtoDataMessage{" +
                "rpm=" + rpm +
                ", bank1=" + bank1 +
                ", bank2=" + bank2 +
                "\n, afm=" + afm +
                ", cts=" + cts +
                ", ait=" + ait +
                "\n, o2=" + o2 +
                ", volt=" + volt +
                ", speedInKph=" + speedInKph +
                "\n, height=" + height +
                ", flat=" + flat +
                ", flon=" + flon +
                "\n, throttleStatus=" + throttleStatus +
                '}';
    }

}
