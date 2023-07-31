package listeners;

import database.LocalNeo4jDatabase;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class ServerStart implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Initialization code here
        System.out.println("Web application initialized.");
        LocalNeo4jDatabase db= new LocalNeo4jDatabase();
        ServletContext context = event.getServletContext();
        db.startup(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Cleanup code here
        System.out.println("Web application destroyed.");
    }
}
