#include <Arduino.h>
#include <SPI.h>
#include <LoRa.h>
#include <WiFi.h>
#include "LoRaLayer.h"
#include "WiFiLayer.h"

const char* ssid = "Tele2_i_ovce_i_novce";
const char* password = "4ESN5aJ5Y5";

WiFiConnection wifi(ssid, password);
LoRaConnection lora;

float temp1 = 0.0;
float temp2 = 0.0;
String lastStatus = "Waiting for data...";
unsigned long lastUpdateTime = 0;

void setup() {
  Serial.begin(115200);
  
  // Initialize WiFi
  wifi.connect();
  
  // Initialize LoRa
  lora.begin();
  
  Serial.println("Receiver initialized");
}

void parseTemperatureData(String message) {
  // Expected format: "T1:23.5,T2:24.1"
  int t1Index = message.indexOf("T1:");
  int t2Index = message.indexOf("T2:");
  int commaIndex = message.indexOf(',');

  if (t1Index != -1 && t2Index != -1) {
    String t1Str = message.substring(t1Index + 3, commaIndex);
    String t2Str = message.substring(t2Index + 3);
    
    temp1 = t1Str.toFloat();
    temp2 = t2Str.toFloat();
    lastUpdateTime = millis();
    lastStatus = "Data received: " + message;
    
    Serial.print("Temperature 1: ");
    Serial.print(temp1);
    Serial.print("°C, Temperature 2: ");
    Serial.print(temp2);
    Serial.println("°C");
  }
}

void loop() {
  // Check WiFi status
  if (wifi.isConnected()) {
    // You could send data to a server here if needed
  }

  // Check for incoming LoRa messages
  String received = lora.receive();
  if (received != "") {
    Serial.print("Received: ");
    Serial.println(received);
    
    if (received.startsWith("T1:")) {
      parseTemperatureData(received);
    }
  }

  // Send acknowledgment periodically
  static unsigned long lastSendTime = 0;
  if (millis() - lastSendTime > 10000) { // Every 10 seconds
    String ackMsg = "ACK:" + String(millis());
    lora.send(ackMsg);
    Serial.println("Sent: " + ackMsg);
    lastSendTime = millis();
  }

  // Display status
  if (millis() - lastUpdateTime > 30000) {
    lastStatus = "No recent data";
  }

  Serial.println(lastStatus);
  delay(1000);
}