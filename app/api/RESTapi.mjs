import fetch from 'node-fetch';
import DatabaseDAO from './services/databaseDAO.js';

export default async function getLocations (request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    console.log(ddao);
    try {
        let locations = await ddao.getAllLocations();
        response.status(200);
        response.send(JSON.stringify(locations));
    }
    catch (error) {
        console.error('Greška prilikom dohvaćanja korisnika iz baze:', error);
        response.status(500).json({ greska: 'Greška prilikom dohvaćanja korisnika iz baze' });
    }
}

export { getLocations };
