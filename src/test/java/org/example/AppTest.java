package org.example;

import org.example.model.RentalAgreement;
import org.example.service.ToolRentalService;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class AppTest {


    // Can create a ToolRentalService instance
    @Test
    public void test_create_tool_rental_service_instance() {
        ToolRentalService toolRentalService = new ToolRentalService();
        assertNotNull(toolRentalService);
    }

    // Can create a RentalAgreement instance using ToolRentalService.checkout()
    @Test
    public void test_create_rental_agreement_instance() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 5, 20, checkoutDate);
        assertNotNull(rentalAgreement);
    }

    // Can print a RentalAgreement instance using RentalAgreement.printRentalAgreement()
    @Test
    public void test_print_rental_agreement() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 5, 20, checkoutDate);
        rentalAgreement.printRentalAgreement();
        // Assert that the printRentalAgreement() method does not throw any exceptions
    }

    // Throws an IllegalArgumentException if an invalid tool code is provided to ToolRentalService.checkout()
    @Test
    public void test_invalid_tool_code_exception() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        assertThrows(IllegalArgumentException.class, () -> {
            toolRentalService.checkout("INVALID", 5, 20, checkoutDate);
        });
    }

    // Throws an IllegalArgumentException if rental days is less than 1 in ToolRentalService.checkout()
    @Test
    public void test_invalid_rental_days_exception() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        assertThrows(IllegalArgumentException.class, () -> {
            toolRentalService.checkout("JAKR", -1, 20, checkoutDate);
        });
    }

    // Throws an IllegalArgumentException if discount percent is less than 0 or greater than 100 in ToolRentalService.checkout()
    @Test
    public void test_invalid_discount_percent_exception() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        assertThrows(IllegalArgumentException.class, () -> {
            toolRentalService.checkout("JAKR", 5, -1, checkoutDate);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            toolRentalService.checkout("JAKR", 5, 101, checkoutDate);
        });
    }

    // Calculates correct pre-discount charge for a rental period that spans multiple charge periods
    @Test
    public void test_calculate_pre_discount_charge() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 10, 20, checkoutDate);
        BigDecimal expectedPreDiscountCharge = new BigDecimal("14.95");
        assertEquals(expectedPreDiscountCharge, rentalAgreement.getPreDiscountCharge());
    }

    // Calculates correct final charge for a rental period that spans multiple charge periods
    @Test
    public void test_calculate_final_charge() {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 3);
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 10, 20, checkoutDate);
        BigDecimal expectedFinalCharge = new BigDecimal("11.96");
        assertEquals(expectedFinalCharge, rentalAgreement.getFinalCharge());
    }

}
