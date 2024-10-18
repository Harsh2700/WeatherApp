import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class Weather {
    private static JFrame frame;
    private static JTextField locationField;
    private static JTextArea weatherDisplay;
    private static JButton fetchButton;
    private static JLabel weatherIconLabel;
    private static JPanel weatherPanel;
    private static String apiKey = "6a4d5f1bb4c5b26afee8af16d64430e0";

    private static Font customFont = new Font("Arial", Font.BOLD, 16);
    private static Color bgColor = new Color(135, 206, 250);  // Light blue background
    private static Color displayBgColor = new Color(255, 255, 255);  // White background for display area

    private static String fetchWeatherData(String city) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse response using org.json.JSONObject
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject mainObj = jsonObject.getJSONObject("main");

            double temperatureKelvin = mainObj.getDouble("temp");
            long humidity = mainObj.getLong("humidity");
            double temperatureCelcius = temperatureKelvin - 273.15;

            // Retrieve weather description and icon
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");
            String iconCode = weather.getString("icon");

            // Set weather icon
            setWeatherIcon(iconCode);

            return "Description: " + description + "\nTemperature: " + String.format("%.2f", temperatureCelcius) + " °C\nHumidity: " + humidity + "%";
        } catch (Exception e) {
            return "Failed to fetch weather data";
        }
    }

    private static void setWeatherIcon(String iconCode) {
        // Load weather icon from OpenWeatherMap's free icon set
        ImageIcon icon = new ImageIcon("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
        weatherIconLabel.setIcon(icon);
    }

    public static void main(String[] args) {
        // Set up the frame
        frame = new JFrame("Weather Forecast App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.getContentPane().setBackground(bgColor);  // Set background color
        frame.setLayout(new BorderLayout());

        // Set up input field and button
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setOpaque(false);  // Make the panel transparent to show the background
        locationField = new JTextField(15);
        locationField.setFont(customFont);
        fetchButton = new JButton("Fetch Weather");
        fetchButton.setFont(customFont);
        weatherIconLabel = new JLabel();  // Label for weather icon

        topPanel.add(new JLabel("Enter City:"));
        topPanel.add(locationField);
        topPanel.add(fetchButton);
        topPanel.add(weatherIconLabel);  // Add icon label to the top panel

        // Set up weather display area
        weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.setBackground(displayBgColor);  // White background for readability
        weatherDisplay = new JTextArea(6, 30);
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(customFont);
        weatherDisplay.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(weatherDisplay);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);  // Transparent scrollpane

        weatherPanel.add(scrollPane, BorderLayout.CENTER);

        // Action listener for the button
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = locationField.getText();
                String weatherInfo = fetchWeatherData(city);
                weatherDisplay.setText(weatherInfo);
            }
        });

        // Add panels to the frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(weatherPanel, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }
}
