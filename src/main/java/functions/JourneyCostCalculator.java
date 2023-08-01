package functions;

import java.util.Scanner;

public class JourneyCostCalculator {

    public static double calculatePersonalTransportCost(double distance, int numberOfPeople, int daysParked) {
        double fuelCost = 0.30 * distance;
        double storageCost = 5 * daysParked;

        int peoplePerShip = 4;
        int numberOfShips = (int) Math.ceil((double) numberOfPeople / peoplePerShip);
        return (fuelCost + (storageCost* numberOfShips));
    }

    public static double calculateHTCTransportCost(double distance, int numberOfPeople) {
        double fuelCost = 0.45 * distance;

        int peoplePerShip = 5;
        int numberOfShips = (int) Math.ceil((double) numberOfPeople / peoplePerShip);

        return fuelCost * numberOfShips;
    }
}
