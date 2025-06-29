#include <SPI.h>
#include <LoRa.h>

// LilyGO TTGO T3 v1.6.1 pins:
#define LORA_SCK    5    // GPIO5  - SCK
#define LORA_MISO  19    // GPIO19 - MISO
#define LORA_MOSI  27    // GPIO27 - MOSI
#define LORA_NSS   18    // GPIO18 - CS
#define LORA_RST   23    // GPIO23 - RESET (DIFFERENT!)
#define LORA_DIO0  26    // GPIO26 - IRQ

#define BAND 433E6

void setup() {
  Serial.begin(115200);
  
  // Special LilyGO SPI initialization
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_NSS);
  LoRa.setPins(LORA_NSS, LORA_RST, LORA_DIO0);

  if (!LoRa.begin(BAND)) {
    Serial.println("LilyGO LoRa init failed!");
    while(1);
  }
  
  LoRa.setSyncWord(0xF3); // Must match transmitter!
  LoRa.receive(); // Continuous receive mode
  Serial.println("LilyGO Receiver Ready");
}

void loop() {
  if (LoRa.parsePacket()) {
    Serial.print("Received: ");
    while(LoRa.available()) {
      Serial.print((char)LoRa.read());
    }
    Serial.println();
  }
  delay(10); // Prevent watchdog triggers
}