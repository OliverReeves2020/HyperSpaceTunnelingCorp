package com.example.hyperspacetunnelingcorp;

import java.io.*;

import database.LocalNeo4jDatabase;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "accelerators", value = "/accelerators")
public class Accelerators extends HttpServlet {
    private String message;
    //* `GET`: `/accelerators` - returns a list of accelerators with their information
    //* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator
    //* `GET`: `/accelerators/{acceleratorID}/to/{targetAcceleratorID}` - returns the cheapest route from `acceleratorID` to `targetAcceleratorID`

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {



        // Get the request path using the getContextPath() and getServletPath() methods
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestPath = request.getRequestURI().substring(contextPath.length() + servletPath.length());

        // `/accelerators` - returns a list of accelerators with their information
        if(requestPath.equals("")){
            System.out.println("null");

            return;
        }

        //* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator



        response.setContentType("text/plain");

        LocalNeo4jDatabase db= new LocalNeo4jDatabase();
        ServletContext context = getServletContext();
        db.findCheapestPath(context,"ARC","SOL");
        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}