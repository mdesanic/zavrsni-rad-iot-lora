#ifndef SENSORLAYER_H
#define SENSORLAYER_H

#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>

class SensorLayer {
private:
    OneWire oneWire;
    DallasTemperature sensors;
    
    struct {
        DeviceAddress address;
        float lastTemp;
        bool connected;
    } sensor[2];
    
    bool initialized = false;
    String deviceMAC;

    // Known addresses fallback (from your previous working code)
    const DeviceAddress KNOWN_SENSOR1 = {0x28, 0x74, 0xFA, 0x84, 0x00, 0x00, 0x00, 0x96};
    const DeviceAddress KNOWN_SENSOR2 = {0x28, 0x4F, 0xE3, 0x84, 0x00, 0x00, 0x00, 0x0C};

public:
    SensorLayer(uint8_t pin) : oneWire(pin), sensors(&oneWire) {
        uint8_t mac[6];
        esp_read_mac(mac, ESP_MAC_WIFI_STA);
        char macStr[18];
        snprintf(macStr, sizeof(macStr), "%02X%02X%02X%02X%02X%02X", 
                mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
        deviceMAC = String(macStr);
    }

    bool begin() {
        sensors.begin();
        delay(100); // Short delay for bus stabilization

        // Try to auto-detect sensors first
        bool detectionSuccess = true;
        if (!sensors.getAddress(sensor[0].address, 0)) {
            Serial.println("Warning: Sensor 0 not detected, using fallback address");
            memcpy(sensor[0].address, KNOWN_SENSOR1, 8);
            detectionSuccess = false;
        }
        
        if (!sensors.getAddress(sensor[1].address, 1)) {
            Serial.println("Warning: Sensor 1 not detected, using fallback address");
            memcpy(sensor[1].address, KNOWN_SENSOR2, 8);
            detectionSuccess = false;
        }

        // Verify sensors respond
        for (int i = 0; i < 2; i++) {
            sensors.setResolution(sensor[i].address, 12);
            sensor[i].connected = (sensors.getTempC(sensor[i].address) != DEVICE_DISCONNECTED_C);
            if (!sensor[i].connected) {
                Serial.print("Warning: Sensor ");
                Serial.print(i);
                Serial.println(" not responding");
            }
        }

        initialized = true;
        return detectionSuccess;
    }

    void requestTemperatures() {
        if (initialized) {
            sensors.requestTemperatures();
            delay(750); // Required for 12-bit resolution
        }
    }

    bool readTemperatures() {
        if (!initialized) return false;
        
        bool allValid = true;
        for (int i = 0; i < 2; i++) {
            sensor[i].lastTemp = sensors.getTempC(sensor[i].address);
            if (sensor[i].lastTemp == DEVICE_DISCONNECTED_C) {
                sensor[i].connected = false;
                allValid = false;
            } else {
                sensor[i].connected = true;
            }
        }
        return allValid;
    }

    String getDeviceMAC() { return deviceMAC; }
    
    String getSensorAddress(uint8_t index) {
        if (index > 1) return "";
        char buf[17];
        sprintf(buf, "%02X%02X%02X%02X%02X%02X%02X%02X",
                sensor[index].address[0], sensor[index].address[1],
                sensor[index].address[2], sensor[index].address[3],
                sensor[index].address[4], sensor[index].address[5],
                sensor[index].address[6], sensor[index].address[7]);
        return String(buf);
    }

    float getTemperature(uint8_t index) {
        if (index > 1 || !sensor[index].connected) return NAN;
        return sensor[index].lastTemp;
    }

    String generateLoRaPayload() {
        String payload;
        payload.reserve(128);
        
        payload = "{\"mac\":\"" + deviceMAC + "\",\"s\":[";
        payload += "{\"a\":\"" + getSensorAddress(0) + "\",\"t\":";
        if (sensor[0].connected) payload += String(sensor[0].lastTemp, 2);
        else payload += "null";
        
        payload += "},{\"a\":\"" + getSensorAddress(1) + "\",\"t\":";
        if (sensor[1].connected) payload += String(sensor[1].lastTemp, 2);
        else payload += "null";
        
        payload += "}]}";
        
        return payload;
    }
};
#endif