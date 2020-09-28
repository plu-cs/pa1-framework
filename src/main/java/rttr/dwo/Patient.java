package rttr.dwo;

public class Patient {
    private String name;
    private int hoursNeeded;

    public Patient( String n, int h) {
        name = n;
        hoursNeeded = h;
    }

    public String getName() { return name; }
    public int getHoursNeeded() { return hoursNeeded; }
}
