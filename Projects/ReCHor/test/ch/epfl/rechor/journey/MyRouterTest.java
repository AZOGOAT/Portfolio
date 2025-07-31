package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyRouterTest {

    private static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(stationName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Nom de station inconnu : " + stationName);
    }

    @Test
    void routerFindsExpectedJourney() throws IOException {
        TimeTable timeTable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable/timetable 14")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");

        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor.journeys(profile, depStationId).get(32);

        assertNotNull(journey);
        System.out.println(JourneyIcalConverter.toIcalendar(journey));
    }
}