#include "WiFiLayer.h"
#include "LoRaLayer.h"  // Include LoRa library

const char* ssid = "Tele2_i_ovce_i_novce";
const char* password = "4ESN5aJ5Y5";

WiFiConnection wifi(ssid, password);
LoRaConnection lora;

void setup() {
    Serial.begin(115200);
    wifi.connect();  // Connect to WiFi
    lora.begin();    // Initialize LoRa (adjust pins if needed)
}

void loop() {
    // WiFi handling (existing code)
    if (wifi.isConnected()) {
        wifi.printIP();
        Serial.println("WiFi is connected.");
    } else {
        Serial.println("WiFi is not connected.");
    }

    // LoRa: Send a test message every 5 seconds
    static unsigned long lastSend = 0;
    if (millis() - lastSend > 5000) {
        lora.send("Hello from ESP32!");
        lastSend = millis();
    }

    // LoRa: Check for incoming messages
    String incoming = lora.receive();
    if (incoming != "") {
        Serial.println("LoRa received: " + incoming);
    }

    delay(1000);
}