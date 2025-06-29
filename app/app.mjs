import express from 'express';
import {
getAllLocations,
.
.
.
} from './api/RESTapi.mjs';

const server = express();
const port = 3000;

function startServer() {
  server.use(express.json());

  // ✅ First define your actual routes
  server.get("/locations", getAllLocations);
  .
  .
  .

  // ❗ Put 404 handler **after** all route definitions
  server.use((request, response) => {
    response.status(404).json({ desc: "no resources" });
  });

  server.listen(port, () => {
    console.log(`Server started on port: ${port}`);
    console.log(`Available endpoints:`);
    console.log(`GET /locations - Fetch all locations`);
    .
    .
    .
  });
}


startServer();
