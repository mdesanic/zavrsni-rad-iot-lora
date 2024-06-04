const mysql = require('mysql2/promise');

const config = {
  host: 'iot-mysql',
  user: 'root',
  password: 'password',
  database: 'mydb'
};

class Database {
  constructor() {
    this.connection = null;
  }

  async connect() {
    try {
      this.connection = await mysql.createConnection(config);
      console.log('Connected to database!');
    } catch (error) {
      console.error('Error connecting to database:', error);
      throw error;
    }
  }
  

  disconnect() {
    if (this.connection) {
      try {
        this.connection.end(function(err) {
          if (err) throw err;
          console.log('Disconnected from database!');
        });
      } catch (error) {
        console.error('Error disconnecting from database:', error);
      } finally {
        this.connection = null;
      }
    }
  }

  runQuery(sql, params = []) {
    if (!this.connection) {
      throw new Error('Database connection not established');
    }

    try {
      const results = this.connection.query(sql, params, function(err, rows, fields) {
        if (err) throw err;
        return { rows, fields };
      });
      return results;
    } catch (error) {
      console.error('Error running query:', error);
      throw error;
    }
  }
}

module.exports = Database;
