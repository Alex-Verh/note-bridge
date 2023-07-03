package notebridge1.notebridge.model;


import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TeacherInstruments {

    private int teacherId;
    private int instrumentId;

    private TeacherInstruments(){


    }

    public TeacherInstruments(int teacherId, int instrumentId) {
        this.teacherId = teacherId;
        this.instrumentId = instrumentId;
    }


    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }
}
