#!/bin/bash

BASE_URL="http://localhost:3000"

echo "GET all locations"
curl -s -X GET "$BASE_URL/locations" 
echo -e "\n----------------------\n"

echo "GET routers by location ID = 1"
curl -s -X GET "$BASE_URL/routers/1" 
echo -e "\n----------------------\n"

echo "GET sensors by router ID = 1"
curl -s -X GET "$BASE_URL/sensors/1"
echo -e "\n----------------------\n"

echo "GET all measurements by sensor ID = 1"
curl -s -X GET "$BASE_URL/measurements/1" 
echo -e "\n----------------------\n"

echo "GET measurements by sensor ID = 1 on date 2023-10-01"
curl -s -X GET "$BASE_URL/measurements/1/date?date=2023-10-01" 
echo -e "\n----------------------\n"

echo "GET measurements by sensor ID = 1 date range 2023-10-01 to 2023-10-10"
curl -s -X GET "$BASE_URL/measurements/1/date-range?start=2023-10-01&end=2023-10-10" 
echo -e "\n----------------------\n"

echo "GET latest measurement by sensor ID = 1"
curl -s -X GET "$BASE_URL/measurements/1/latest" 
echo -e "\n----------------------\n"

echo "GET all data by location ID = 1"
curl -s -X GET "$BASE_URL/data/1" 
echo -e "\n----------------------\n"

echo "GET latest measurements by router ID = 1"
curl -s -X GET "$BASE_URL/latest-measurements/1"
echo -e "\n----------------------
