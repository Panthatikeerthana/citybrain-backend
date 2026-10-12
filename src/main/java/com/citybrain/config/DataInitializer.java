package com.citybrain.config;

import com.citybrain.model.*;
import com.citybrain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TrafficRepository trafficRepository;
    private final WeatherRepository weatherRepository;
    private final CitizenComplaintRepository citizenComplaintRepository;
    private final EmergencyIncidentRepository emergencyIncidentRepository;
    private final WaterIssueRepository waterIssueRepository;
    private final WasteRecordRepository wasteRecordRepository;
    private final PollutionRecordRepository pollutionRecordRepository;
    private final RiskPredictionRepository riskPredictionRepository;

    @Value("${citybrain.seed.enabled:true}")
    private boolean seedEnabled;

    public DataInitializer(
            TrafficRepository trafficRepository,
            WeatherRepository weatherRepository,
            CitizenComplaintRepository citizenComplaintRepository,
            EmergencyIncidentRepository emergencyIncidentRepository,
            WaterIssueRepository waterIssueRepository,
            WasteRecordRepository wasteRecordRepository,
            PollutionRecordRepository pollutionRecordRepository,
            RiskPredictionRepository riskPredictionRepository
    ) {
        this.trafficRepository = trafficRepository;
        this.weatherRepository = weatherRepository;
        this.citizenComplaintRepository = citizenComplaintRepository;
        this.emergencyIncidentRepository = emergencyIncidentRepository;
        this.waterIssueRepository = waterIssueRepository;
        this.wasteRecordRepository = wasteRecordRepository;
        this.pollutionRecordRepository = pollutionRecordRepository;
        this.riskPredictionRepository = riskPredictionRepository;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Database seeding is disabled (citybrain.seed.enabled=false).");
            return;
        }

        log.info("Checking City Brain operational tables in MySQL...");

        seedTraffic();
        seedWeather();
        seedComplaints();
        seedEmergencies();
        seedWaterIssues();
        seedWasteRecords();
        seedPollutionRecords();
        seedRiskPredictions();

        log.info("City Brain operational tables verified and initialized.");
    }

    private void seedTraffic() {
        if (trafficRepository.count() > 0) return;
        log.info("Seeding initial traffic corridor records...");

        LocalDateTime now = LocalDateTime.now();
        List<Traffic> records = List.of(
                createTraffic("HITEC City - Cyber Towers Junction", 78, 3420, "Heavy Congestion", now),
                createTraffic("Gachibowli Outer Ring Road (ORR)", 42, 2150, "Moderate", now),
                createTraffic("Banjara Hills Road No. 12", 65, 1890, "Heavy Congestion", now),
                createTraffic("Jubilee Hills Check Post", 58, 2400, "Moderate", now),
                createTraffic("Secunderabad Clock Tower", 35, 1650, "Smooth", now),
                createTraffic("Begumpet Airport Flyover", 28, 1420, "Smooth", now),
                createTraffic("Madhapur Inorbit Mall Road", 72, 2980, "Heavy Congestion", now),
                createTraffic("Charminar Old City Corridor", 85, 1950, "Severe Congestion", now),
                createTraffic("Kukatpally Y-Junction", 62, 3100, "Moderate", now),
                createTraffic("Ameerpet Metro Hub", 80, 3800, "Heavy Congestion", now)
        );
        trafficRepository.saveAll(records);
    }

    private Traffic createTraffic(String loc, int cong, int veh, String st, LocalDateTime dt) {
        Traffic t = new Traffic();
        t.setLocation(loc);
        t.setCongestion(cong);
        t.setVehicles(veh);
        t.setStatus(st);
        t.setRecordedAt(dt);
        return t;
    }

    private void seedWeather() {
        if (weatherRepository.count() > 0) return;
        log.info("Seeding initial weather station records...");

        LocalDateTime now = LocalDateTime.now();
        List<Weather> stations = List.of(
                createWeather("Greater Hyderabad Central", 28.5, 62, 0.0, now),
                createWeather("HITEC City Station", 29.1, 58, 0.0, now),
                createWeather("Banjara Hills Station", 27.8, 65, 0.0, now),
                createWeather("Secunderabad Station", 28.2, 60, 0.0, now),
                createWeather("Shamshabad Airport", 27.4, 66, 0.0, now)
        );
        weatherRepository.saveAll(stations);
    }

    private Weather createWeather(String loc, double temp, int hum, double rain, LocalDateTime dt) {
        Weather w = new Weather();
        w.setLocation(loc);
        w.setTemperature(temp);
        w.setHumidity(hum);
        w.setRainfall(rain);
        w.setRecordedAt(dt);
        return w;
    }

    private void seedComplaints() {
        if (citizenComplaintRepository.count() > 0) return;
        log.info("Seeding initial citizen complaints...");

        List<CitizenComplaint> complaints = List.of(
                new CitizenComplaint("Rajesh Kumar", "Road & Potholes", "Major crater pothole on Gachibowli to Financial District main corridor causing traffic bottlenecks.", "Gachibowli", "HIGH", "IN_PROGRESS"),
                new CitizenComplaint("Priya Sharma", "Water Supply", "Low pressure and intermittent supply in residential sector 4 since morning.", "Banjara Hills", "MEDIUM", "OPEN"),
                new CitizenComplaint("Anand Reddy", "Garbage & Sanitation", "Overflowing dumper bin near community park requiring immediate clearance.", "Jubilee Hills", "HIGH", "RESOLVED"),
                new CitizenComplaint("Sneha Patel", "Street Lighting", "Multiple non-functional LED street lights along arterial lane creating safety concerns.", "Madhapur", "LOW", "IN_PROGRESS"),
                new CitizenComplaint("Mohammed Farooq", "Drainage & Sewage", "Sewage overflow near metro pillar 124 during peak evening hours.", "Ameerpet", "CRITICAL", "OPEN"),
                new CitizenComplaint("Kavitha Rao", "Traffic Signal Malfunction", "Signal blinking amber continuously at four-way intersection causing gridlock.", "Begumpet", "HIGH", "RESOLVED"),
                new CitizenComplaint("Venkatesh Murthy", "Illegal Encroachment", "Footpath obstruction by construction material causing pedestrian hazard.", "Secunderabad", "MEDIUM", "OPEN"),
                new CitizenComplaint("Deepak Varma", "Noise Pollution", "Commercial diesel generator noise exceeding permissible decibel limits after 10 PM.", "HITEC City", "LOW", "RESOLVED")
        );
        citizenComplaintRepository.saveAll(complaints);
    }

    private void seedEmergencies() {
        if (emergencyIncidentRepository.count() > 0) return;
        log.info("Seeding initial emergency incidents...");

        List<EmergencyIncident> incidents = List.of(
                new EmergencyIncident("Traffic Accident", "PVNR Elevated Expressway Pillar 84", "High", "Multi-vehicle collision blocking two lanes towards international airport.", "IN_PROGRESS"),
                new EmergencyIncident("Water Pipeline Rupture", "Kondapur Botanical Garden Road", "Critical", "Major 900mm primary transmission main breach causing localized road subsidence.", "DISPATCHED"),
                new EmergencyIncident("Electrical Substation Smoke", "Sanath Nagar Industrial Area", "Critical", "Transformer thermal excursion with contained smoke emission. Rapid response crew on site.", "CONTAINED"),
                new EmergencyIncident("Flash Waterlogging", "Khairatabad RTA Underpass", "High", "Debris clogging storm drain pumps during sudden downpour. Mobile pumping unit deployed.", "IN_PROGRESS"),
                new EmergencyIncident("Structural Facade Crack", "Sultan Bazar Heritage Corridor", "Medium", "Structural sensor telemetry triggered anomaly alert on commercial facade.", "REPORTED")
        );
        emergencyIncidentRepository.saveAll(incidents);
    }

    private void seedWaterIssues() {
        if (waterIssueRepository.count() > 0) return;
        log.info("Seeding initial water utility issues...");

        List<WaterIssue> issues = List.of(
                new WaterIssue("Madhapur Sector 2 Pipeline", "Pipeline Leakage", "High", "Main 600mm distribution pipe joint leak resulting in pressure loss.", "Repair in Progress"),
                new WaterIssue("Gachibowli Telecom Nagar", "Low Pressure Anomaly", "Medium", "Elevated reservoir booster pump trip causing reduced delivery head.", "Inspecting"),
                new WaterIssue("Banjara Hills Road 10", "Water Quality Alert", "Low", "Turbidity sensor threshold exceeded post-maintenance pipeline flush.", "Resolved"),
                new WaterIssue("Secunderabad Marredpally", "Contamination Risk", "High", "Suspected ingress near storm drain crossing undergoing sanitization.", "Active Remediation"),
                new WaterIssue("Kukatpally Housing Board", "Valve Malfunction", "Medium", "Telemetry-controlled pressure reducing valve stuck at 30% aperture.", "Scheduled Maintenance")
        );
        waterIssueRepository.saveAll(issues);
    }

    private void seedWasteRecords() {
        if (wasteRecordRepository.count() > 0) return;
        log.info("Seeding initial solid waste records...");

        List<WasteRecord> records = List.of(
                new WasteRecord("HITEC City Phase 2 Depot", "Municipal Solid Waste", 1250.0, "Collected"),
                new WasteRecord("Begumpet Main Market", "Commercial Organic Waste", 850.0, "Scheduled for Pickup"),
                new WasteRecord("Jubilee Hills Ward 14", "Recyclable Plastics", 420.0, "Collected"),
                new WasteRecord("Charminar Heritage Zone", "Mixed Solid Waste", 1600.0, "In Transit"),
                new WasteRecord("Gachibowli Financial Hub", "Electronic Waste", 310.0, "Processed")
        );
        wasteRecordRepository.saveAll(records);
    }

    private void seedPollutionRecords() {
        if (pollutionRecordRepository.count() > 0) return;
        log.info("Seeding initial air quality sensor station records...");

        List<PollutionRecord> records = List.of(
                new PollutionRecord("HITEC City Cyber Towers", 92, 38.5, 72.0, "Moderate"),
                new PollutionRecord("Banjara Hills Eco-Park", 58, 18.2, 42.0, "Good"),
                new PollutionRecord("Sanath Nagar Industrial Corridor", 168, 84.0, 145.0, "Unhealthy"),
                new PollutionRecord("Charminar South Station", 135, 62.0, 110.0, "Unhealthy for Sensitive Groups"),
                new PollutionRecord("Gachibowli Stadium Enclave", 74, 24.5, 55.0, "Moderate"),
                new PollutionRecord("Jubilee Hills Road 36", 65, 21.0, 48.0, "Moderate"),
                new PollutionRecord("Secunderabad Railway Terminal", 142, 68.0, 120.0, "Unhealthy for Sensitive Groups"),
                new PollutionRecord("Begumpet Airport Monitoring Post", 82, 32.0, 64.0, "Moderate")
        );
        pollutionRecordRepository.saveAll(records);
    }

    private void seedRiskPredictions() {
        if (riskPredictionRepository.count() > 0) return;
        log.info("Seeding initial predictive risk models...");

        List<RiskPrediction> risks = List.of(
                new RiskPrediction("PVNR Expressway Junction", "Traffic Gridlock Risk", "High", 0.88, "Predictive model forecasts high evening peak saturation due to IT corridor outbound surge."),
                new RiskPrediction("Gachibowli Storm Drain Channel 4", "Waterlogging Risk", "Critical", 0.92, "Runoff accumulation model indicates 85mm surface ponding threshold if rainfall exceeds 15mm/hr."),
                new RiskPrediction("Sanath Nagar Air Basin", "Air Quality Degradation Risk", "High", 0.81, "Thermal inversion layer forecast indicates PM2.5 particulate trap between 05:00 and 09:00."),
                new RiskPrediction("HITEC City Substation 3", "Grid Load Overcapacity", "Medium", 0.67, "Cooling demand trajectory predicts 94% transformer utilization index."),
                new RiskPrediction("Begumpet Arterial Corridor", "Structural Vibration Anomaly", "Low", 0.35, "Flyover sensor harmonics within safe engineering tolerance margins.")
        );
        riskPredictionRepository.saveAll(risks);
    }
}
