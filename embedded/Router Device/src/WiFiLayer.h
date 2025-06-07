#ifndef WIFI_CONNECTION_H
#define WIFI_CONNECTION_H

#include <WiFi.h>

class WiFiConnection {
public:
    // Constructor (set WiFi credentials)
    WiFiConnection(const char* ssid, const char* password) 
        : _ssid(ssid), _password(password) {}

    // Connect to WiFi
    void connect() {
        Serial.println("Connecting to WiFi...");
        WiFi.begin(_ssid, _password);

        int retryCount = 0;
        while (WiFi.status() != WL_CONNECTED && retryCount < 20) {
            delay(500);
            Serial.print(".");
            retryCount++;
        }

        if (WiFi.status() == WL_CONNECTED) {
            Serial.println("\nWiFi connected!");
            printIP();
        } else {
            Serial.println("\nFailed to connect to WiFi.");
        }
    }

    // Check if connected
    bool isConnected() {
        return WiFi.status() == WL_CONNECTED;
    }

    // Print local IP (optional)
    void printIP() {
        Serial.print("IP address: ");
        Serial.println(WiFi.localIP());
    }

private:
    const char* _ssid;      // WiFi SSID
    const char* _password;  // WiFi Password
};

#endif