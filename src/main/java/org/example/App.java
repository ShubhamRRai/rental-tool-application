package org.example;


import org.example.model.RentalAgreement;
import org.example.service.ToolRentalService;

import java.time.LocalDate;

public class App
{
     /**
     * Entry point for running the tool rental service application.
     * <p>
     * This method simulates the checkout of a tool from the tool rental service.
     * It creates a checkout date, initializes a ToolRentalService instance, and
     * checks out a tool with the specified parameters (tool code, rental days,
     * discount percent, and checkout date). It then prints the rental agreement
     * details to the console.
     * </p>
     *
     * @param args the command-line arguments (not used in this method)
     */
    public static void main( String[] args )
    {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3); // Assuming today's date for checkout

        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement =  toolRentalService.checkout("JAKR", 5, 20, checkoutDate);

        rentalAgreement.printRentalAgreement();

    }
}
