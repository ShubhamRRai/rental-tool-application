package org.example;


import org.example.model.RentalAgreement;
import org.example.service.ToolRentalService;

import java.time.LocalDate;

public class App
{
    public static void main( String[] args )
    {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3); // Assuming today's date for checkout

        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement =  toolRentalService.checkout("JAKR", 5, 20, checkoutDate);

        rentalAgreement.printRentalAgreement();

    }
}
