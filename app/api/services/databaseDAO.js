import { Database } from "../modules/database.js";


class DatabaseDAO {
    constructor() {
        this.database = new Database();
    }

    /**
     * Retrieves all locations from the database.
     * @returns {Promise<Array>} A promise that resolves to an array of location objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllLocations() {
        try {
            await this.database.connect();
            const sql = "SELECT * FROM Locations;";
            const data = await this.database.runQuery(sql);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllLocations:', error);
            throw error;
        }
    }

    /**
     * Retrieves all routers associated with a specific location ID.
     * @param {number} locationId - The ID of the location.
     * @returns {Promise<Array>} A promise that resolves to an array of router objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllRoutersByLocationId(locationId) {
        try {
            await this.database.connect();
            const sql = "SELECT * FROM Routers WHERE location_id = ?;";
            const data = await this.database.runQuery(sql, [locationId]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllRoutersByLocationId:', error);
            throw error;
        }
    }

    /**
     * Retrieves all sensors associated with a specific router ID.
     * @param {number} routerId - The ID of the router.
     * @returns {Promise<Array>} A promise that resolves to an array of sensor objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllSensorsByRouterId(routerId) {
        try {
            await this.database.connect();
            const sql = `
                SELECT s.* FROM Sensors s
                JOIN SensorDevices sd ON s.sensor_device_id = sd.id
                WHERE sd.router_id = ?;
            `;
            const data = await this.database.runQuery(sql, [routerId]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllSensorsByRouterId:', error);
            throw error;
        }
    }


    /**
     * Retrieves all measurements associated with a specific sensor ID.
     * @param {number} sensorId - The ID of the sensor.
     * @returns {Promise<Array>} A promise that resolves to an array of measurement objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllMeasurementsBySensorId(sensorId) {
        try {
            await this.database.connect();
            const sql = "SELECT * FROM TemperatureReadings WHERE sensor_id = ?;";
            const data = await this.database.runQuery(sql, [sensorId]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllMeasurementsBySensorId:', error);
            throw error;
        }
    }


    /**
     * Retrieves all measurements for a specific sensor ID on a specific date.
     * @param {number} sensorId - The ID of the sensor.
     * @param {string} date - The date in 'YYYY-MM-DD' format.
     * @returns {Promise<Array>} A promise that resolves to an array of measurement objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllMeasurementsBySensorIdAndDate(sensorId, date) {
        try {
            await this.database.connect();
            const sql = `
                SELECT * FROM TemperatureReadings
                WHERE sensor_id = ? AND DATE(recorded_at) = ?;
            `;
            const data = await this.database.runQuery(sql, [sensorId, date]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllMeasurementsBySensorIdAndDate:', error);
            throw error;
        }
    }


    /**
     * Retrieves all measurements for a specific sensor ID within a date range, with pagination.
     * @param {number} sensorId - The ID of the sensor.
     * @param {string} startDate - The start date in 'YYYY-MM-DD' format.
     * @param {string} endDate - The end date in 'YYYY-MM-DD' format.
     * @param {number} [limit=10] - The maximum number of results to retrieve.
     * @param {number} [offset=0] - The number of results to skip.
     * @returns {Promise<Array>} A promise that resolves to an array of measurement objects.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllMeasurementsBySensorIdAndDateRange(sensorId, startDate, endDate, limit = 10, offset = 0) {
        try {
            await this.database.connect();
            const sql = `
                SELECT * FROM TemperatureReadings
                WHERE sensor_id = ? AND recorded_at BETWEEN ? AND ?
                ORDER BY recorded_at ASC
                LIMIT ? OFFSET ?;
            `;
            const data = await this.database.runQuery(sql, [sensorId, startDate, endDate, limit, offset]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllMeasurementsBySensorIdAndDateRange:', error);
            throw error;
        }
    }


    /**
     * Retrieves the latest measurement for a specific sensor ID.
     * @param {number} sensorId - The ID of the sensor.
     * @returns {Promise<Object>} A promise that resolves to the latest measurement object.
     * @throws {Error} Throws an error if the query fails.
     */
    async getLatestMeasurementBySensorId(sensorId) {
        try {
            await this.database.connect();
            const sql = `
                SELECT * FROM TemperatureReadings
                WHERE sensor_id = ?
                ORDER BY recorded_at DESC
                LIMIT 1;
            `;
            const data = await this.database.runQuery(sql, [sensorId]);
            this.database.disconnect();
            return data.length > 0 ? data[0] : null;
        } catch (error) {
            console.error('Error in getLatestMeasurementBySensorId:', error);
            throw error;
        }
    }


    /**
     * Retrieves all routers, sensors, and measurements associated with a specific location ID.
     * @param {number} locationId - The ID of the location.
     * @returns {Promise<Object>} A promise that resolves to an object containing routers, sensors, and measurements.
     * @throws {Error} Throws an error if the query fails.
     */
    async getAllDataByLocationId(locationId) {
        try {
            await this.database.connect();

            const routersSql = "SELECT * FROM Routers WHERE location_id = ?;";
            const routers = await this.database.runQuery(routersSql, [locationId]);

            const sensorDevicesSql = `
                SELECT * FROM SensorDevices 
                WHERE router_id IN (SELECT id FROM Routers WHERE location_id = ?);
            `;
            const sensorDevices = await this.database.runQuery(sensorDevicesSql, [locationId]);

            const sensorsSql = `
                SELECT s.* FROM Sensors s
                JOIN SensorDevices sd ON s.sensor_device_id = sd.id
                WHERE sd.router_id IN (SELECT id FROM Routers WHERE location_id = ?);
            `;
            const sensors = await this.database.runQuery(sensorsSql, [locationId]);

            const temperatureReadingsSql = `
                SELECT tr.* FROM TemperatureReadings tr
                JOIN Sensors s ON tr.sensor_id = s.id
                JOIN SensorDevices sd ON s.sensor_device_id = sd.id
                WHERE sd.router_id IN (
                    SELECT id FROM Routers WHERE location_id = ?
                );
            `;
            const readings = await this.database.runQuery(temperatureReadingsSql, [locationId]);

            this.database.disconnect();

            return { routers, sensorDevices, sensors, readings };
        } catch (error) {
            console.error('Error in getAllDataByLocationId:', error);
            throw error;
        }
    }


    /**
     * Retrieves the latest measurements for all sensors in a specific router.
     * @param {number} routerId - The ID of the router.
     * @returns {Promise<Array>} A promise that resolves to an array of latest measurement objects for each sensor.
     * @throws {Error} Throws an error if the query fails.
     */
    async getLatestMeasurementsByRouterId(routerId) {
        try {
            await this.database.connect();
            const sql = `
                SELECT tr.* FROM TemperatureReadings tr
                INNER JOIN (
                    SELECT sensor_id, MAX(recorded_at) AS latest
                    FROM TemperatureReadings
                    GROUP BY sensor_id
                ) latest_readings
                ON tr.sensor_id = latest_readings.sensor_id AND tr.recorded_at = latest_readings.latest
                JOIN Sensors s ON tr.sensor_id = s.id
                JOIN SensorDevices sd ON s.sensor_device_id = sd.id
                WHERE sd.router_id = ?;
            `;
            const data = await this.database.runQuery(sql, [routerId]);
            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getLatestMeasurementsByRouterId:', error);
            throw error;
        }
    }
}

export { DatabaseDAO };