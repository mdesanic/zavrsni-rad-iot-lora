#include <Arduino.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// Data wire is plugged into port 18 (GPIO18)
#define ONE_WIRE_BUS 18

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

// Device address holders
DeviceAddress sensor_a = { 0x28, 0x74, 0xFA, 0x84, 0x00, 0x00, 0x00, 0x96 }; // Example address for sensor A
DeviceAddress sensor_b = { 0x28, 0x4F, 0xE3, 0x84, 0x00, 0x00, 0x00, 0x0C };

// Function prototype for printAddress
/*void printAddress(DeviceAddress deviceAddress) {
  for (uint8_t i = 0; i < 8; i++) {
    if (deviceAddress[i] < 16) Serial.print("0");
    Serial.print(deviceAddress[i], HEX);
  }
  Serial.println();
}*/

void setup() {
  Serial.begin(9600);
  sensors.begin();

  int deviceCount = sensors.getDeviceCount();
  /*Serial.print("Found ");
  Serial.print(deviceCount);
  Serial.println(" devices.");

  DeviceAddress tempAddress;
  for (uint8_t i = 0; i < deviceCount; i++) {
    if (sensors.getAddress(tempAddress, i)) {
      Serial.print("Sensor ");
      Serial.print(i);
      Serial.print(" Address: ");
      printAddress(tempAddress);
    }
  }*/
}

void loop(void) {
  sensors.requestTemperatures();

  float temp1 = sensors.getTempC(sensor_a);
  float temp2 = sensors.getTempC(sensor_b);

  if (temp1 != DEVICE_DISCONNECTED_C) {
    Serial.print("Sensor 1: ");
    Serial.println(temp1);
  } else {
    Serial.println("Sensor 1 disconnected");
  }

  if (temp2 != DEVICE_DISCONNECTED_C) {
    Serial.print("Sensor 2: ");
    Serial.println(temp2);
  } else {
    Serial.println("Sensor 2 disconnected");
  }

  delay(1000);
};
