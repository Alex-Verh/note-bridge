package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Lesson {
    private int id;
    private int teacherId;
    private double price;
    private int instrumentId;
    private int skillId;
    private String description;
    private String title;

    private String instrumentName = "Default Instrument";

    public Lesson() {
    }

    public Lesson(int id, int teacherId, double price, int instrumentId, int skillId, String description, String title, String instrumentName) {
        this.id = id;
        this.teacherId = teacherId;
        this.price = price;
        this.instrumentId = instrumentId;
        this.skillId = skillId;
        this.description = description;
        this.title = title;
        this.instrumentName = instrumentName;
    }

    public Lesson(int teacherId, double price, int instrumentId, int skillId, String description, String title, String instrumentName) {
        this.teacherId = teacherId;
        this.price = price;
        this.instrumentId = instrumentId;
        this.skillId = skillId;
        this.description = description;
        this.title = title;
        this.instrumentName = instrumentName;
    }

    public Lesson(int id, int teacherId, double price, int instrumentId, int skillId, String description, String title) {
        this.id = id;
        this.teacherId = teacherId;
        this.price = price;
        this.instrumentId = instrumentId;
        this.skillId = skillId;
        this.description = description;
        this.title = title;
    }

    public Lesson(int teacherId, double price, int instrumentId, int skillId, String description, String title) {
        this.teacherId = teacherId;
        this.price = price;
        this.instrumentId = instrumentId;
        this.skillId = skillId;
        this.description = description;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
