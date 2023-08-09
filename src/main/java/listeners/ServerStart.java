package listeners;

import database.DatabaseManager;
import database.LocalNeo4jDatabase;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.neo4j.dbms.api.DatabaseManagementService;

import java.io.File;

public class ServerStart implements ServletContextListener {

    private DatabaseManagementService managementService;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Initialization code here
        System.out.println("Web application initialized.");
        LocalNeo4jDatabase db = new LocalNeo4jDatabase();
        ServletContext context = event.getServletContext();
        db.startup(context);
        // Define the relative path to the data directory inside resources
        String resourcePath = "/data/Solar";
        // Get the real path to the resource directory on the server
        String directoryPath = context.getRealPath(resourcePath);
        // Get the real path to the resource directory on the server
        File dataDirectory = new File(directoryPath);
        this.managementService = DatabaseManager.getManagementService(dataDirectory);

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Cleanup code here
        managementService.shutdown();

        System.out.println("Web application destroyed.");


    }
}
