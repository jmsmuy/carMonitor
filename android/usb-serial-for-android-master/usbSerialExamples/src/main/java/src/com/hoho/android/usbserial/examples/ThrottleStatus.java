package src.com.hoho.android.usbserial.examples;

/**
 * Created by jmsmuy on 01/01/16.
 */
public enum ThrottleStatus {
    IDLE("IDLE", "Idling", 1), WOT("WOT", "Wide open throttle", 3), PARTIAL("PARTIAL", "Partially opened throttle", 2);

    private String dataReceived;
    private String printableName;
    private int id;

    ThrottleStatus(String dataReceived, String printableName, int id) {
        this.dataReceived = dataReceived;
        this.printableName = printableName;
        this.id = id;
    }

    public String getDataReceived() {
        return dataReceived;
    }

    public String getPrintableName() {
        return printableName;
    }

    public static ThrottleStatus toValue(String dataReceived) {
        for (ThrottleStatus status : ThrottleStatus.values()) {
            if (status.getDataReceived().trim().equals(dataReceived.trim())) {
                return status;
            }
            if (status.getId() == Integer.valueOf(dataReceived.trim())) {
                return status;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return printableName;
    }

}
