import { Database } from "../modules/database.js";

class DatabaseDAO {
    constructor() {
        this.database = new Database();
    }

    async registerDevice(mac) {
        await this.database.connect();

        try {
            const existingResult = await this.database.runQuery(
                "SELECT id FROM Transmitters WHERE transmitter_mac_address = ?",
                [mac]
            );
            if (existingResult.length > 0) {
                return { exists: true, deviceId: existingResult[0].id };
            }

            const insertResult = await this.database.runQuery(
                "INSERT INTO Transmitters (name, transmitter_mac_address) VALUES (?, ?)",
                [`Transmitter_${mac}`, mac]
            );

            return { exists: false, deviceId: insertResult.insertId };

        } catch (error) {
            if (error.code === 'ER_DUP_ENTRY') {
                const fallbackResult = await this.database.runQuery(
                    "SELECT id FROM Transmitters WHERE transmitter_mac_address = ?",
                    [mac]
                );
                if (fallbackResult.length > 0) {
                    return { exists: true, deviceId: fallbackResult[0].id };
                } else {
                    throw new Error("Duplicate MAC detected but device not found on fallback lookup.");
                }
            } else {
                throw error;
            }
        } finally {
            this.database.disconnect();
        }
    }


    async storeReading(transmitterMac, sensorDeviceMac, sensorAddress, temperature) {
        try {
            await this.database.connect();
            await this.database.runQuery("START TRANSACTION");

            let devices = await this.database.runQuery(
                "SELECT id FROM Transmitters WHERE transmitter_mac_address = ?",
                [transmitterMac]
            );

            let device;
            if (!devices || devices.length === 0) {
                const insertResult = await this.database.runQuery(
                    "INSERT INTO Transmitters (name, transmitter_mac_address) VALUES (?, ?)",
                    [`Transmitter_${transmitterMac}`, transmitterMac]
                );
                device = { id: insertResult.insertId };
            } else {
                device = devices[0];
            }

            let sensorDevices = await this.database.runQuery(
                "SELECT id FROM SensorDevices WHERE sensor_device_mac_address = ? LIMIT 1",
                [sensorDeviceMac]
            );

            let sensorDevice;
            if (!sensorDevices || sensorDevices.length === 0) {
                const insertResult = await this.database.runQuery(
                    "INSERT INTO SensorDevices (transmitter_id, name, sensor_device_mac_address) VALUES (?, ?, ?)",
                    [
                        device.id,
                        `Sensor_${sensorDeviceMac}`,
                        sensorDeviceMac,
                    ]
                );
                sensorDevice = { id: insertResult.insertId };
            } else {
                sensorDevice = sensorDevices[0];

                if (sensorDevice.transmitter_id !== device.id) {
                    await this.database.runQuery(
                        "UPDATE SensorDevices SET transmitter_id = ? WHERE id = ?",
                        [device.id, sensorDevice.id]
                    );
                }
            }

            let sensor;
            try {
                let sensors = await this.database.runQuery(
                    "SELECT id FROM Sensors WHERE sensor_address = ? AND sensor_device_id = ?",
                    [sensorAddress, sensorDevice.id]
                );

                if (sensors && sensors.length > 0) {
                    sensor = sensors[0];
                } else {
                    const insertResult = await this.database.runQuery(
                        "INSERT INTO Sensors (sensor_device_id, sensor_address) VALUES (?, ?)",
                        [sensorDevice.id, sensorAddress]
                    );
                    sensor = { id: insertResult.insertId };
                }
            } catch (error) {
                if (error.code === 'ER_DUP_ENTRY') {
                    let existing = await this.database.runQuery(
                        "SELECT id FROM Sensors WHERE sensor_device_id = ? AND sensor_address = ?",
                        [sensorDevice.id, sensorAddress]
                    );
                    if (existing && existing.length > 0) {
                        sensor = existing[0];
                    } else {
                        throw error;
                    }
                } else {
                    throw error;
                }
            }

            const result = await this.database.runQuery(
                "INSERT INTO TemperatureReadings (sensor_id, temperature) VALUES (?, ?)",
                [sensor.id, temperature]
            );

            await this.database.runQuery("COMMIT");
            return result.insertId;
        } catch (error) {
            await this.database.runQuery("ROLLBACK");
            console.error('Transaction failed:', error);
            throw error;
        } finally {
            this.database.disconnect();
        }
    }

    async getAllLocations() {
        await this.database.connect();
        try {
            return await this.database.runQuery(
                "SELECT id, name, description, created_at FROM Locations"
            );
        } catch (error) {
            console.error('Failed to fetch locations:', error);
            throw new Error('Error retrieving locations');
        } finally {
            this.database.disconnect();
        }
    }

    async createLocation(name, description = null) {
        await this.database.connect();
        try {
            const existing = await this.database.runQuery(
                "SELECT id FROM Locations WHERE name = ?",
                [name]
            );
            if (existing.length > 0) {
                return { created: false, exists: true, locationId: existing[0].id };
            }
            const result = await this.database.runQuery(
                "INSERT INTO Locations (name, description) VALUES (?, ?)",
                [name, description]
            );
            return { created: true, locationId: result.insertId };
        } catch (error) {
            console.error('Failed to create location:', error);
            throw new Error('Error creating location');
        } finally {
            this.database.disconnect();
        }
    }

    async updateLocation(id, updates) {
        const fields = [];
        const values = [];

        if (updates.name !== undefined) {
            fields.push("name = ?");
            values.push(updates.name);
        }

        if (updates.description !== undefined) {
            fields.push("description = ?");
            values.push(updates.description);
        }

        if (fields.length === 0) return;

        values.push(id);

        await this.database.connect();
        try {
            await this.database.runQuery(
                `UPDATE Locations SET ${fields.join(', ')} WHERE id = ?`,
                values
            );
        } catch (error) {
            console.error('Failed to update location:', error);
            throw new Error('Error updating location');
        } finally {
            this.database.disconnect();
        }
    }

    async getLocationById(id) {
        await this.database.connect();
        try {
            const result = await this.database.runQuery(
                "SELECT id, name, description, created_at FROM Locations WHERE id = ?",
                [id]
            );
            return result.length > 0 ? result[0] : null;
        } catch (error) {
            console.error('Failed to fetch location by id:', error);
            throw new Error('Error retrieving location');
        } finally {
            this.database.disconnect();
        }
    }

    async getTransmittersByLocation(locationId) {
        await this.database.connect();
        try {
            return await this.database.runQuery(
                "SELECT id, name, transmitter_mac_address FROM Transmitters WHERE location_id = ?",
                [locationId]
            );
        } catch (error) {
            console.error('Failed to fetch transmitters:', error);
            throw new Error('Error retrieving transmitters');
        } finally {
            this.database.disconnect();
        }
    }

    async getTransmittersWithoutLocation() {
        await this.database.connect();
        try {
            return await this.database.runQuery(
                "SELECT id, name, transmitter_mac_address FROM Transmitters WHERE location_id IS NULL;",
            );
        } catch (error) {
            console.error('Failed to fetch transmitters without location:', error);
            throw new Error('Error retrieving transmitters without location');
        } finally {
            this.database.disconnect();
        }
    }

    async getTransmitterDetails(id) {
        await this.database.connect();
        try {
            const transmitterResult = await this.database.runQuery(
                "SELECT id, name, transmitter_mac_address, location_id FROM Transmitters WHERE id = ?",
                [id]
            );
            if (transmitterResult.length === 0) return null;

            const transmitter = transmitterResult[0];

            if (transmitter.location_id !== null && transmitter.location_id !== undefined) {
                const locationResult = await this.database.runQuery(
                    "SELECT name FROM Locations WHERE id = ?",
                    [transmitter.location_id]
                );
                if (locationResult.length > 0) {
                    transmitter.location_name = locationResult[0].name;
                } else {
                    transmitter.location_name = null;
                }
            } else {
                transmitter.location_name = null;
            }

            const sensorDevices = await this.database.runQuery(
                "SELECT id, name, sensor_device_mac_address FROM SensorDevices WHERE transmitter_id = ?",
                [id]
            );

            for (const device of sensorDevices) {
                const sensors = await this.database.runQuery(
                    "SELECT id, name, sensor_address FROM Sensors WHERE sensor_device_id = ?",
                    [device.id]
                );
                device.sensors = sensors;
            }

            transmitter.sensorDevices = sensorDevices;
            return transmitter;
        } catch (error) {
            console.error('Failed to get transmitter details:', error);
            throw new Error('Error retrieving transmitter details');
        } finally {
            this.database.disconnect();
        }
    }

    async updateTransmitter(id, updates) {
        const fields = [];
        const values = [];

        if (updates.name !== undefined) {
            fields.push("name = ?");
            values.push(updates.name);
        }
        if (updates.location_id !== undefined) {
            fields.push("location_id = ?");
            values.push(updates.location_id);
        }
        if (fields.length === 0) return;
        values.push(id);

        await this.database.connect();
        try {
            await this.database.runQuery(
                `UPDATE Transmitters SET ${fields.join(', ')} WHERE id = ?`,
                values
            );
        } catch (error) {
            console.error('Failed to update transmitter:', error);
            throw new Error('Error updating transmitter');
        } finally {
            this.database.disconnect();
        }
    }

    async updateSensorDevice(id, updates) {
        const fields = [];
        const values = [];

        if (updates.name !== undefined) {
            fields.push("name = ?");
            values.push(updates.name);
        }
        
        if (fields.length === 0) return;
        values.push(id);

        await this.database.connect();
        try {
            await this.database.runQuery(
                `UPDATE SensorDevices SET ${fields.join(', ')} WHERE id = ?`,
                values
            );
        } catch (error) {
            console.error('Failed to update sensor device:', error);
            throw new Error('Error updating sensor device');
        } finally {
            this.database.disconnect();
        }
    }

    async updateSensor(id, updates) {
        await this.database.connect();
        try {
            const fields = [];
            const values = [];

            if (updates.name !== undefined) {
                fields.push("name = ?");
                values.push(updates.name);
            }

            if (fields.length > 0) {
                values.push(id);
                await this.database.runQuery(
                    `UPDATE Sensors SET ${fields.join(', ')} WHERE id = ?`,
                    values
                );
                values.pop();
            }

            if (updates.depth_meters !== undefined) {
                const exists = await this.database.runQuery(
                    "SELECT id FROM SensorSettings WHERE sensor_id = ?",
                    [id]
                );
                if (exists.length > 0) {
                    await this.database.runQuery(
                        "UPDATE SensorSettings SET depth_meters = ? WHERE sensor_id = ?",
                        [updates.depth_meters, id]
                    );
                } else {
                    if (updates.depth_meters !== null) {
                        await this.database.runQuery(
                            "INSERT INTO SensorSettings (sensor_id, depth_meters) VALUES (?, ?)",
                            [id, updates.depth_meters]
                        );
                    }
                }
            }
        } catch (error) {
            console.error('Failed to update sensor:', error);
            throw new Error('Error updating sensor');
        } finally {
            this.database.disconnect();
        }
    }

    async getLatestReadingsForSensorDevice(sensorDeviceId) {
        await this.database.connect();
        try {
            const sensors = await this.database.runQuery(
                `SELECT
                    s.id,
                    s.name,
                    s.sensor_address,
                    ss.depth_meters
                FROM
                    Sensors s
                LEFT JOIN
                    SensorSettings ss ON s.id = ss.sensor_id
                WHERE
                    s.sensor_device_id = ?`,
                [sensorDeviceId]
            );

            if (sensors.length === 0) {
                return [];
            }

            const sensorsWithLatestReadings = [];
            for (const sensor of sensors) {
                const latestReadingResult = await this.database.runQuery(
                    "SELECT temperature, recorded_at FROM TemperatureReadings WHERE sensor_id = ? ORDER BY recorded_at DESC LIMIT 1",
                    [sensor.id]
                );
                const latestReading = latestReadingResult.length > 0 ? latestReadingResult[0] : null;

                sensorsWithLatestReadings.push({
                    id: sensor.id,
                    name: sensor.name,
                    sensor_address: sensor.sensor_address,
                    depth_meters: sensor.depth_meters,
                    latest_reading: latestReading
                });
            }
            return sensorsWithLatestReadings;
        } catch (error) {
            console.error('Failed to get latest readings for sensor device:', error);
            throw new Error('Error retrieving latest readings');
        } finally {
            this.database.disconnect();
        }
    }

    async getGroupedReadings(sensorDeviceId, from, to) {
        await this.database.connect();
        try {
            const sensors = await this.database.runQuery(
                "SELECT id, name FROM Sensors WHERE sensor_device_id = ?",
                [sensorDeviceId]
            );

            const result = [];

            for (const sensor of sensors) {
                const readings = await this.database.runQuery(
                    `SELECT temperature, recorded_at FROM TemperatureReadings
                     WHERE sensor_id = ? AND recorded_at BETWEEN ? AND ?
                     ORDER BY recorded_at ASC`,
                    [sensor.id, from, to]
                );
                result.push({
                    sensorId: sensor.id,
                    name: sensor.name,
                    readings
                });
            }

            return result;
        } catch (error) {
            console.error('Failed to fetch grouped readings:', error);
            throw new Error('Error fetching grouped readings');
        } finally {
            this.database.disconnect();
        }
    }
}

export { DatabaseDAO };