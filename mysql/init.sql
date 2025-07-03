CREATE DATABASE IF NOT EXISTS water_temp_db;
USE water_temp_db;

-- Locations
CREATE TABLE Locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Transmitters
CREATE TABLE Transmitters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    transmitter_mac_address VARCHAR(17) UNIQUE NOT NULL COMMENT 'Device MAC address',
    location_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES Locations(id) ON DELETE SET NULL
);

-- SensorDevices
CREATE TABLE SensorDevices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    transmitter_id INT NOT NULL,
    name TEXT,
    sensor_device_mac_address VARCHAR(255) UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transmitter_id) REFERENCES Transmitters(id) ON DELETE CASCADE
);

-- Sensors
CREATE TABLE Sensors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sensor_device_id INT NOT NULL,
    sensor_address VARCHAR(17) UNIQUE NOT NULL COMMENT '8-byte hex address',
    name VARCHAR(255), -- Editable via app
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_device_id) REFERENCES SensorDevices(id) ON DELETE CASCADE
);

-- SensorSettings
CREATE TABLE SensorSettings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sensor_id INT NOT NULL UNIQUE,
    depth_meters FLOAT COMMENT 'Negative for underwater',
    note TEXT,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE
);

-- TemperatureReadings
CREATE TABLE TemperatureReadings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sensor_id INT NOT NULL,
    temperature FLOAT NOT NULL COMMENT 'In Celsius',
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sensor_id) REFERENCES Sensors(id) ON DELETE CASCADE,
    INDEX (recorded_at)
);

-- Insert Locations
INSERT INTO Locations (name, description) VALUES
('Lake Tahoe', 'Main monitoring station at the north shore'),
('Crater Lake', 'Deep water temperature monitoring'),
('Great Salt Lake', 'Shallow water salinity testing area'),
('Lake Michigan', 'Coastal temperature tracking'),
('Reservoir #5', 'Municipal water supply monitoring');

-- Insert Transmitters
INSERT INTO Transmitters (name, transmitter_mac_address, location_id) VALUES
('Tahoe-Buoy1', '00:1A:3F:12:45:67', 1),
('Crater-Deep1', '00:1B:4D:23:56:78', 2),
('SaltLake-Sensor1', '00:1C:5E:34:67:89', 3),
('Michigan-Coast1', '00:1D:6F:45:78:90', 4),
('Unassigned-1', '00:1E:7A:56:89:01', NULL),
('Unassigned-2', '00:1F:8B:67:90:12', NULL);

-- Insert SensorDevices
INSERT INTO SensorDevices (transmitter_id, name, sensor_device_mac_address) VALUES
(1, 'Tahoe Surface Array', 'AA:BB:CC:11:22:33'),
(1, 'Tahoe Deep Array', 'AA:BB:CC:44:55:66'),
(2, 'Crater Main Sensor', 'BB:CC:DD:11:22:33'),
(3, 'Salt Lake Test Unit', 'CC:DD:EE:11:22:33'),
(4, 'Michigan Coastal', 'DD:EE:FF:11:22:33'),
(5, 'Unassigned Device', 'EE:FF:GG:11:22:33');

-- Insert Sensors
INSERT INTO Sensors (sensor_device_id, sensor_address, name) VALUES
(1, '28FF123456781234', 'Surface Temp 1'),
(1, '28FF234567891234', 'Surface Temp 2'),
(2, '28FF345678901234', 'Depth Temp 5m'),
(2, '28FF456789012345', 'Depth Temp 10m'),
(3, '28FF567890123456', 'Crater Main'),
(4, '28FF678901234567', 'Salt Lake Test'),
(5, '28FF789012345678', 'Coastal Surface'),
(6, '28FF890123456789', 'Unassigned Sensor');

-- Insert SensorSettings
INSERT INTO SensorSettings (sensor_id, depth_meters, note) VALUES
(1, 0.5, 'Surface float'),
(2, 0.5, 'Secondary surface'),
(3, -5.0, '5m below surface'),
(4, -10.0, '10m thermocline'),
(5, -15.0, 'Deep water monitoring'),
(6, -2.0, 'Salt water testing'),
(7, 0.3, 'Coastal waters');

-- Insert TemperatureReadings (last 24 hours for each sensor)
-- Sensor 1 (Tahoe Surface 1)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(1, 18.2, NOW() - INTERVAL 1 HOUR),
(1, 18.5, NOW() - INTERVAL 2 HOUR),
(1, 17.9, NOW() - INTERVAL 3 HOUR),
(1, 17.5, NOW() - INTERVAL 4 HOUR),
(1, 17.2, NOW() - INTERVAL 5 HOUR);

-- Sensor 2 (Tahoe Surface 2)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(2, 18.0, NOW() - INTERVAL 1 HOUR),
(2, 18.3, NOW() - INTERVAL 2 HOUR),
(2, 17.8, NOW() - INTERVAL 3 HOUR),
(2, 17.4, NOW() - INTERVAL 4 HOUR),
(2, 17.1, NOW() - INTERVAL 5 HOUR);

-- Sensor 3 (Tahoe Depth 5m)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(3, 12.5, NOW() - INTERVAL 1 HOUR),
(3, 12.4, NOW() - INTERVAL 2 HOUR),
(3, 12.3, NOW() - INTERVAL 3 HOUR),
(3, 12.2, NOW() - INTERVAL 4 HOUR),
(3, 12.1, NOW() - INTERVAL 5 HOUR);

-- Sensor 4 (Tahoe Depth 10m)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(4, 8.2, NOW() - INTERVAL 1 HOUR),
(4, 8.1, NOW() - INTERVAL 2 HOUR),
(4, 8.0, NOW() - INTERVAL 3 HOUR),
(4, 7.9, NOW() - INTERVAL 4 HOUR),
(4, 7.8, NOW() - INTERVAL 5 HOUR);

-- Sensor 5 (Crater Main)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(5, 6.5, NOW() - INTERVAL 1 HOUR),
(5, 6.4, NOW() - INTERVAL 2 HOUR),
(5, 6.3, NOW() - INTERVAL 3 HOUR),
(5, 6.2, NOW() - INTERVAL 4 HOUR),
(5, 6.1, NOW() - INTERVAL 5 HOUR);

-- Sensor 6 (Salt Lake Test)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(6, 22.1, NOW() - INTERVAL 1 HOUR),
(6, 22.5, NOW() - INTERVAL 2 HOUR),
(6, 23.0, NOW() - INTERVAL 3 HOUR),
(6, 23.2, NOW() - INTERVAL 4 HOUR),
(6, 23.5, NOW() - INTERVAL 5 HOUR);

-- Sensor 7 (Michigan Coastal)
INSERT INTO TemperatureReadings (sensor_id, temperature, recorded_at) VALUES
(7, 15.2, NOW() - INTERVAL 1 HOUR),
(7, 15.0, NOW() - INTERVAL 2 HOUR),
(7, 14.8, NOW() - INTERVAL 3 HOUR),
(7, 14.6, NOW() - INTERVAL 4 HOUR),
(7, 14.5, NOW() - INTERVAL 5 HOUR);

