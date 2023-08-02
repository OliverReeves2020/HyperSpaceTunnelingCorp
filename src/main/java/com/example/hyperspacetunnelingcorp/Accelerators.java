package com.example.hyperspacetunnelingcorp;

import java.io.*;

import database.LocalNeo4jDatabase;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "accelerators", value = "/accelerators")
public class Accelerators extends HttpServlet {

    //* `GET`: `/accelerators` - returns a list of accelerators with their information
    //* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator
    //* `GET`: `/accelerators/{acceleratorID}/to/{targetAcceleratorID}` - returns the cheapest route from `acceleratorID` to `targetAcceleratorID`

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get the request path
        String requestPath = request.getPathInfo();
        String contentType = request.getHeader("Accept");

        // `/accelerators` - returns a list of accelerators with their information
        if(requestPath==null){
            System.out.println("null");

            return;
        }

        String[] parts = requestPath.substring(1).split("/");

        //* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator
        if(parts.length==1){
            LocalNeo4jDatabase db= new LocalNeo4jDatabase();
            ServletContext context = getServletContext();
            sender(response,contentType,db.nodeInfo(getServletContext(),parts[0]));
            return;

        }

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



        System.out.println(contentType);

        response.setContentType("text/plain");


        //db.allNodes(getServletContext());
        // db.nodeInfo(getServletContext(),"SOL");

    }

    public void sender(HttpServletResponse response,String ContentType,String message) throws IOException {
        //default to json
        if(ContentType.equals("*/*")||ContentType.equals("application/json")||ContentType.equals("plain/text")){
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(message);
            out.close();
        }
        else{
            throw new IOException("request format not supported");
        }




    }


    public void destroy() {
    }



}