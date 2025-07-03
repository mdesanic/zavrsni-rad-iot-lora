#ifndef GATEWAYLAYER_H
#define GATEWAYLAYER_H

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

class GatewayLayer {
private:
    const char* ssid;
    const char* password;
    const String baseURL;
    String deviceMac; 

    void printJsonPayload(const DynamicJsonDocument& doc) {
        String payload;
        serializeJsonPretty(doc, payload);
        Serial.println("Sending JSON payload:");
        Serial.println(payload);
        Serial.println("---------------------");
    }

public:
    GatewayLayer(const char* wifiSSID, const char* wifiPass, String serverIP) 
        : ssid(wifiSSID), password(wifiPass), baseURL("http://" + serverIP + ":3000") {
        uint8_t mac[6];
        WiFi.macAddress(mac);
        char macStr[18] = {0};
        sprintf(macStr, "%02X:%02X:%02X:%02X:%02X:%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
        deviceMac = String(macStr);
    }

    bool begin() {
        WiFi.begin(ssid, password);
        Serial.print("Connecting to WiFi");
        
        int attempts = 0;
        while (WiFi.status() != WL_CONNECTED && attempts < 20) {
            delay(500);
            Serial.print(".");
            attempts++;
        }
        
        if (WiFi.status() != WL_CONNECTED) {
            Serial.println("\nWiFi FAILED");
            return false;
        }
        
        Serial.println("\nWiFi Connected");
        Serial.print("IP: ");
        Serial.println(WiFi.localIP());
        Serial.print("MAC: ");
        Serial.println(deviceMac);
        return true;
    }
    
    bool registerDevice() {
        if (WiFi.status() != WL_CONNECTED) return false;

        HTTPClient http;
        http.begin(baseURL + "/devices");
        http.addHeader("Content-Type", "application/json");

        DynamicJsonDocument doc(256);
        doc["mac"] = deviceMac;
        doc["type"] = "gateway";

        printJsonPayload(doc);

        String payload;
        serializeJson(doc, payload);

        int httpCode = http.POST(payload);
        http.end();
        return (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_CONFLICT);
    }

    bool sendSensorData(const String &sensorDeviceMac, const String &sensorAddress, float temp) {
        if (WiFi.status() != WL_CONNECTED) return false;

        HTTPClient http;
        http.begin(baseURL + "/readings");
        http.addHeader("Content-Type", "application/json");

        DynamicJsonDocument doc(512);
        doc["transmitter_mac"] = deviceMac;
        doc["sensor_device_mac"] = sensorDeviceMac;
        doc["sensor_address"] = sensorAddress;
        doc["temperature"] = temp;
        doc["timestamp"] = millis();

        printJsonPayload(doc);

        String payload;
        serializeJson(doc, payload);

        int httpCode = http.POST(payload);
        http.end();
        return (httpCode == HTTP_CODE_OK);
    }
};
#endif