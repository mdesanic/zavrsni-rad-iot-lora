#include <GatewayLayer.h>
#include <DisplayLayer.h>

// Display pins
#define OLED_SDA    22
#define OLED_SCL    21
#define OLED_RST    4

// WiFi Config
const char* WIFI_SSID = "test_environment";
const char* WIFI_PASS = "nijemidobro";
const String SERVER_IP = "172.20.10.11";

DisplayLayer display(OLED_SDA, OLED_SCL, OLED_RST);
GatewayLayer gateway(WIFI_SSID, WIFI_PASS, SERVER_IP);

// Mock device configuration
const String MOCK_MAC = "D4:E5:F6:A7:B8:C9";
const String SENSOR1_ADDRESS = "2874FA8400000096";
const String SENSOR2_ADDRESS = "284FE3840000000C";

void displayError(const String &msg) {
  display.clear();
  display.setCursor(0, 0);
  display.println("ERROR:");
  display.println(msg);
  display.update(true);
  delay(5000); // Show error for 5 sec then continue
}

void displayStatus(const String &msg) {
  display.clear();
  display.setCursor(0, 0);
  display.println(msg);
  display.update(true);
}

void setup() {
  Serial.begin(115200);
  
  // Initialize display
  if (!display.begin()) {
    Serial.println("Display init failed!");
    while(1);
  }

  // Initialize WiFi
  if (!gateway.begin()) {
    displayError("WiFi FAILED");
  }

  // Register mock device (only once)
  if (!gateway.registerDevice()) {
    displayError("Registration failed");
  }

  displayStatus("Gateway Ready");
  delay(2000);
}

void loop() {
  // Generate mock temperatures
  float temp1 = 20.0 + random(0, 50)/10.0; // 20.0-25.0°C
  float temp2 = 15.0 + random(0, 60)/10.0; // 15.0-21.0°C

  // Send sensor 1 data
  if (!gateway.sendSensorData(MOCK_MAC, SENSOR1_ADDRESS, temp1)) {
    displayError("Sensor 1 send failed");
  }

  // Send sensor 2 data
  if (!gateway.sendSensorData(MOCK_MAC, SENSOR2_ADDRESS, temp2)) {
    displayError("Sensor 2 send failed");
  }

  // Display status
  display.clear();
  display.setCursor(0, 0);
  display.println("Last Sent Data:");
  display.println("Server: " + SERVER_IP);
  display.update(true);

  delay(10000); // Send every 10 seconds
}