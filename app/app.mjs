import express from 'express';

const server = express();
const port = 3000;

function startServer(){
	server.use(express.json());

	server.use((request, response) => {
		response.status(404);
		response.json({ desc: "no resources" });
	});

    server.listen(port, () => {
      console.log(`Server started on port: ${port}`);
   });
};

startServer();