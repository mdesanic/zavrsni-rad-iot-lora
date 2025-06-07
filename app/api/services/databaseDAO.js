import { Database } from "../modules/database.js";

class DatabaseDAO {
    constructor() {
        this.database = new Database();
    }

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
}

export { DatabaseDAO };