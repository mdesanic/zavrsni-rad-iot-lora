import express from 'express';
import {
  getAllLocations,
  getTransmitterDetails,
  getSensorDeviceReport,
  registerDevice,
  handleReading,
  createLocation,
  updateLocation,
  updateTransmitter,
  updateSensor,
  updateSensorDevice,
  getTransmittersByLocation,
  getLocationById,
  getTransmittersWithoutLocation,
  getLatestReadingsForSensorDevice
} from './api/RESTapi.mjs';

const server = express();
const port = 3000;

function startServer() {
    server.use(express.json());

    server.get("/locations", getAllLocations);
    server.get("/locations/:id/transmitters", getTransmittersByLocation);
    server.get("/locations/:id", getLocationById);
    server.get("/transmitters/:id", getTransmitterDetails);
    server.get("/sensor-devices/:id/grouped-readings", getSensorDeviceReport); 
    server.get("/transmitters", getTransmittersWithoutLocation);
    server.get("/sensor-devices/:id/latest-readings", getLatestReadingsForSensorDevice);
    server.post("/devices", registerDevice);
    server.post("/readings", handleReading);
    server.post("/location", createLocation);
    server.patch("/transmitters/:id", updateTransmitter);
    server.patch("/locations/:id", updateLocation);
    server.patch("/sensors/:id", updateSensor);
    server.patch("/sensor-devices/:id", updateSensorDevice);

    server.use((request, response) => {
      response.status(404).json({ desc: "no resources" });
    });

    server.listen(port, () => {
      console.log(`Server started on port: ${port}`);
      console.log(`Available endpoints:`);
      console.log(`GET /locations - Fetch all locations`);
      console.log(`GET /transmitters/:id - Fetch full transmitter details`);
      console.log(`GET /reports/sensor-device/:id?from=...&to=... - Historical grouped readings`);
      console.log(`POST /devices - Register device`);
      console.log(`POST /readings - Store a temperature reading`);
      console.log(`POST /location - Create a new location`);
      console.log(`PATCH /transmitters/:id - Update transmitter`);
      console.log(`PATCH /locations/:id - Update location`);
      console.log(`PATCH /sensors/:id - Rename or update sensor depth`);
      console.log(`PATCH /sensor-devices/:id - Rename sensor device`);
    });
}

startServer();