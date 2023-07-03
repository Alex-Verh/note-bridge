package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;

@XmlRootElement
public class User {

    private static final String UNKNOWN = "UNKNOWN";

    // mandatory
    private int id;
    private String email;
    private String password;
    private String fullName;

    // optional variables with default values
    private String country = UNKNOWN;
    private String city = UNKNOWN;
    private String pfpPath = UNKNOWN;
    private String description = UNKNOWN;
    private boolean online = false;


    // testing purposes only
    public User(){}

    // overloading constructors
    public User(int id, String email, String password, String fullName) {
        this.id = id;
        this.email = email.toLowerCase();
        this.password = password;
        this.fullName = fullName;
    }

    public  User(String email, String password, String fullName) {
        this.email = email.toLowerCase();
        this.password = password;
        this.fullName = fullName;
    }

    public User(int id, String email, String password, String fullName, String country, String city) {
        this.id = id;
        this.email = email.toLowerCase();
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
    }

    public User(String email, String password, String fullName, String country, String city) {
        this.email = email.toLowerCase();
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
    }

    public User(int id, String email, String password, String fullName, String country, String city, String pfpPath, String description, boolean online) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
        this.pfpPath = pfpPath;
        this.description = description;
        this.online = online;
    }

    public User(String email, String password, String fullName, String country, String city, String pfpPath, String description, boolean online) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
        this.pfpPath = pfpPath;
        this.description = description;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPfpPath(String path) {
        this.pfpPath = path;
    }

    public String getPfpPath() {
        return pfpPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
