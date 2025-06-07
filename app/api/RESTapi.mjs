import fetch from 'node-fetch';
import { DatabaseDAO } from './services/databaseDAO.js';

export default async function getAllLocations(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    console.log(ddao);
    try {
        let locationsResponse = await ddao.getAllLocations();
        let locations = locationsResponse[0]; // Extract location data from the first element
        response.status(200);
        response.send(JSON.stringify(locations));
    } catch (error) {
        console.error('Error while fetching users from the database:', error);
        response.status(500).json({ error: 'Error while fetching users from the database' });
    }
}

async function getAllRoutersByLocationId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const locationId = request.params.id; // Assuming the location ID is sent as a URL parameter
        const routers = await ddao.getAllRoutersByLocationId(locationId);
        response.status(200).json(routers);
    } catch (error) {
        console.error('Error while fetching routers:', error);
        response.status(500).json({ error: 'Error while fetching routers' });
    }
}

async function getAllSensorsByRouterId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const routerId = request.params.id; 
        console.log(routerId);// Assuming the router ID is sent as a URL parameter
        const sensors = await ddao.getAllSensorsByRouterId(routerId);
        response.status(200).json(sensors);
    } catch (error) {
        console.error('Error while fetching sensors:', error);
        response.status(500).json({ error: 'Error while fetching sensors' });
    }
}

async function getAllMeasurementsBySensorId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const sensorId = request.params.id; // Assuming the sensor ID is sent as a URL parameter
        const measurements = await ddao.getAllMeasurementsBySensorId(sensorId);
        response.status(200).json(measurements);
    } catch (error) {
        console.error('Error while fetching measurements:', error);
        response.status(500).json({ error: 'Error while fetching measurements' });
    }
}

async function getAllMeasurementsBySensorIdAndDate(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const sensorId = request.params.id; // Assuming the sensor ID is sent as a URL parameter
        const date = request.query.date; // Assuming the date is sent as a query parameter
        const measurements = await ddao.getAllMeasurementsBySensorIdAndDate(sensorId, date);
        response.status(200).json(measurements);
    } catch (error) {
        console.error('Error while fetching measurements by date:', error);
        response.status(500).json({ error: 'Error while fetching measurements by date' });
    }
}

async function getAllMeasurementsBySensorIdAndDateRange(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const sensorId = request.params.id; // Assuming the sensor ID is sent as a URL parameter
        const { startDate, endDate, limit, offset } = request.query; // Assuming these are sent as query parameters
        const measurements = await ddao.getAllMeasurementsBySensorIdAndDateRange(sensorId, startDate, endDate, limit, offset);
        response.status(200).json(measurements);
    } catch (error) {
        console.error('Error while fetching measurements by date range:', error);
        response.status(500).json({ error: 'Error while fetching measurements by date range' });
    }
}

async function getLatestMeasurementBySensorId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const sensorId = request.params.id; // Assuming the sensor ID is sent as a URL parameter
        const measurement = await ddao.getLatestMeasurementBySensorId(sensorId);
        response.status(200).json(measurement);
    } catch (error) {
        console.error('Error while fetching latest measurement:', error);
        response.status(500).json({ error: 'Error while fetching latest measurement' });
    }
}

async function getAllDataByLocationId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const locationId = request.params.id; // Assuming the location ID is sent as a URL parameter
        const data = await ddao.getAllDataByLocationId(locationId);
        response.status(200).json(data);
    } catch (error) {
        console.error('Error while fetching all data by location ID:', error);
        response.status(500).json({ error: 'Error while fetching all data by location ID' });
    }
}

async function getLatestMeasurementsByRouterId(request, response) {
    response.type("application/json");
    let ddao = new DatabaseDAO();
    try {
        const routerId = request.params.id; // Assuming the router ID is sent as a URL parameter
        const measurements = await ddao.getLatestMeasurementsByRouterId(routerId);
        response.status(200).json(measurements);
    } catch (error) {
        console.error('Error while fetching latest measurements by router ID:', error);
        response.status(500).json({ error: 'Error while fetching latest measurements by router ID' });
    }
}

export {
    getAllLocations,
    getAllRoutersByLocationId,
    getAllSensorsByRouterId,
    getAllMeasurementsBySensorId,
    getAllMeasurementsBySensorIdAndDate,
    getAllMeasurementsBySensorIdAndDateRange,
    getLatestMeasurementBySensorId,
    getAllDataByLocationId,
    getLatestMeasurementsByRouterId
};

