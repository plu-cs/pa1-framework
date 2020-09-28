package rttr.dwo;

public class Doctor {
    private String name;
    private int hoursFree;
    public Doctor( String n, int h ) {
        name = n;
        hoursFree = h;
    }
    public int getHoursFree() { return hoursFree; }
    public String getName() { return name; }
}
