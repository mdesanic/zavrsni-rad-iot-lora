import fetch from 'node-fetch';
import DatabaseDAO from './services/databaseDAO.js';

export default async function getLocations (request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    console.log(ddao);
    try {
        let locationsResponse = await ddao.getAllLocations();
        let locations = locationsResponse[0]; // Extract location data from the first element
        response.status(200);
        response.send(JSON.stringify(locations));
    }
    catch (error) {
        console.error('Greška prilikom dohvaćanja korisnika iz baze:', error);
        response.status(500).json({ greska: 'Greška prilikom dohvaćanja korisnika iz baze' });
    }
}

export { getLocations };
