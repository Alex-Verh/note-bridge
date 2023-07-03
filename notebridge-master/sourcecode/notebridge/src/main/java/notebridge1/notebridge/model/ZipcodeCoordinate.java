package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ZipcodeCoordinate {

    private String zipcode;
    private double latitude;
    private double longitude;

    public ZipcodeCoordinate() {}

    public ZipcodeCoordinate(String zipcode, double latitude, double longitude) {
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
