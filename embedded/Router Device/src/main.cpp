#include <GatewayLayer.h>
#include <DisplayLayer.h>
#include <LoRaLayer.h> 
#include <ArduinoJson.h> 

#define OLED_SDA     22
#define OLED_SCL     21
#define OLED_RST     4

const char* WIFI_SSID = "test_environment";
const char* WIFI_PASS = "nijemidobro";
const String SERVER_IP = "172.20.10.11";

#define LORA_SCK     5
#define LORA_MISO   19
#define LORA_MOSI   27
#define LORA_NSS    18
#define LORA_RST    14
#define LORA_DIO0   26
#define LORA_BAND 433E6

DisplayLayer display(OLED_SDA, OLED_SCL, OLED_RST);
GatewayLayer gateway(WIFI_SSID, WIFI_PASS, SERVER_IP);
LoRaLayer loraReceiver(LORA_NSS, LORA_RST, LORA_DIO0, LORA_BAND);

void displayError(const String &msg) {
  display.clear();
  display.setCursor(0, 0);
  display.println("ERROR:");
  display.println(msg);
  display.update(true);
  delay(5000); 
}

void displayStatus(const String &msg) {
  display.clear();
  display.setCursor(0, 0);
  display.println(msg);
  display.update(true);
}

void setup() {
  Serial.begin(115200);
  
  if (!display.begin()) {
    Serial.println("Display init failed!");
    while(1);
  }
  display.showSplash();
  displayStatus("Initializing...");

  if (!gateway.begin()) {
    displayError("WiFi FAILED");
  }

  if (!gateway.registerDevice()) {
    displayError("Gateway registration failed");
  }

  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_NSS);
  if (!loraReceiver.begin(LORA_SCK, LORA_MISO, LORA_MOSI)) {
    displayError("LoRa Receiver FAILED!");
  }

  displayStatus("Gateway Ready");
  delay(2000);
}

void loop() {
  String loRaPayload = loraReceiver.receive();

  if (loRaPayload.length() > 0) {
    DynamicJsonDocument doc(512);
    DeserializationError error = deserializeJson(doc, loRaPayload);

    if (error) {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      displayError("JSON Parse Err");
      delay(2000);
      return;
    }

    String sensorDeviceMac = doc["mac"].as<String>();
    JsonArray sensorsArray = doc["s"].as<JsonArray>();

    if (sensorDeviceMac.length() > 0 && !sensorsArray.isNull()) {
      display.clear();
      display.setCursor(0, 0);
      display.println("Data Received:");
      display.println("MAC: " + sensorDeviceMac);
      display.update(true);
      delay(1000);

      for (JsonObject sensorData : sensorsArray) {
        String sensorAddress = sensorData["a"].as<String>();
        float temperature = sensorData["t"].as<float>();

        if (!isnan(temperature)) {
            Serial.printf("Sending Data: MAC=%s, Addr=%s, Temp=%.2f\n",
                          sensorDeviceMac.c_str(), sensorAddress.c_str(), temperature);
            
            if (!gateway.sendSensorData(sensorDeviceMac, sensorAddress, temperature)) {
              displayError("HTTP Send Failed");
              delay(2000);
            } else {
              Serial.println("Data sent successfully to backend.");
              displayStatus("Data Sent OK");
            }
        } else {
            Serial.printf("Sensor %s on device %s has null/invalid temperature. Not sending.\n",
                          sensorAddress.c_str(), sensorDeviceMac.c_str());
            displayStatus("Invalid Temp Skipped");
        }
        delay(500);
      }
    } else {
      Serial.println("Invalid LoRa payload structure.");
      displayError("Invalid LoRa");
      delay(2000);
    }
  }

  delay(100); 
}