package database;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

import java.io.File;
import java.time.Duration;

public class DatabaseManager {
    private static DatabaseManagementService managementService;

    public static synchronized DatabaseManagementService getManagementService(File dataDirectory) {
        if (managementService == null) {

            managementService = new DatabaseManagementServiceBuilder(dataDirectory.toPath())
                    .setConfig(GraphDatabaseSettings.transaction_timeout, Duration.ofSeconds(60))
                    .setConfig(GraphDatabaseSettings.preallocate_logical_logs, true)
                    .build();
        }
        return managementService;
    }
}
