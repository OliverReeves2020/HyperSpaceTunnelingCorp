package com.example.hyperspacetunnelingcorp;

import functions.JourneyCostCalculator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name="transport",value="/transport")
public class Transport extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //* `GET`: `/transport/{distance}?passengers={number}&parking={days}`
        // - returns the cheapest vehicle to use (and the cost of the journey) for the given `distance` (in AUs), `number` or passengers and `days` of parking
        //  * Accelerators typically sit above the star, so if you're on Earth and want to travel to the Sol accelerator, the distance would be ~1AU.

        // Extract information from the URL path
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Distance not provided in the path.");
            return;
        }

        // Remove the leading "/" from the path
        String distanceParam = pathInfo.substring(1);
        // Convert the parameter to the appropriate data type
        double distance;
        try {
            distance = Double.parseDouble(distanceParam);
            //check if distance is valid
            if(distance<=0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid distance provided in the path.");
            return;
        }
        //extract info from path
        String passengers=req.getParameter("passengers");
        String parking=req.getParameter("parking");

        if(passengers==null||parking==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing or no parameters found");
            return;
        }

        //check that format is correct
        int numberOfPeople;
        int daysParked;
        try {
            numberOfPeople = Integer.parseInt(passengers);
            daysParked = Integer.parseInt(parking);
            if(numberOfPeople<=0||daysParked<0){throw new NumberFormatException();}

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parmaters provided in the path.");
            return;
        }

        //find the cheapest option
        double personal = JourneyCostCalculator.calculatePersonalTransportCost(distance, numberOfPeople, daysParked);
        double htc = JourneyCostCalculator.calculateHTCTransportCost(distance, numberOfPeople);
        Map<String, Double> tosend=new HashMap<>();
        System.out.println(personal);
        System.out.println(htc);
        if(htc>personal){
            //personal is cheaper so return personal
            tosend.put("personal",personal);
            sendResponse(resp,tosend);
        }
        else if (personal>htc) {
            //htc is cheaper
            tosend.put("htc",htc);
            sendResponse(resp,tosend);
        }
        else{
            //both are equal so return both
            tosend.put("htc",htc);
            tosend.put("personal",personal);
            sendResponse(resp,tosend);

        }


    }

    private void sendResponse(HttpServletResponse response, Map<String, Double> tosend) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(tosend);
        out.close();
    }

}
