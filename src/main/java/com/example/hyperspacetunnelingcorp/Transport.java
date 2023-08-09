package com.example.hyperspacetunnelingcorp;

import functions.JourneyCostCalculator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;


@WebServlet(name = "transport", value = "/transport")
public class Transport extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get the request path
        String requestPath = req.getPathInfo();
        String contentType = req.getHeader("Accept");


        if (requestPath == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "path is not complete");
            return;
        }

        String[] parts = requestPath.substring(1).split("/");

        //GET: /transport/options
        if (parts.length == 1 && parts[0].equals("options")) {

            String jsonString = "{\n" +
                    "  \"Personal Transport\": {\n" +
                    "    \"cost\": \"£0.30/[AU](standard fuel cost)\",\n" +
                    "    \"storage_cost\": \"£5 per day for ship storage at the accelerator\",\n" +
                    "    \"capacity\": \"fits up to 4 people\"\n" +
                    "  },\n" +
                    "  \"HTC Transport\": {\n" +
                    "    \"cost\": \"£0.45/[AU]\",\n" +
                    "    \"capacity\": \"fits up to 5 people\"\n" +
                    "  }\n" +
                    "}";

            sender(resp, contentType, jsonString);

        } else if (parts.length == 1) {
            double distance;
            try {
                distance = Double.parseDouble(parts[0]);
                //check if distance is valid
                if (distance <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid distance provided in the path.");
                return;
            }
            //extract info from path
            String passengers = req.getParameter("passengers");
            String parking = req.getParameter("parking");

            if (passengers == null || parking == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing or no parameters found");
                return;
            }

            //check that format is correct
            int numberOfPeople;
            int daysParked;
            try {
                numberOfPeople = Integer.parseInt(passengers);
                daysParked = Integer.parseInt(parking);
                if (numberOfPeople <= 0 || daysParked < 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parmaters provided in the path.");
                return;
            }
            try {
                double personal = JourneyCostCalculator.calculatePersonalTransportCost(distance, numberOfPeople, daysParked);
                double htc = JourneyCostCalculator.calculateHTCTransportCost(distance, numberOfPeople);
                JSONObject tosend = new JSONObject();

                System.out.println(personal);
                System.out.println(htc);

                if (htc > personal) {
                    // personal is cheaper so return personal
                    tosend.put("personal", personal);
                } else if (personal > htc) {
                    // htc is cheaper
                    tosend.put("htc", htc);
                } else {
                    // both are equal so return both
                    tosend.put("htc", htc);
                    tosend.put("personal", personal);
                }


                sender(resp, contentType, tosend.toString());
            } catch (JSONException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "internal issue");
            }


        }


        //* `GET`: `/transport/{distance}?passengers={number}&parking={days}`
        // - returns the cheapest vehicle to use (and the cost of the journey) for the given `distance` (in AUs), `number` or passengers and `days` of parking
        //  * Accelerators typically sit above the star, so if you're on Earth and want to travel to the Sol accelerator, the distance would be ~1AU.

        // Convert the parameter to the appropriate data type


    }


    public void sender(HttpServletResponse response, String ContentType, String message) throws IOException {
        //default to json
        Accelerators.jsonResponse(response, ContentType, message);


    }


}
