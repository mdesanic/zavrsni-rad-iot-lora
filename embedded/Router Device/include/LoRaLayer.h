#include <LoRa.h>

class LoRaConnection {
public:
    void begin() {
        // TTGO T3 V2.1 Default Pins
        LoRa.setPins(18, 14, 26);  // NSS, RST, DIO0
        if (!LoRa.begin(433E6)) {   // 915 MHz (adjust to 868E6 if needed)
            Serial.println("LoRa init failed!");
            while (1);
        }
        Serial.println("LoRa initialized!");
    }

    void send(String message) {
        LoRa.beginPacket();
        LoRa.print(message);
        LoRa.endPacket();
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