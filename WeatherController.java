package com.example.weather.controller;

import com.example.weather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;
    public WeatherController(WeatherService weatherService) { this.weatherService = weatherService; }

    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam(defaultValue = "Nellore") String city) {
        try { return ResponseEntity.ok(weatherService.getForecast(city)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "Could not load weather right now. Check your internet connection and try again.")); }
    }
}
