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



        // Get the request path
        String requestPath = request.getPathInfo();
        //split into diffrent paths
        System.out.println(requestPath);
        // `/accelerators` - returns a list of accelerators with their information
        if(requestPath==null){
            System.out.println("null");

            return;
        }

        String[] parts = requestPath.substring(1).split("/");

        //* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator



        //* `GET`: `/accelerators/{acceleratorID}/to/{targetAcceleratorID}` - returns the cheapest route from `acceleratorID` to `targetAcceleratorID`
        if(parts.length==3&&parts[1].equals("to")){
            String startAccelerator = parts[0].toString();
            String endAccelerator = parts[2].toString();
            LocalNeo4jDatabase db= new LocalNeo4jDatabase();
            ServletContext context = getServletContext();


           if(db.validNodeByName(context,startAccelerator)&& db.validNodeByName(context,endAccelerator)){



           }else{
               response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parmaters provided in the path.");
               return;
           }



        }









        response.setContentType("text/plain");

        LocalNeo4jDatabase db= new LocalNeo4jDatabase();
        ServletContext context = getServletContext();
        db.findCheapestPath(context,"ARC","SOL");

    }

    public void destroy() {
    }
}