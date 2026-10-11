package com.citybrain.service;

import com.citybrain.dto.LivePollutionDto;
import com.citybrain.dto.LiveTrafficDto;
import com.citybrain.dto.LiveWeatherDto;
import com.citybrain.dto.TrafficFlowItem;
import com.citybrain.model.*;
import com.citybrain.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CityBrainContextService {

    private final TrafficRepository trafficRepository;
    private final WeatherRepository weatherRepository;
    private final CitizenComplaintRepository complaintRepository;
    private final EmergencyIncidentRepository emergencyRepository;
    private final WaterIssueRepository waterRepository;
    private final WasteRecordRepository wasteRepository;
    private final PollutionRecordRepository pollutionRepository;
    private final RiskPredictionRepository riskRepository;

    private final WeatherApiService weatherApiService;
    private final PollutionApiService pollutionApiService;
    private final TrafficApiService trafficApiService;

    public CityBrainContextService(
            TrafficRepository trafficRepository,
            WeatherRepository weatherRepository,
            CitizenComplaintRepository complaintRepository,
            EmergencyIncidentRepository emergencyRepository,
            WaterIssueRepository waterRepository,
            WasteRecordRepository wasteRepository,
            PollutionRecordRepository pollutionRepository,
            RiskPredictionRepository riskRepository,
            WeatherApiService weatherApiService,
            PollutionApiService pollutionApiService,
            TrafficApiService trafficApiService
    ) {
        this.trafficRepository = trafficRepository;
        this.weatherRepository = weatherRepository;
        this.complaintRepository = complaintRepository;
        this.emergencyRepository = emergencyRepository;
        this.waterRepository = waterRepository;
        this.wasteRepository = wasteRepository;
        this.pollutionRepository = pollutionRepository;
        this.riskRepository = riskRepository;
        this.weatherApiService = weatherApiService;
        this.pollutionApiService = pollutionApiService;
        this.trafficApiService = trafficApiService;
    }

    /**
     * Builds focused, domain-relevant context distinguishing LIVE TELEMETRY from STORED HISTORICAL RECORDS.
     */
    public String buildContext(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "No user query provided.";
        }

        String query = userMessage.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();

        boolean matchAll = query.contains("summary") || query.contains("summarize") || 
                           query.contains("overview") || query.contains("today") || 
                           query.contains("status") || query.contains("city") || 
                           query.contains("everything") || query.contains("hello") || 
                           query.contains("hi") || query.contains("report");

        boolean matchTraffic = matchAll || query.contains("traffic") || query.contains("congestion") || 
                               query.contains("vehicle") || query.contains("road") || query.contains("speed") ||
                               query.contains("jam") || query.contains("uppal") || query.contains("lb nagar") || 
                               query.contains("hitec") || query.contains("kukatpally") || query.contains("junction");

        boolean matchWeather = matchAll || query.contains("weather") || query.contains("rain") || 
                               query.contains("temp") || query.contains("monsoon") || query.contains("climate") || 
                               query.contains("humidity") || query.contains("storm") || query.contains("forecast") ||
                               query.contains("now") || query.contains("current");

        boolean matchEmergency = matchAll || query.contains("emergency") || query.contains("incident") || 
                                 query.contains("accident") || query.contains("fire") || query.contains("police") || 
                                 query.contains("ambulance") || query.contains("critical") || query.contains("hazard") || 
                                 query.contains("danger");

        boolean matchWater = matchAll || query.contains("water") || query.contains("leak") || 
                             query.contains("pipeline") || query.contains("pressure") || query.contains("drainage") || 
                             query.contains("supply") || query.contains("flood") || query.contains("sewage");

        boolean matchWaste = matchAll || query.contains("waste") || query.contains("garbage") || 
                             query.contains("trash") || query.contains("bin") || query.contains("dump") || 
                             query.contains("sanitation") || query.contains("clean");

        boolean matchPollution = matchAll || query.contains("pollution") || query.contains("aqi") || 
                                 query.contains("air") || query.contains("smog") || query.contains("pm2.5") || 
                                 query.contains("pm10") || query.contains("emission") || query.contains("environment");

        boolean matchComplaints = matchAll || query.contains("complaint") || query.contains("grievance") || 
                                  query.contains("citizen") || query.contains("pothole") || query.contains("issue") || 
                                  query.contains("public") || query.contains("urgent");

        boolean matchRisks = matchAll || query.contains("risk") || query.contains("predict") || 
                             query.contains("threat") || query.contains("vulnerab") || query.contains("forecast risk") || 
                             query.contains("highest risk");

        int sectionsAdded = 0;

        // 1. LIVE & HISTORICAL WEATHER
        if (matchWeather) {
            // Live Weather Feed
            try {
                LiveWeatherDto liveWeather = weatherApiService.getLiveWeather(null, null, false);
                if (liveWeather != null) {
                    sb.append("--- LIVE WEATHER TELEMETRY (Source: Open-Meteo Live API Feed) ---\n");
                    sb.append(String.format(
                        Locale.ROOT,
                        "- Status: LIVE | Location: %s | Temp: %.1f°C | Humidity: %d%% | Precipitation: %.1f mm | Wind: %.1f km/h | Condition: %s | Observed: %s\n\n",
                        liveWeather.getLocation(), liveWeather.getTemperature(), liveWeather.getHumidity(),
                        liveWeather.getPrecipitation(), liveWeather.getWindSpeed(), liveWeather.getCondition(),
                        liveWeather.getObservationTime()
                    ));
                    sectionsAdded++;
                }
            } catch (Exception e) {
                sb.append("--- LIVE WEATHER TELEMETRY: Temporarily unavailable ---\n\n");
            }

            // Stored Historical Records
            List<Weather> records = weatherRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- STORED HISTORICAL WEATHER (Source: MySQL weather table) ---\n");
                int limit = matchAll ? 3 : 5;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    Weather w = records.get(i);
                    sb.append(String.format(
                        Locale.ROOT,
                        "- [Historical] Zone: %s | Temp: %.1f°C | Humidity: %d%% | Rainfall: %.1f mm\n",
                        w.getLocation(), w.getTemperature(), w.getHumidity(), w.getRainfall()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 2. LIVE & HISTORICAL AIR QUALITY / POLLUTION
        if (matchPollution) {
            // Live Air Quality Feed
            try {
                LivePollutionDto liveAqi = pollutionApiService.getLivePollution(null, null, false);
                if (liveAqi != null) {
                    sb.append("--- LIVE AIR QUALITY TELEMETRY (Source: Open-Meteo Air Quality Feed) ---\n");
                    sb.append(String.format(
                        Locale.ROOT,
                        "- Status: LIVE | Location: %s | US AQI: %d (%s) | PM2.5: %.1f µg/m³ | PM10: %.1f µg/m³ | Dominant: %s | Observed: %s\n\n",
                        liveAqi.getLocation(), liveAqi.getAqi(), liveAqi.getCategory(),
                        liveAqi.getPm25(), liveAqi.getPm10(), liveAqi.getDominantPollutant(),
                        liveAqi.getObservationTime()
                    ));
                    sectionsAdded++;
                }
            } catch (Exception e) {
                sb.append("--- LIVE AIR QUALITY TELEMETRY: Temporarily unavailable ---\n\n");
            }

            // Stored Historical Records
            List<PollutionRecord> records = pollutionRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- STORED HISTORICAL POLLUTION (Source: MySQL pollution_records table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    PollutionRecord p = records.get(i);
                    sb.append(String.format(
                        Locale.ROOT,
                        "- [Historical] Station: %s | AQI: %d | PM2.5: %.1f µg/m³ | PM10: %.1f µg/m³ | Status: %s\n",
                        p.getLocation(), p.getAqi() != null ? p.getAqi() : 0,
                        p.getPm25() != null ? p.getPm25() : 0.0,
                        p.getPm10() != null ? p.getPm10() : 0.0,
                        p.getStatus()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 3. LIVE & HISTORICAL TRAFFIC
        if (matchTraffic) {
            // Live Traffic Feed
            LiveTrafficDto liveTraffic = trafficApiService.getLiveTraffic(false);
            if (liveTraffic != null && liveTraffic.isConfigured() && liveTraffic.getItems() != null && !liveTraffic.getItems().isEmpty()) {
                sb.append("--- LIVE TRAFFIC TELEMETRY (Source: TomTom Flow Feed) ---\n");
                for (TrafficFlowItem item : liveTraffic.getItems()) {
                    sb.append(String.format(
                        Locale.ROOT,
                        "- Status: LIVE | Node: %s | Congestion: %d%% | Current Speed: %d km/h | Free-flow: %d km/h | Flow: %s\n",
                        item.getLocation(), item.getCongestionLevel(), item.getCurrentSpeed(), item.getFreeFlowSpeed(), item.getStatus()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            } else {
                sb.append("--- LIVE TRAFFIC TELEMETRY STATUS ---\n");
                sb.append("- Notice: Live traffic unavailable — TomTom API key required (TRAFFIC_API_KEY). Never substitute fake traffic numbers. If asked about live traffic, state explicitly that live traffic is unavailable and TomTom API key is required.\n\n");
                sectionsAdded++;
            }

            // Stored Historical Records
            List<Traffic> records = trafficRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- STORED HISTORICAL TRAFFIC (Source: MySQL traffic table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    Traffic t = records.get(i);
                    sb.append(String.format(
                        Locale.ROOT,
                        "- [Historical] Node: %s | Congestion: %d%% | Vehicles: %d/hr | Status: %s\n",
                        t.getLocation(), t.getCongestion(), t.getVehicles(), t.getStatus()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 4. Emergency Incidents
        if (matchEmergency) {
            List<EmergencyIncident> records = emergencyRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- EMERGENCY INCIDENTS (Source: MySQL emergency_incidents table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    EmergencyIncident e = records.get(i);
                    sb.append(String.format(
                        "- Incident #%d: [%s] %s at %s | Status: %s | Details: %s\n",
                        e.getId(), e.getSeverity(), e.getType(), e.getLocation(), e.getStatus(), e.getDescription()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 5. Water Issues
        if (matchWater) {
            List<WaterIssue> records = waterRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- WATER DISTRIBUTION & ISSUES (Source: MySQL water_issues table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    WaterIssue w = records.get(i);
                    sb.append(String.format(
                        "- Water Issue #%d: %s at %s | Severity: %s | Status: %s | Details: %s\n",
                        w.getId(), w.getIssueType(), w.getLocation(), w.getSeverity(), w.getStatus(), w.getDescription()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 6. Waste Records
        if (matchWaste) {
            List<WasteRecord> records = wasteRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- SOLID WASTE MANAGEMENT (Source: MySQL waste_records table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    WasteRecord w = records.get(i);
                    sb.append(String.format(
                        Locale.ROOT,
                        "- Sector: %s | Type: %s | Quantity: %.1f tons | Status: %s\n",
                        w.getLocation(), w.getWasteType(), w.getQuantity() != null ? w.getQuantity() : 0.0, w.getStatus()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 7. Citizen Complaints
        if (matchComplaints) {
            List<CitizenComplaint> records = complaintRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- CITIZEN COMPLAINTS (Source: MySQL citizen_complaints table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    CitizenComplaint c = records.get(i);
                    sb.append(String.format(
                        "- Grievance #%d: [%s] %s | Location: %s | Priority: %s | Status: %s | By: %s\n",
                        c.getId(), c.getCategory(), c.getDescription(), c.getLocation(), c.getPriority(), c.getStatus(), c.getCitizenName()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        // 8. Risk Predictions
        if (matchRisks) {
            List<RiskPrediction> records = riskRepository.findAll();
            if (!records.isEmpty()) {
                sb.append("--- PREDICTIVE URBAN RISKS (Source: MySQL risk_predictions table) ---\n");
                int limit = matchAll ? 4 : 8;
                for (int i = 0; i < Math.min(records.size(), limit); i++) {
                    RiskPrediction r = records.get(i);
                    sb.append(String.format(
                        Locale.ROOT,
                        "- Risk #%d: [%s Level - Prob: %.0f%%] %s at %s | Details: %s\n",
                        r.getId(), r.getRiskLevel(), (r.getProbability() != null ? r.getProbability() * 100 : 0.0),
                        r.getRiskType(), r.getLocation(), r.getDescription()
                    ));
                }
                sb.append("\n");
                sectionsAdded++;
            }
        }

        if (sectionsAdded == 0) {
            return "No relevant telemetry or database records found in City Brain for this inquiry.";
        }

        return sb.toString().trim();
    }
}
