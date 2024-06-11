const Database = require("../modules/database.js");

class DatabaseDAO {

    constructor() {
        this.database = new Database();
    }

    getAllLocations = async function () {
        try {
            await this.database.connect();
            console.log('Database connected');

            let sql = "SELECT * FROM Location;";
            let data = await this.database.runQuery(sql, []);

            this.database.disconnect();
            return data;
        } catch (error) {
            console.error('Error in getAllLocations:', error);
            throw error;
        }
    }
}

module.exports = DatabaseDAO;