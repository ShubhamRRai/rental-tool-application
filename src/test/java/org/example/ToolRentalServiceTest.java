package org.example;

import org.example.model.RentalAgreement;
import org.example.model.Tool;
import org.example.service.ToolRentalService;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ToolRentalServiceTest {


    // Can checkout a tool for a valid tool code, rental days, discount percent, and checkout date
    @Test
    public void test_checkout_valid_tool_code_rental_days_discount_percent_checkout_date() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("CHNS", 5, 10, LocalDate.of(2022, 1, 1));
        assertEquals("CHNS", rentalAgreement.getToolCode());
        assertEquals("Chainsaw", rentalAgreement.getToolType());
        assertEquals("Stihl", rentalAgreement.getToolBrand());
        assertEquals(5, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2022, 1, 1), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2022, 1, 6), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("1.49"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("4.47"), rentalAgreement.getPreDiscountCharge());
        assertEquals(10, rentalAgreement.getDiscountPercent());
        assertEquals(new BigDecimal("0.45"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("4.02"), rentalAgreement.getFinalCharge());
        assertEquals(3, rentalAgreement.getChargeDays());
    }

    // Can calculate pre-discount charge for a tool and charge days
    @Test
    public void test_calculate_pre_discount_charge_and_charge_days() {
        ToolRentalService toolRentalService = new ToolRentalService();
        Tool tool = new Tool("Chainsaw", "Stihl", new BigDecimal("1.49"), true, false, true);
        int chargeDays = toolRentalService.chargeDays(tool, 5, LocalDate.of(2022, 1, 1));
        assertEquals(3, chargeDays);
        BigDecimal preDiscountCharge = toolRentalService.calculatePreDiscountCharge(tool, chargeDays);
        assertEquals(new BigDecimal("4.47"), preDiscountCharge);
    }

    // Can calculate discount amount for a pre-discount charge and discount percent
    @Test
    public void test_calculate_discount_amount() {
        ToolRentalService toolRentalService = new ToolRentalService();
        BigDecimal preDiscountCharge = new BigDecimal("7.45");
        int discountPercent = 10;
        BigDecimal discountAmount = toolRentalService.calculateDiscountAmount(preDiscountCharge, discountPercent);
        assertEquals(new BigDecimal("0.75"), discountAmount);
    }


    // Can handle weekend charges for a tool
    @Test
    public void test_weekend_charges() {
        ToolRentalService toolRentalService = new ToolRentalService();
        Tool tool = new Tool("Ladder", "Werner", new BigDecimal("1.99"), true, true, false);
        int chargeDays = toolRentalService.chargeDays(tool, 7, LocalDate.of(2022, 1, 1));
        assertEquals(7, chargeDays);
    }

    // Can handle holiday charges for a tool
    @Test
    public void test_holiday_charges() {
        ToolRentalService toolRentalService = new ToolRentalService();
        Tool tool = new Tool("Jackhammer", "DeWalt", new BigDecimal("2.99"), true, false, false);
        int chargeDays = toolRentalService.chargeDays(tool, 7, LocalDate.of(2022, 9, 1));
        assertEquals(4, chargeDays);
    }

    // Throws an exception for an invalid tool code
    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_tool_code() {
        ToolRentalService toolRentalService = new ToolRentalService();
        toolRentalService.checkout("INVALID", 5, 10, LocalDate.of(2022, 1, 1));
    }

    // Throws an exception for rental days less than 1
    @Test(expected = IllegalArgumentException.class)
    public void test_rental_days_less_than_1() {
        ToolRentalService toolRentalService = new ToolRentalService();
        toolRentalService.checkout("CHNS", 0, 10, LocalDate.of(2022, 1, 1));
    }

    // Throws an exception for discount percent less than 0
    @Test(expected = IllegalArgumentException.class)
    public void test_discount_percent_less_than_0() {
        ToolRentalService toolRentalService = new ToolRentalService();
        toolRentalService.checkout("CHNS", 5, -10, LocalDate.of(2022, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkout_JAKR_09_03_15() {
        ToolRentalService toolRentalService = new ToolRentalService();
        toolRentalService.checkout("JAKR", 5, 101, LocalDate.of(2015, 9, 3));
    }

    @Test
    public void test_checkout_LADW_07_02_20() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("LADW", 3, 10, LocalDate.of(2020, 7, 2));
        assertEquals("LADW", rentalAgreement.getToolCode());
        assertEquals("Ladder", rentalAgreement.getToolType());
        assertEquals("Werner", rentalAgreement.getToolBrand());
        assertEquals(3, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2020, 7, 5), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("1.99"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("3.98"), rentalAgreement.getPreDiscountCharge());
        assertEquals(10, rentalAgreement.getDiscountPercent());
        assertEquals(new BigDecimal("0.40"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("3.58"), rentalAgreement.getFinalCharge());
        assertEquals(2, rentalAgreement.getChargeDays());
    }

    @Test
    public void test_checkout_CHNS_07_02_20() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("CHNS", 5, 25, LocalDate.of(2020, 7, 2));
        assertEquals("CHNS", rentalAgreement.getToolCode());
        assertEquals("Chainsaw", rentalAgreement.getToolType());
        assertEquals("Stihl", rentalAgreement.getToolBrand());
        assertEquals(5, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2020, 7, 7), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("1.49"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("4.47"), rentalAgreement.getPreDiscountCharge());
        assertEquals(25, rentalAgreement.getDiscountPercent());
        assertEquals(new BigDecimal("1.12"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("3.35"), rentalAgreement.getFinalCharge());
        assertEquals(3, rentalAgreement.getChargeDays());
    }

    @Test
    public void test_checkout_JAKD_09_03_15() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKD", 6, 0, LocalDate.of(2015, 9, 3));
        assertEquals("JAKD", rentalAgreement.getToolCode());
        assertEquals("Jackhammer", rentalAgreement.getToolType());
        assertEquals("DeWalt", rentalAgreement.getToolBrand());
        assertEquals(6, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2015, 9, 3), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2015, 9, 9), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("2.99"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("8.97"), rentalAgreement.getPreDiscountCharge());
        assertEquals(0, rentalAgreement.getDiscountPercent());
        assertEquals(new BigDecimal("0.00"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("8.97"), rentalAgreement.getFinalCharge());
        assertEquals(3, rentalAgreement.getChargeDays());
    }

    @Test
    public void test_checkout_JAKR_07_02_20() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 9, 0, LocalDate.of(2020, 7, 2));
        assertEquals("JAKR", rentalAgreement.getToolCode());
        assertEquals("Jackhammer", rentalAgreement.getToolType());
        assertEquals("Ridgid", rentalAgreement.getToolBrand());
        assertEquals(9, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2020, 7, 11), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("2.99"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("14.95"), rentalAgreement.getPreDiscountCharge());
        assertEquals(0, rentalAgreement.getDiscountPercent(), 0);
        assertEquals(new BigDecimal("0.00"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("8.97"), rentalAgreement.getFinalCharge());
        assertEquals(3, rentalAgreement.getChargeDays());
    }

    @Test
    public void test_checkout_JAKR_07_02_20_50Discount() {
        ToolRentalService toolRentalService = new ToolRentalService();
        RentalAgreement rentalAgreement = toolRentalService.checkout("JAKR", 9, 50, LocalDate.of(2020, 7, 2));
        assertEquals("JAKR", rentalAgreement.getToolCode());
        assertEquals("Jackhammer", rentalAgreement.getToolType());
        assertEquals("Ridgid", rentalAgreement.getToolBrand());
        assertEquals(9, rentalAgreement.getRentalDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentalAgreement.getCheckoutDate());
        assertEquals(LocalDate.of(2020, 7, 11), rentalAgreement.getDueDate());
        assertEquals(new BigDecimal("2.99"), rentalAgreement.getDailyRentalCharge());
        assertEquals(new BigDecimal("14.95"), rentalAgreement.getPreDiscountCharge());
        assertEquals(50, rentalAgreement.getDiscountPercent());
        assertEquals(new BigDecimal("7.48"), rentalAgreement.getDiscountAmount());
        assertEquals(new BigDecimal("7.47"), rentalAgreement.getFinalCharge());
        assertEquals(5, rentalAgreement.getChargeDays());
    }
}
