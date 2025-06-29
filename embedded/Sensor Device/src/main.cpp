#include <LoRaLayer.h>
#include <DisplayLayer.h>
#include <SensorLayer.h>

// Pin definitions
#define ONE_WIRE_PIN 23 
#define LORA_SCK     5
#define LORA_MISO   19
#define LORA_MOSI   27
#define LORA_NSS    18
#define LORA_RST    14
#define LORA_DIO0   26
#define LORA_BAND 433E6
#define OLED_SDA     4
#define OLED_SCL    15
#define OLED_RST    16

DisplayLayer display(OLED_SDA, OLED_SCL, OLED_RST);
SensorLayer tempSensors(ONE_WIRE_PIN);
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
  while(!Serial); // Wait for Serial
  
  // Initialize display
  if (!display.begin()) {
    Serial.println("Display init failed!");
    while(1);
  }
  display.showSplash();
  
  // Show device MAC on splash screen
  display.println("MAC:");
  display.println(tempSensors.getDeviceMAC());
  display.update(true);
  delay(2000);

  // Initialize LoRa
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_NSS);
  if (!lora.begin(LORA_SCK, LORA_MISO, LORA_MOSI)) {
    displayError("LoRa FAIL!");
  }

  // Initialize sensors
  if (!tempSensors.begin()) {
    displayError("Sensor FAIL!");
  }

  // Show ready message with sensor addresses
  display.clear();
  display.println("System Ready");
  display.println("Sensors:");
  display.println(tempSensors.getSensorAddress(0));
  display.println(tempSensors.getSensorAddress(1));
  display.update(true);
  delay(2000);
}

void loop() {
  // 1. Read sensors
  tempSensors.requestTemperatures();
  delay(750); // Critical for 12-bit conversion
  
  bool readingsValid = tempSensors.readTemperatures();
  
  // 2. Prepare display
  display.clear();
  display.setCursor(0, 0);
  
  if (!readingsValid) {
    display.println("Sensor Error!");
    display.update(true);
    delay(5000);
    return;
  }

  // 3. Show temperatures on display
  Serial.printf("T1: %.2f C\n", tempSensors.getTemperature(0));
  Serial.printf("T2: %.2f C\n", tempSensors.getTemperature(1));
  display.update();

  // 4. Prepare and send LoRa payload
  String payload = tempSensors.generateLoRaPayload();
  Serial.println("Sending: " + payload);
  
  if (lora.send(payload)) {
    display.println("Send OK");
    display.update();
    
    // Wait for ACK
    String ack = lora.receive(2000);
    if (ack.length() > 0) {
      display.println("ACK Received");
    } else {
      display.println("No ACK");
    }
  } else {
    display.println("Send FAILED");
  }
  display.update(true);
  
  delay(10000); // Send every 10 seconds
}

