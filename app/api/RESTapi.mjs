import fetch from 'node-fetch';
import { DatabaseDAO } from './services/databaseDAO.js';

export async function registerDevice(request, response) {
    response.type("application/json");
    const { mac } = request.body;
    const ddao = new DatabaseDAO();

    try {
        const result = await ddao.registerDevice(mac);

        if (result.exists) {
            response.status(200).json({
                message: "Device already registered",
                deviceId: result.deviceId
            });
        } else {
            response.status(201).json({
                message: "Device registered",
                deviceId: result.deviceId
            });
        }
    } catch (error) {
        console.error('Device registration failed:', error);
        response.status(500).json({ error: 'Device registration failed' });
    }
}

export async function handleReading(request, response) {
    response.type("application/json");
    const ddao = new DatabaseDAO();

    try {
        const { transmitter_mac, sensor_device_mac, sensor_address, temperature } = request.body;

        if (!transmitter_mac || !sensor_device_mac || !sensor_address || temperature === undefined) {
            return response.status(400).json({
                error: 'Missing required fields',
                required: ['transmitter_mac', 'sensor_device_mac', 'sensor_address', 'temperature']
            });
        }

        const readingId = await ddao.storeReading(
            transmitter_mac,
            sensor_device_mac,
            sensor_address,
            temperature
        );

        response.status(201).json({
            success: true,
            readingId
        });
    } catch (error) {
        console.error('Reading processing failed:', error);
        response.status(500).json({
            error: 'Reading processing failed',
            details: error.message
        });
    }
}

export async function getAllLocations(req, res) {
    res.type("application/json");
    const ddao = new DatabaseDAO();

    try {
        const locations = await ddao.getAllLocations();
        res.status(200).json({ locations });
    } catch (error) {
        console.error('Failed to fetch locations:', error);
        res.status(500).json({ error: 'Could not retrieve locations' });
    }
}

export async function createLocation(req, res) {
    res.type("application/json");
    const { name, description } = req.body;
    const ddao = new DatabaseDAO();

    if (!name) {
        return res.status(400).json({ error: 'Missing required field: name' });
    }

    try {
        const result = await ddao.createLocation(name, description);

        if (result.exists) {
            return res.status(200).json({
                message: 'Location already exists',
                locationId: result.locationId
            });
        }

        res.status(201).json({ message: 'Location created', locationId: result.insertId });

    } catch (error) {
        console.error('createLocation failed:', error.message);
        res.status(500).json({ error: 'Database error during location creation' });
    }
}

export async function updateLocation(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const updates = req.body;
    const ddao = new DatabaseDAO();

    if (!updates.name && !updates.description) {
        return res.status(400).json({ error: 'No valid fields provided to update' });
    }

    try {
        await ddao.updateLocation(id, updates);
        res.status(200).json({ message: 'Location updated' });
    } catch (error) {
        console.error('Failed to update location:', error);
        res.status(500).json({ error: 'Could not update location' });
    }
}

export async function getTransmitterDetails(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const ddao = new DatabaseDAO();

    try {
        const details = await ddao.getTransmitterDetails(id);
        if (!details) {
            return res.status(404).json({ error: 'Transmitter not found' });
        }
        res.status(200).json(details);
    } catch (error) {
        console.error('getTransmitterDetails failed:', error.message);
        res.status(500).json({ error: 'Failed to retrieve transmitter details' });
    }
}

export async function updateTransmitter(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const updates = req.body;
    const ddao = new DatabaseDAO();

    if (!updates.name && updates.location_id === undefined) {
        return res.status(400).json({ error: 'No valid fields provided to update' });
    }

    try {
        await ddao.updateTransmitter(id, updates);
        res.status(200).json({ message: 'Transmitter updated' });
    } catch (error) {
        console.error('updateTransmitter failed:', error.message);
        res.status(500).json({ error: 'Could not update transmitter' });
    }
}

export async function updateSensorDevice(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const updates = req.body;
    const ddao = new DatabaseDAO();

    if (!updates.name) {
        return res.status(400).json({ error: 'Missing required field: name' });
    }

    try {
        await ddao.updateSensorDevice(id, updates);
        res.status(200).json({ message: 'Sensor device updated' });
    } catch (error) {
        console.error('Failed to update sensor device:', error);
        res.status(500).json({ error: 'Could not update sensor device' });
    }
}

export async function updateSensor(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const updates = req.body;
    const ddao = new DatabaseDAO();

    if (!updates.name && updates.depth_meters === undefined) {
        return res.status(400).json({ error: 'No valid fields provided to update' });
    }

    try {
        await ddao.updateSensor(id, updates);
        res.status(200).json({ message: 'Sensor updated' });
    } catch (error) {
        console.error('Failed to update sensor:', error);
        res.status(500).json({ error: 'Could not update sensor' });
    }
}

export async function getSensorDeviceReport(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const { from, to } = req.query;
    const ddao = new DatabaseDAO();

    if (!from || !to) {
        return res.status(400).json({ error: 'Missing required query parameters: from, to' });
    }

    try {
        const report = await ddao.getGroupedReadings(id, from, to);
        if (!report || report.length === 0) {
            return res.status(404).json({ message: 'No readings found in time range' });
        }
        res.status(200).json({ report });
    } catch (error) {
        console.error('getSensorDeviceReport failed:', error.message);
        res.status(500).json({ error: 'Could not fetch sensor report' });
    }
}

export async function getTransmittersByLocation(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const ddao = new DatabaseDAO();

    if (!id) {
        return res.status(400).json({ error: 'Missing required parameter: id' });
    }

    try {
        const transmitters = await ddao.getTransmittersByLocation(id);
        res.status(200).json({ transmitters });
    } catch (error) {
        console.error('Failed to fetch transmitters by location:', error);
        res.status(500).json({ error: 'Could not retrieve transmitters for location' });
    }
}

export async function getLocationById(req, res) {
    res.type("application/json");
    const { id } = req.params;
    const ddao = new DatabaseDAO();

    if (!id) {
        return res.status(400).json({ error: 'Missing required parameter: id' });
    }

    try {
        const location = await ddao.getLocationById(id);
        if (!location) {
            return res.status(404).json({ error: 'Location not found' });
        }
        res.status(200).json({ location });
    } catch (error) {
        console.error('Failed to fetch location by id:', error);
        res.status(500).json({ error: 'Could not retrieve location' });
    }
}

export async function getTransmittersWithoutLocation(req, res) {
    res.type("application/json");
    const ddao = new DatabaseDAO();

    try {
        const transmitters = await ddao.getTransmittersWithoutLocation();

        if (!transmitters || transmitters.length === 0) {
            return res.status(200).json([]);
        }

        res.status(200).json(transmitters);
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({
            error: 'Database error',
            details: error.message
        });
    }
}

export async function getLatestReadingsForSensorDevice(request, response) {
    response.type("application/json");
    const ddao = new DatabaseDAO();
    try {
        const sensorDeviceId = request.params.id;
        const result = await ddao.getLatestReadingsForSensorDevice(sensorDeviceId);
        if (result.length === 0) {
            response.status(200).json({ message: "No sensors found for this sensor device.", sensors: [] });
        } else {
            response.status(200).json({ sensors: result });
        }
    } catch (error) {
        console.error('Failed to get latest readings for sensor device:', error);
        response.status(500).json({ message: "Error retrieving latest readings." });
    }
}