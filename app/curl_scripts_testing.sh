#!/bin/bash

BASE_URL=http://localhost:3000

echo "🌐 1. Create Location"
curl -s -X POST "$BASE_URL/location" -H "Content-Type: application/json" -d '{
  "name": "Lake Alpha",
  "description": "Test lake environment"
}'
echo -e "\n"

echo "📡 2. Register Transmitter"
curl -s -X POST "$BASE_URL/devices" -H "Content-Type: application/json" -d '{
  "mac": "E8:6B:EA:24:44:B8"
}'
echo -e "\n"

echo "🌡️ 3. Store Temperature Reading"
curl -s -X POST "$BASE_URL/readings" -H "Content-Type: application/json" -d '{
  "transmitter_mac": "E8:6B:EA:24:44:B8",
  "sensor_device_mac": "D4:E5:F6:A7:B8:C9",
  "sensor_address": "2874FA8400000096",
  "temperature": 22.5
}'
echo -e "\n"

echo "📍 4. Get All Locations"
curl -s "$BASE_URL/locations"
echo -e "\n"

echo "🔎 5. Get Transmitter Details (id=1)"
curl -s "$BASE_URL/transmitters/1"
echo -e "\n"

echo "📈 6. Get Sensor Report (deviceId=1) with sample date range"
curl -s "$BASE_URL/reports/sensor-device/1?from=2025-07-02T07:04:00&to=2025-07-02T07:06:00"
echo -e "\n"

echo "✏️ 7. Update Sensor (id=1)"
curl -s -X PATCH "$BASE_URL/sensor/1" -H "Content-Type: application/json" -d '{
  "name": "Surface Sensor",
  "depth": -0.5
}'
echo -e "\n"

echo "✏️ 8. Rename SensorDevice (id=1)"
curl -s -X PATCH "$BASE_URL/sensorDevice/1" -H "Content-Type: application/json" -d '{
  "name": "Device_A_Updated"
}'
echo -e "\n"

echo "✏️ 9. Rename Router (id=1)"
curl -s -X PATCH "$BASE_URL/router/1" -H "Content-Type: application/json" -d '{
  "name": "Main Gateway"
}'
echo -e "\n"
