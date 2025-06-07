import mysql from 'mysql2/promise';

const config = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || 'password',
  database: process.env.DB_DATABASE || 'water_temp_db',
  port: 3306,
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
        this.connection.end((err) => {
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

  async runQuery(sql, params = []) {
    if (!this.connection) {
      throw new Error('Database connection not established');
    }

    try {
      const [rows] = await this.connection.query(sql, params);
      return rows;
    } catch (error) {
      console.error('Error running query:', error);
      throw error;
    }
  }
}

export { Database };
