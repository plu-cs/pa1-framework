package rttr.dwo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoctorsWithoutOrdersParser {

    public static HospitalTestCase loadHospitalTestCase( Scanner scan ) {

        HospitalTestCase result = new HospitalTestCase();

        while(scan.hasNextLine()) {
            String line = scan.nextLine();

            line = line.trim();

            // Skip lines that are empty or begin with a '#'
            if( line.isEmpty() || line.startsWith("#") ) continue;

            String doctorHeader = "Doctor";
            String patientHeader = "Patient";

            if( line.startsWith(doctorHeader) || line.startsWith(patientHeader)) {
                String[] parts = line.split(":");
                if( parts.length != 2 ) {
                    throw new RuntimeException("Line should have only 2 parts.");
                }
                int hours = Integer.parseInt(parts[1].trim());
                String name = parts[0].trim();
                if( line.startsWith(doctorHeader) ) {
                    result.doctors.add( new Doctor(name, hours) );
                } else {
                    result.patients.add( new Patient(name, hours));
                }
            } else {
                throw new RuntimeException("Unrecognized line: " + line);
            }
        }

        return result;
    }

}
