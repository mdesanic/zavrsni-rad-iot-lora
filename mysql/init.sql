USE water_temp_db;


CREATE TABLE Locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 1. Router uređaji (lokacije itd.)
CREATE TABLE Routers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    location_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES Locations(id) ON DELETE SET NULL
);


-- 2. Sensor uređaji koji su spojeni na routere
CREATE TABLE SensorDevices (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    router_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    serial_number VARCHAR(255) UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (router_id) REFERENCES Routers(id) ON DELETE CASCADE
);

-- 3. Fizički senzori spojeni na jedan SensorDevice (npr. DS18B20)
CREATE TABLE Sensors (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_device_id INTEGER NOT NULL,
    sensor_index INTEGER NOT NULL, -- npr. 0 ili 1 (redni broj na uređaju)
    sensor_address TEXT, -- opcionalno: OneWire adresa ako želiš
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_device_id) REFERENCES SensorDevices(id) ON DELETE CASCADE,
    UNIQUE(sensor_device_id, sensor_index)
);

-- 4. Mjerenja temperature
CREATE TABLE TemperatureReadings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_id INTEGER NOT NULL,
    temperature REAL NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE
);

-- 5. Postavke za pojedini senzor (npr. dubina u metrima)
CREATE TABLE SensorSettings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_id INTEGER NOT NULL UNIQUE,
    depth_meters REAL, -- ili depth_cm INTEGER
    note TEXT,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE
);