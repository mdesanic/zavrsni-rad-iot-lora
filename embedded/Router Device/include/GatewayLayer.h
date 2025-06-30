#ifndef GATEWAYLAYER_H
#define GATEWAYLAYER_H

#include <WiFi.h>
#include <HTTPClient.h>

class GatewayLayer {
private:
    const char* ssid;
    const char* password;
    const String serverURL;

public:
    GatewayLayer(const char* wifiSSID, const char* wifiPass, String serverIP) 
        : ssid(wifiSSID), password(wifiPass), serverURL("http://" + serverIP + ":8080/data") {}

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
        return true;
    }

    bool sendToServer(const String& jsonData) {
        if (WiFi.status() != WL_CONNECTED) return false;

        HTTPClient http;
        http.begin(serverURL);
        http.addHeader("Content-Type", "application/json");

        int httpCode = http.POST(jsonData);
        bool success = (httpCode == HTTP_CODE_OK);
        
        if (!success) {
            Serial.printf("HTTP Error: %d\n", httpCode);
        }

        http.end();
        return success;
    }
};
#endif