import express from 'express';
import {
getAllLocations,
getAllRoutersByLocationId,
getAllSensorsByRouterId,
getAllMeasurementsBySensorId,
getAllMeasurementsBySensorIdAndDate,
getAllMeasurementsBySensorIdAndDateRange,
getLatestMeasurementBySensorId,
getAllDataByLocationId,
getLatestMeasurementsByRouterId
} from './api/RESTapi.mjs';

const server = express();
const port = 3000;

function startServer() {
  server.use(express.json());

  // ✅ First define your actual routes
  server.get("/locations", getAllLocations);
  server.get("/routers/:id", getAllRoutersByLocationId);
  server.get("/sensors/:id", getAllSensorsByRouterId);
  server.get("/measurements/:id", getAllMeasurementsBySensorId);
  server.get("/measurements/:id/date", getAllMeasurementsBySensorIdAndDate);
  server.get("/measurements/:id/date-range", getAllMeasurementsBySensorIdAndDateRange);
  server.get("/measurements/:id/latest", getLatestMeasurementBySensorId);
  server.get("/data/:id", getAllDataByLocationId);
  server.get("/latest-measurements/:id", getLatestMeasurementsByRouterId);

  // ❗ Put 404 handler **after** all route definitions
  server.use((request, response) => {
    response.status(404).json({ desc: "no resources" });
  });

  server.listen(port, () => {
    console.log(`Server started on port: ${port}`);
    console.log(`Available endpoints:`);
    console.log(`GET /locations - Fetch all locations`);
    console.log(`GET /routers/:id - Fetch all routers by location ID`);
    console.log(`GET /sensors/:id - Fetch all sensors by router ID`);
    console.log(`GET /measurements/:id - Fetch all measurements by sensor ID`);
    console.log(`GET /measurements/:id/date - Fetch measurements by sensor ID and date`);
    console.log(`GET /measurements/:id/date-range - Fetch measurements by sensor ID and date range`);
    console.log(`GET /measurements/:id/latest - Fetch latest measurement by sensor ID`);
    console.log(`GET /data/:id - Fetch all data by location ID`);
    console.log(`GET /latest-measurements/:id - Fetch latest measurements by router ID`);
  });
}


startServer();
