import express from 'express';
import { getLocations } from './api/RESTapi.mjs'; // Assuming named export

const server = express();
const port = 3000;

function startServer() {
  server.use(express.json());

  server.get("/database/locations", getLocations); // Directly reference the function

  server.use((request, response) => {
    response.status(404);
    response.json({ desc: "no resources" });
  });

  server.listen(port, () => {
    console.log(`Server started on port: ${port}`);
  });
}

startServer();
