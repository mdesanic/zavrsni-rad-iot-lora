#include <Adafruit_SSD1306.h>

class DisplayLayer {
private:
  Adafruit_SSD1306 display;
  
public:
  DisplayLayer() : display(128, 64, &Wire, -1) {}
  
  bool init() {
    Wire.begin(21, 22); // SDA, SCL
    return display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  }
  
  void showSplash(const String &title) {
    display.clearDisplay();
    display.setTextSize(1);
    display.setTextColor(SSD1306_WHITE);
    display.setCursor(0, 0);
    display.println(title);
    display.display();
    delay(1000);
  }
  
  void update(const String lines[], uint8_t count) {
    display.clearDisplay();
    display.setCursor(0, 0);
    for (uint8_t i = 0; i < count; i++) {
      display.println(lines[i]);
    }
    display.display();
  }
  
  void showError(const String &message) {
    display.clearDisplay();
    display.setCursor(0, 0);
    display.println("ERROR:");
    display.println(message);
    display.display();
  }
};