package com.example.hyperspacetunnelingcorp;

import java.io.*;

import database.LocalNeo4jDatabase;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "accelerators", value = "/accelerators")
public class Accelerators extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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