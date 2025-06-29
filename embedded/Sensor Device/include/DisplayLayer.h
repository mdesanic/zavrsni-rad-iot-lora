#ifndef DISPLAYLAYER_H
#define DISPLAYLAYER_H

#include <Adafruit_SSD1306.h>
#include <Wire.h>

// OLED display size
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

class DisplayLayer {
private:
    Adafruit_SSD1306 display;
    unsigned long lastUpdate = 0;
    uint16_t updateInterval = 100; // Default update interval in ms
    
public:
    // Constructor with configurable pins
    DisplayLayer(uint8_t sda, uint8_t scl, uint8_t rst) 
        : display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, rst) {
        Wire.begin(sda, scl);
    }
    
    bool begin(uint8_t i2cAddress = 0x3C) {
        if(!display.begin(SSD1306_SWITCHCAPVCC, i2cAddress)) {
            return false;
        }
        display.setTextSize(1);
        display.setTextColor(SSD1306_WHITE);
        display.clearDisplay();
        display.display();
        return true;
    }
    
    void showSplash() {
        clear();
        setCursor(0,0);
        println("LoRa Transmitter");
        println("Initializing...");
        update(true); // Force immediate update
    }

    void setUpdateInterval(uint16_t interval) {
        updateInterval = interval;
    }

    void clear() {
        display.clearDisplay();
    }

    void setCursor(int16_t x, int16_t y) {
        display.setCursor(x, y);
    }

    template<typename T>
    void print(T text) {
        display.print(text);
    }

    template<typename T>
    void println(T text) {
        display.println(text);
    }

    bool update(bool force = false) {
        unsigned long now = millis();
        if (!force && (now - lastUpdate < updateInterval)) {
            return false;
        }
        display.display();
        lastUpdate = now;
        return true;
    }
};
#endif