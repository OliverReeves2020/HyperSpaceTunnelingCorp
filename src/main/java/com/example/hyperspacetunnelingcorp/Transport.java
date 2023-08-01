package com.example.hyperspacetunnelingcorp;

import functions.JourneyCostCalculator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet(name="transport",value="/transport")
public class Transport extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //* `GET`: `/transport/{distance}?passengers={number}&parking={days}`
        // - returns the cheapest vehicle to use (and the cost of the journey) for the given `distance` (in AUs), `number` or passengers and `days` of parking
        //  * Accelerators typically sit above the star, so if you're on Earth and want to travel to the Sol accelerator, the distance would be ~1AU.

        //extract info from path



        //check that format is correct
        double distance = 100;
        int numberOfPeople = 10;
        int daysParked = 10;


        //find the cheapest option
        double personal = JourneyCostCalculator.calculatePersonalTransportCost(100, 10, 10);
        double htc = JourneyCostCalculator.calculateHTCTransportCost(100, 10);
        if(htc>personal){

        }
        else if (personal>htc) {

        }
        else{

        }


    }

}
