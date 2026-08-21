import com.dcit308.wasteops.db.*;
import com.dcit308.wasteops.domain.*;
import java.nio.file.*;

public class TestDb {
    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();
        try {
            importer.importAll(tempDir.toString());
            System.out.println("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
