#include <Arduino.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include "LoRaLayer.h"

LoRaConnection lora;

// OLED display size
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

// I2C pins for Heltec WiFi LoRa 32 V2
#define OLED_SDA 4
#define OLED_SCL 15
#define OLED_RST 16

// Create display instance
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RST);

// DS18B20 sensor pin
#define ONE_WIRE_BUS 18

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

DeviceAddress sensor_a = { 0x28, 0x74, 0xFA, 0x84, 0x00, 0x00, 0x00, 0x96 };
DeviceAddress sensor_b = { 0x28, 0x4F, 0xE3, 0x84, 0x00, 0x00, 0x00, 0x0C };

String lastLoRaMsg = "";
unsigned long lastSendTime = 0;
const unsigned long sendInterval = 1000; // Send every 5 seconds

void setup() {
  Serial.begin(115200);

  // Initialize LoRa
  lora.begin();
  lora.setTimeout(5000); // Set 5 second timeout

  // Initialize OLED with proper pins for Heltec board
  Wire.begin(OLED_SDA, OLED_SCL);
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    while (true);
  }
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.display();

  // Start DS18B20 sensors
  sensors.begin();

  // Display startup message
  display.clearDisplay();
  display.setCursor(0,0);
  display.println("Temp Sensor Node");
  display.println("Initializing...");
  display.display();
  delay(1000);
}

void loop() {
  sensors.requestTemperatures();

  float temp1 = sensors.getTempC(sensor_a);
  float temp2 = sensors.getTempC(sensor_b);

  // Check for incoming LoRa messages
 String incoming = lora.receive();
  if (incoming != "") {
    lastLoRaMsg = incoming;
    Serial.print("LoRa Received: ");
    Serial.println(incoming);
  }

  // Send temperature data with timeout handling
  if (millis() - lastSendTime > sendInterval) {
    unsigned long startTime = millis();
    
    char message[16];
    snprintf(message, sizeof(message), "%.1f,%.1f", temp1, temp2);
    
    Serial.print("Attempting to send: ");
    Serial.println(message);
    
    bool sendSuccess = lora.send(message);
    
    if (sendSuccess) {
      unsigned long transmitTime = millis() - startTime;
      Serial.print("Transmit successful, time: ");
      Serial.print(transmitTime);
      Serial.println(" ms");
    } else {
      Serial.println("Failed to send within timeout period");
    }
    
    lastSendTime = millis();
  }

  // Display on OLED
  display.clearDisplay();
  display.setCursor(0,0);
  
  if (temp1 != DEVICE_DISCONNECTED_C) {
    display.printf("Sensor1: %.1f C\n", temp1);
  } else {
    display.println("Sensor1: Disconnected");
  }

  if (temp2 != DEVICE_DISCONNECTED_C) {
    display.printf("Sensor2: %.1f C\n", temp2);
  } else {
    display.println("Sensor2: Disconnected");
  }

  display.println();

  if (lastLoRaMsg.length() > 0) {
    display.println("Last LoRa Msg:");
    display.println(lastLoRaMsg);
  } else {
    display.println("No LoRa Msg");
  }

  display.display();

  delay(1000);
}