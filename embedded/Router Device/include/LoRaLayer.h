#ifndef LORALAYER_H
#define LORALAYER_H

#include <LoRa.h>

class LoRaConnection {
private:
    unsigned long _timeout = 5000; // 5 second timeout
public:
    void setTimeout(unsigned long timeout) {
        _timeout = timeout;
    }

    void begin() {
        // Heltec WiFi LoRa 32 V2 Default Pins
        LoRa.setPins(18, 14, 26);  // NSS, RST, DIO0
        if (!LoRa.begin(433E6)) {
            Serial.println("LoRa init failed!");
        }
        
        // Optimized settings for faster transmission
        LoRa.setSpreadingFactor(7);      // SF7 (fastest)
        LoRa.setSignalBandwidth(250E3);  // 250kHz (faster bandwidth)
        LoRa.setCodingRate4(5);          // 4/5 (less error correction)
        LoRa.setSyncWord(0xF3);
        LoRa.enableCrc();
        
        Serial.println("LoRa initialized with timeout support!");
    }

    bool send(String message) {
        LoRa.beginPacket();
        LoRa.print(message);
        int result = LoRa.endPacket(true); // true = async

        unsigned long start = millis();
        while (LoRa.beginPacket()) {
            if (millis() - start > _timeout) {
                Serial.println("LoRa send timeout!");
                LoRa.idle(); 
                return false;
            }
            delay(10);
        }
        return result == 1;
    }


    String receive() {
        if (LoRa.parsePacket()) {
            String incoming = "";
            while (LoRa.available()) {
                incoming += (char)LoRa.read();
            }
            return incoming;
        }
        return "";
    }
};

#endif