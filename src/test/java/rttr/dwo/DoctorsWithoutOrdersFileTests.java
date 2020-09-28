package rttr.dwo;

import org.junit.Test;

import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class DoctorsWithoutOrdersFileTests {

    private static class DWOTestCase {
        public List<Doctor> doctors;
        public List<Patient> patients;
        public boolean result;

        public DWOTestCase(HospitalTestCase tc) {
            doctors = tc.doctors;
            patients = tc.patients;
            result = false;
        }
    }

    // One patient can be seen by one doctor
    @Test
    public void onePatientOneDoctor() {
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = List.of(
                new Doctor( "Dr. A", 4 )
        );
        List<Patient> patients = List.of(
                new Patient( "A", 4 )
        );

        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);

        assertTrue(result);
        assertEquals(1, schedule.size());
        assertTrue(schedule.containsKey("Dr. A"));
        assertEquals(1, schedule.get("Dr. A").size());
        assertTrue(schedule.get("Dr. A").contains(patients.get(0).getName()));

        docs = List.of(
                new Doctor( "Dr. A", 5 )
        );
        schedule.clear();

        docsMap = docVectorToMap(docs);

        result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertTrue(result);
        assertEquals(1, schedule.size());
        assertTrue(schedule.containsKey("Dr. A"));
        assertEquals(1, schedule.get("Dr. A").size());
        assertTrue(schedule.get("Dr. A").contains( patients.get(0).getName()));
    }

    private Map<String, Integer> patientVectorToMap(List<Patient> patients) {
        Map<String, Integer> result = new HashMap<>();
        for( Patient p : patients) {
            result.put(p.getName(), p.getHoursNeeded());
        }
        return result;
    }

    private Map<String, Integer> docVectorToMap(List<Doctor> docs) {
        Map<String, Integer> returnValue = new HashMap<>();
        for (Doctor doctor: docs) {
            returnValue.put(doctor.getName(), doctor.getHoursFree());
        }
        return returnValue;
    }

    // Doesn't assign patient to doctor that doesn't have enough hours
    @Test
    public void notEnoughHours() {
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = List.of(
                new Doctor( "Dr. A", 3 )
        );
        List<Patient> patients = List.of(
                new Patient( "A", 4 )
        );
        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertFalse(result);
        assertTrue(schedule.size() == 0 || schedule.size() == 1);
        assertTrue(schedule.size() == 0 ||
                (schedule.containsKey("Dr. A") && schedule.get("Dr. A").isEmpty()));
    }

    // Assigns two patients to the same doctor
    @Test
    public void twoPatientsSameDoctor() {
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = List.of(
                new Doctor( "Dr. A", 8 )
        );
        List<Patient> patients = List.of(
                new Patient( "A", 5 ),
                new Patient( "B", 3 )
        );
        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertTrue(result);
        assertEquals(1, schedule.size());
        assertTrue(schedule.containsKey("Dr. A"));
        assertEquals(2, schedule.get("Dr. A").size());
        assertTrue(schedule.get("Dr. A").contains(patients.get(0).getName()));
        assertTrue(schedule.get("Dr. A").contains(patients.get(1).getName()));
    }

    // Returns false if schedule isn't possible
    @Test
    public void impossibleSchedule() {
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = new ArrayList<>();
        docs.add(new Doctor( "Dr. A", 8 ));
        List<Patient> patients = new ArrayList<>();
        patients.add( new Patient("A", 5 ));
        patients.add( new Patient( "B", 4 ));

        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertFalse(result);
        docs.add(new Doctor("Dr. B", 5 ));
        patients.add(new Patient( "C", 8 ));
        schedule.clear();
        docsMap = docVectorToMap(docs);
        patientsMap = patientVectorToMap(patients);

        result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertFalse(result);
    }

    // Doesn't do greedy solution part 1
    @Test
    public void dontBeGreedy1() {
        // Patient needing most time to doc with most hours
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = List.of(
                new Doctor("Dr. A", 10),
                new Doctor("Dr. B", 8)
        );
        List<Patient> patients = List.of(
                new Patient( "A", 8),
                new Patient("B", 4 ),
                new Patient("C", 6 )
        );
        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertTrue(result);
        assertEquals(2, schedule.size());
        assertTrue(schedule.containsKey("Dr. A"));
        assertTrue(schedule.containsKey("Dr. B"));
        assertEquals(2, schedule.get("Dr. A").size());
        assertTrue(schedule.get("Dr. A").contains(patients.get(1).getName()));
        assertTrue(schedule.get("Dr. A").contains(patients.get(2).getName()));
        assertEquals(1, schedule.get("Dr. B").size());
        assertTrue(schedule.get("Dr. B").contains(patients.get(0).getName()));
    }

    // Doesn't do greedy solution part 2
    @Test
    public void dontBeGreedy2() {
        // Patient needing most time to doc with most hours
        Map<String, Set<String>> schedule = new HashMap<>();

        List<Doctor> docs = List.of(
                new Doctor( "Dr. A", 5 ),
                new Doctor( "Dr. B", 12 )
        );
        List<Patient> patients = List.of(
                new Patient( "A", 8 ),
                new Patient( "B", 4 ),
                new Patient( "C", 5 )
        );
        Map<String, Integer> docsMap = docVectorToMap(docs);
        Map<String, Integer> patientsMap = patientVectorToMap(patients);

        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);
        assertTrue(result);
        assertEquals(2, schedule.size());
        assertTrue(schedule.containsKey("Dr. A"));
        assertTrue(schedule.containsKey("Dr. B"));
        assertEquals(2, schedule.get("Dr. B").size());
        assertTrue(schedule.get("Dr. B").contains(patients.get(0).getName()));
        assertTrue(schedule.get("Dr. B").contains(patients.get(1).getName()));
        assertEquals(1, schedule.get("Dr. A").size());
        assertTrue(schedule.get("Dr. A").contains(patients.get(2).getName()));
    }

    // ComplexNo.dwo
    @Test
    public void complexNo() throws Exception  {
        String file ="ComplexNo.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // SimpleNo.dwo
    @Test
    public void simpleNo() throws Exception {
        String file ="SimpleNo.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // HandoutExampleNo.dwo
    @Test
    public void handoutExampleNo()  throws Exception {
        String file ="HandoutExampleNo.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // NoDoctors.dwo
    @Test
    public void noDoctors() throws Exception {
        String file ="NoDoctors.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // TooManyEvens.dwo
    @Test
    public void tooManyEvens() throws Exception  {
        String file ="TooManyEvens.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // TooManyHalves.dwo
    @Test
    public void tooManyHalves() throws Exception {
        String file ="TooManyHalves.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = false;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // ComplexYes.dwo
    @Test
    public void complexYes() throws Exception  {
        String file ="ComplexYes.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // DontFirstFit.dwo
    @Test
    public void dontFirstFit() throws Exception  {
        String file ="DontFirstFit.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // Fibonacci.dwo
    @Test
    public void fibonacci() throws Exception {
        String file ="Fibonacci.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // HandoutExampleYes1.dwo
    @Test
    public void handoutExampleYes1() throws Exception  {
        String file ="HandoutExampleYes1.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // HandoutExampleYes2.dwo
    @Test
    public void handoutExampleYes2() throws Exception  {
        String file ="HandoutExampleYes2.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // Nobody.dwo
    @Test
    public void nobody() throws Exception {
        String file ="Nobody.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // NoPatients.dwo
    @Test
    public void noPatients() throws Exception  {
        String file ="NoPatients.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // OneDoctor.dwo
    @Test
    public void oneDoctor() throws Exception  {
        String file ="OneDoctor.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // SimpleYes.dwo
    @Test
    public void simpleYes() throws Exception {
        String file ="SimpleYes.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    // SlackSpace.dwo
    @Test
    public void slackSpace() throws Exception {
        String file ="SlackSpace.dwo";
        Map<String, Set<String>> schedule = new HashMap<>();
        boolean expected = true;
        DWOTestCase testCase = runTest(file, schedule, expected);
        assertEquals(expected, allPatientsSeen(testCase, schedule));
        assertEquals(expected, testCase.result);
    }

    private DWOTestCase runTest( String fileName, Map<String, Set<String>> schedule, boolean expected ) throws Exception {
        Scanner input = new Scanner(Path.of("input", fileName).toFile());

        HospitalTestCase hTestCase = DoctorsWithoutOrdersParser.loadHospitalTestCase(input);
        DWOTestCase testCase = new DWOTestCase(hTestCase);
        Map<String, Integer> docsMap = docVectorToMap(testCase.doctors);
        Map<String, Integer> patientsMap = patientVectorToMap(testCase.patients);
        boolean result = DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(docsMap),
                new HashMap<>(patientsMap), schedule);

        testCase.result = result;
        return testCase;
    }

    private boolean allPatientsSeen(DWOTestCase testCase, Map<String, Set<String>> schedule ) {
        Set<String> seen = new HashSet<>();
        for (String doctor: schedule.keySet()) {
            seen.addAll(schedule.get(doctor));
        }

        Set<String> allPatients = new HashSet<>();
        for (Patient patient: testCase.patients) {
            allPatients.add( patient.getName() );
        }

        return seen.equals(allPatients);
    }
}
