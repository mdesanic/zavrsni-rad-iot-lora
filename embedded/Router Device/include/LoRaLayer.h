#ifndef LORALAYER_H
#define LORALAYER_H

#include <SPI.h>
#include <LoRa.h>

class LoRaLayer {
private:
    // Configuration variables
    uint8_t _nssPin;
    uint8_t _resetPin;
    uint8_t _dio0Pin;
    long _frequency;
    uint8_t _syncWord;
    int _txPower;
    
    // SPI pins (set during begin)
    uint8_t _sckPin;
    uint8_t _misoPin;
    uint8_t _mosiPin;

public:
    // Constructor (sets basic parameters)
    LoRaLayer(uint8_t nss, uint8_t rst, uint8_t dio0, 
             long freq = 433E6, uint8_t sync = 0xF3, int power = 20)
        : _nssPin(nss), _resetPin(rst), _dio0Pin(dio0),
          _frequency(freq), _syncWord(sync), _txPower(power) {}

    // Initialize LoRa with custom SPI pins
    bool begin(uint8_t sck, uint8_t miso, uint8_t mosi) {
        _sckPin = sck;
        _misoPin = miso;
        _mosiPin = mosi;
        
        SPI.begin(_sckPin, _misoPin, _mosiPin, _nssPin);
        LoRa.setPins(_nssPin, _resetPin, _dio0Pin);
        
        if (!LoRa.begin(_frequency)) {
            return false;
        }
        
        LoRa.setSyncWord(_syncWord);
        LoRa.setTxPower(_txPower);
        return true;
    }

    // Send string data
    bool send(const String &data) {
        LoRa.beginPacket();
        LoRa.print(data);
        return LoRa.endPacket();
    }

    String receive() {
        if (LoRa.parsePacket()) {
            String packet;
            while (LoRa.available()) {
                packet += (char)LoRa.read();
            }
            return packet;
        }
        return "";
    }

    bool sendACK() {
        LoRa.beginPacket();
        LoRa.print("ACK");
        return LoRa.endPacket();
    }
};
#endif