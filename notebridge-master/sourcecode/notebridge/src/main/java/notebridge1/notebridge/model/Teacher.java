package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Teacher {

    private static final String UNKNOWN = "UNKNOWN";

    private int id;
    private String experience = UNKNOWN;
    private double avgRating = 0;
    private String zipcode = UNKNOWN;
    private String videoPath = UNKNOWN;


    public Teacher() {

    }

    public Teacher(int id, String experience, double avgRating, String zipcode, String videoPath) {
        if (zipcode == null) {
            zipcode = UNKNOWN;
        }
        if (experience == null) {
            experience = UNKNOWN;
        }
        if (videoPath == null) {
            videoPath = UNKNOWN;
        }

        this.id = id;
        this.experience = experience;
        this.avgRating = avgRating;
        this.zipcode = zipcode.toUpperCase().replaceAll("\\s", "");
        this.videoPath = videoPath;
    }

    public Teacher(String experience, double avgRating, String zipcode, String videoPath) {
        this.experience = experience;
        this.avgRating = avgRating;
        this.zipcode = zipcode;
        this.videoPath = videoPath;
    }

    public Teacher(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode.toUpperCase().replaceAll("\\s", "");
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        return id + " " + experience + " " + avgRating + " " + zipcode + " " + videoPath;
    }
}
