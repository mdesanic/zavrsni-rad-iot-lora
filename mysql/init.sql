USE water_temp_db;


CREATE TABLE Locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Transmitters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    location_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES Locations(id) ON DELETE SET NULL
);

CREATE TABLE SensorDevices (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    transmitter_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    serial_number VARCHAR(255) UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transmitter_id) REFERENCES Transmitters(id) ON DELETE CASCADE
);

CREATE TABLE Sensors (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_device_id INTEGER NOT NULL,
    sensor_index INTEGER NOT NULL, 
    sensor_address TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_device_id) REFERENCES SensorDevices(id) ON DELETE CASCADE,
    UNIQUE(sensor_device_id, sensor_index)
);

CREATE TABLE TemperatureReadings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_id INTEGER NOT NULL,
    temperature REAL NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE
);

CREATE TABLE SensorSettings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sensor_id INTEGER NOT NULL UNIQUE,
    depth_meters REAL, 
    note TEXT,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE
);
