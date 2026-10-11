# SkyCast — Weather Forecast Application

A responsive weather website built with **Java 17, Spring Boot, REST API integration, and Jackson JSON parsing**. It retrieves live weather data from Open-Meteo, which does not require an API key.

## Features
- Search for a city worldwide
- Current temperature, feels-like temperature, humidity, wind speed, and precipitation
- Five-day forecast with daily high/low temperatures and rain probability
- Sunrise and sunset times
- Responsive interface for mobile and desktop
- Input validation and readable API error messages

## Technologies
- Java 17
- Spring Boot 3.3.5 / Spring Web
- Java `HttpClient` for REST API requests
- Jackson `ObjectMapper` / `JsonNode` for JSON parsing
- HTML5, CSS3, and JavaScript
- Open-Meteo Geocoding API and Forecast API

## Requirements
- JDK 17 or later
- Maven 3.6+ (or use an IDE with Maven support)
- Internet connection (needed to download Maven dependencies and fetch weather data)

## Run the application
1. Extract this ZIP file.
2. Open a terminal in the extracted `WeatherForecastApplication` folder.
3. Run: `mvn spring-boot:run`
4. Open `http://localhost:8080` in your browser.
5. Search for a city such as Nellore, Hyderabad, London, or Tokyo.

You can also import the folder as an existing Maven project in IntelliJ IDEA, Eclipse, or Spring Tool Suite and run `WeatherForecastApplication.java`.

## API endpoint
`GET http://localhost:8080/api/weather?city=Nellore`

The backend first resolves the city using Open-Meteo Geocoding, then requests current conditions and five forecast days. Jackson parses the JSON responses and the controller returns a structured JSON object to the browser.

## Project structure
```text
WeatherForecastApplication/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/weather/
    │   ├── WeatherForecastApplication.java
    │   ├── controller/WeatherController.java
    │   └── service/WeatherService.java
    └── resources/static/
        ├── index.html
        ├── style.css
        └── app.js
```

## Notes
- Weather data is live and depends on Open-Meteo availability and internet access.
- No API key or database setup is required.
- The project is supplied as source code; compile and run it locally before presenting or submitting. Maven may need to download dependencies the first time.
- Weather data source: https://open-meteo.com/ (review the provider's terms/attribution requirements before public deployment).
