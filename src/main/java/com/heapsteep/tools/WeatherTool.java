package com.heapsteep.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherTool {

    private final RestTemplate restTemplate = new RestTemplate();
    // Read API key from environment variable WEATHER_API_KEY
    private static final String WEATHER_API_KEY = System.getenv("WEATHER_API_KEY");
    //private static final String WEATHER_API_KEY = "a5e7c920f1cc4cd9b2992919261805";

    /*@Tool(description = "Get the current weather for a specified city.")
    public String getWeather(String city) {
        switch(city.toLowerCase()) {
            case "new york":
                return "The weather in New York is currently sunny with a temperature of 25°C.";
            case "london":
                return "The weather in London is currently cloudy with a temperature of 18°C.";
            case "tokyo":
                return "The weather in Tokyo is currently rainy with a temperature of 22°C.";
            default:
                return "Sorry, I don't have the weather information for " + city + ".";
        }
    }*/

    @Tool(description = "Get the weather forecast for the given city and date.")
    public String getWeather(String city, String date) {
        if (WEATHER_API_KEY == null || WEATHER_API_KEY.isEmpty()) {
            return "Weather API key not configured. Please set the environment variable WEATHER_API_KEY.";
        }

        String url = "http://api.weatherapi.com/v1/forecast.json?key=" + WEATHER_API_KEY + "&q=" + city + "&date=" + date;
        try {
            String response = restTemplate.getForObject(url, String.class);
            if(response != null) {
               return response;
            }else{
                return "Sorry, I couldn't retrieve the weather information for " + city + ".";
            }
        } catch (Exception e) {
            return "Sorry, there was an error retrieving the weather information for " + city + ".";
        }
    }

}
