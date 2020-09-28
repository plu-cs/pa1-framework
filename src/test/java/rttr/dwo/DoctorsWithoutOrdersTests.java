package rttr.dwo;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoctorsWithoutOrdersTests {

    // Provided Test: Can't schedule if a patient requires more hours than any doctor has.
    @Test
    public void schedulePatientWithTooManyHours() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. Zhivago", 8 ),
                Map.entry( "Dr. Strange", 8 ),
                Map.entry( "Dr. Horrible", 8 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Needs help", 9 )  // Needs more time than any one doctor can provide
        );

        Assert.assertFalse(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
    }

    // Provided Test: Can schedule if doctor has way more time than patient needs.
    @Test
    public void scheduleDoctorWithPlentyOfTime() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. Wheelock", 12 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Lucky Patient", 8 )
        );

        Assert.assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
    }

    // Provided Test: Can schedule if there's one doctor and one patient with the same hours.
    @Test
    public void oneDoctorOnePatientSameHoursCanSchedule() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. Wheelock", 8 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Lucky Patient", 8 )
        );

        Assert.assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
    }

    // Provided Test: Schedule for one doctor and one patient is correct.
    @Test
    public void oneDoctorOnePatientSameHoursCorrectSchedule() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. Wheelock", 8 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Lucky Patient", 8 )
        );

        Assert.assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));

        Map<String, Set<String>> expectedSchedule = Map.ofEntries(
                Map.entry("Dr. Wheelock", Set.of( "Lucky Patient" ))
        );
        Assert.assertEquals(expectedSchedule, schedule);
    }

    // Provided Test: Single doctor can see many patients.
    @Test
    public void singleDoctorCanSeeManyPatients() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. House", 7 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Patient A", 4 ),
                Map.entry( "Patient B", 2 ),
                Map.entry( "Patient C", 1 )
        );

        assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));

        Map<String, Set<String>> expectedSchedule = Map.ofEntries(
                Map.entry("Dr. House", Set.of( "Patient A", "Patient B", "Patient C" ) )
        );
        assertEquals(expectedSchedule, schedule);
    }

    // Provided Test: Multiple doctors can see multiple patients.
    @Test
    public void multipleDoctorsCanSeeMultiplePatients() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Dr. House", 7  ),
                Map.entry( "AutoDoc",   70 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Patient A", 4  ),
                Map.entry( "Patient B", 2  ),
                Map.entry( "Patient C", 1  ),
                Map.entry( "Patient D", 40 ),
                Map.entry( "Patient E", 20 ),
                Map.entry( "Patient F", 10 )
        );

        assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));

        Map<String, Set<String>> expectedSchedule = Map.ofEntries(
                Map.entry( "Dr. House", Set.of( "Patient A", "Patient B", "Patient C" ) ),
                Map.entry( "AutoDoc", Set.of("Patient D", "Patient E", "Patient F" ) )
        );
        assertEquals(expectedSchedule, schedule);
    }

    // Provided Test: Doesn't necessarily assign neediest patient to most available doctor.
    @Test
    public void notGreedy() {
        Map<String, Set<String>> schedule = new HashMap<>();

        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Doctor Workaholic", 10 ),
                Map.entry( "Doctor Average",    8 ),
                Map.entry( "Doctor Lazy",       6 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Patient EightHour", 8 ),
                Map.entry( "Patient SixHour",   6 ),
                Map.entry( "Patient FiveHour1", 5 ),
                Map.entry( "Patient FiveHour2", 5 )
        );

        /* You can't make this work if you assign Patient EightHour to Doctor Workaholic.
         * The only way for this setup to work is if you give the two five-hour patients
         * to Doctor Workaholic.
         */
        Map<String, Set<String>> expectedSchedule = Map.ofEntries(
                Map.entry( "Doctor Workaholic", Set.of( "Patient FiveHour1", "Patient FiveHour2" ) ),
                Map.entry("Doctor Average",    Set.of("Patient EightHour") ),
                Map.entry("Doctor Lazy",       Set.of("Patient SixHour" ) )
        );

        assertTrue(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
        assertEquals(expectedSchedule, schedule);
    }

    // Provided Test: Can't schedule patients if there are no doctors.
    @Test
    public void noDoctors() {
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "You Poor Soul", 8 )
        );
        Map<String, Set<String>> schedule = new HashMap<>();
        assertFalse(DoctorsWithoutOrders.canAllPatientsBeSeen(new HashMap<>(), patients, schedule));
    }

    // Provided Test: Agreement in total hours doesn't mean a schedule exists (1).
    @Test
    public void totalHoursAgree1() {
        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Doctor A", 3 ),
                Map.entry( "Doctor B", 3 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Patient X", 2 ),
                Map.entry( "Patient Y", 2 ),
                Map.entry( "Patient Z", 2 )
        );

        /* Notice the the total hours free (6) matches the total hours needed (6), but it's set
         * up in a way where things don't align properly.
         */
        Map<String, Set<String>> schedule = new HashMap<>();
        assertFalse(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
    }

    // Provided Test: Agreement in total hours doesn't mean a schedule exists (2).
    @Test
    public void totalHoursAgree2() {
        Map<String, Integer> doctors = Map.ofEntries(
                Map.entry( "Doctor A", 8 ),
                Map.entry( "Doctor B", 8 ),
                Map.entry( "Doctor C", 8 )
        );
        Map<String, Integer> patients = Map.ofEntries(
                Map.entry( "Patient U", 5 ),
                Map.entry( "Patient V", 5 ),
                Map.entry( "Patient W", 5 ),
                Map.entry( "Patient X", 4 ),
                Map.entry( "Patient Y", 3 ),
                Map.entry( "Patient Z", 2 )
        );

        /* Notice the the total hours free (24) matches the total number of hours needed
         * (24), but the way those hours are divvied up makes things impossible. Specifically,
         * no doctor can see two of the patients who each need five hours, so they need to be
         * spread around the three doctors evenly, but then there isn't enough time for
         * anyone to see the patient who needs four hours.
         */
        Map<String, Set<String>> schedule = new HashMap<>();
        assertFalse(DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, schedule));
    }
}
