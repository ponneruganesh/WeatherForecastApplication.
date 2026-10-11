package com.example.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class WeatherService {
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> getForecast(String city) throws Exception {
        if (city == null || city.trim().isEmpty()) throw new IllegalArgumentException("Please enter a city name.");
        String encoded = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
        JsonNode geo = getJson("https://geocoding-api.open-meteo.com/v1/search?name=" + encoded + "&count=1&language=en&format=json");
        JsonNode results = geo.path("results");
        if (!results.isArray() || results.isEmpty()) throw new IllegalArgumentException("City not found. Please check the spelling and try again.");
        JsonNode place = results.get(0);
        double latitude = place.path("latitude").asDouble();
        double longitude = place.path("longitude").asDouble();
        String resolvedCity = place.path("name").asText(city.trim());
        String country = place.path("country").asText("");
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,wind_speed_10m,weather_code"
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset"
                + "&timezone=auto&forecast_days=5";
        JsonNode data = getJson(url);
        JsonNode current = data.path("current");
        JsonNode daily = data.path("daily");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("city", resolvedCity); result.put("country", country);
        result.put("timezone", data.path("timezone").asText("Local time"));
        result.put("current", Map.of(
                "temperature", current.path("temperature_2m").asDouble(),
                "feelsLike", current.path("apparent_temperature").asDouble(),
                "humidity", current.path("relative_humidity_2m").asInt(),
                "windSpeed", current.path("wind_speed_10m").asDouble(),
                "precipitation", current.path("precipitation").asDouble(),
                "weatherCode", current.path("weather_code").asInt(),
                "isDay", current.path("is_day").asInt() == 1,
                "time", current.path("time").asText("")));
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int i = 0; i < daily.path("time").size(); i++) {
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("date", daily.path("time").get(i).asText());
            day.put("weatherCode", daily.path("weather_code").get(i).asInt());
            day.put("max", daily.path("temperature_2m_max").get(i).asDouble());
            day.put("min", daily.path("temperature_2m_min").get(i).asDouble());
            day.put("rainChance", daily.path("precipitation_probability_max").get(i).asInt(0));
            day.put("sunrise", daily.path("sunrise").get(i).asText(""));
            day.put("sunset", daily.path("sunset").get(i).asText(""));
            forecast.add(day);
        }
        result.put("forecast", forecast);
        return result;
    }

    private JsonNode getJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json").header("User-Agent", "WeatherForecastStudentProject/1.0").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IllegalStateException("Weather API returned status " + response.statusCode());
        return mapper.readTree(response.body());
    }
}
