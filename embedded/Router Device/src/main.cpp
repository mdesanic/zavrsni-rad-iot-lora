#include <GatewayLayer.h>
#include <DisplayLayer.h>
#include <LoRaLayer.h>

// LilyGO TTGO T3 v1.6.1 pins
#define LORA_SCK    5
#define LORA_MISO  19
#define LORA_MOSI  27
#define LORA_NSS   18
#define LORA_RST   23  // Different from sender!
#define LORA_DIO0  26
#define LORA_BAND 433E6
#define OLED_SDA    22
#define OLED_SCL    21
#define OLED_RST    4

const char* WIFI_SSID = "Tele2_i_ovce_i_novce";
const char* WIFI_PASS = "4ESN5aJ5Y5";
const String SERVER_IP = "192.168.1.66"; 

DisplayLayer display(OLED_SDA, OLED_SCL, OLED_RST);
GatewayLayer gateway(WIFI_SSID, WIFI_PASS, SERVER_IP);
LoRaLayer lora(LORA_NSS, LORA_RST, LORA_DIO0, LORA_BAND);

void displayError(const String &msg) {
  display.clear();
  display.setCursor(0, 0);
  display.println("ERROR:");
  display.println(msg);
  display.update(true);
  while(1); // Halt on error
}

void setup() {
  Serial.begin(115200);
  
  // Initialize LoRa (using same layer as transmitter)
  if (!lora.begin(LORA_SCK, LORA_MISO, LORA_MOSI)) {
    Serial.println("LoRa init failed!");
    while(1);
  }
  Serial.println("LoRa Receiver Ready");

  // Initialize WiFi
  if (!gateway.begin()) {
    Serial.println("WiFi FAILED");
    while(1);
  }
}

void loop() {
  // 1. Receive LoRa packet
  String packet = lora.receive();
  
  if (packet != "") {
    Serial.println("Received: " + packet);
    
    // 2. Forward to server
    if (gateway.sendToServer(packet)) {
      Serial.println("Forwarded to server");
      
      // 3. Send ACK back
      if (lora.sendACK()) {
        Serial.println("ACK sent");
      }
    }
  }
  
  delay(10);
}