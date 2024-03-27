package org.example.service;

import org.example.model.RentalAgreement;
import org.example.model.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

public class ToolRentalService {

    private static final Map<String, Tool> TOOL_INFO = new HashMap<>();

    static {
        TOOL_INFO.put("CHNS", new Tool("Chainsaw", "Stihl", new BigDecimal("1.49"), true, false, true));
        TOOL_INFO.put("LADW", new Tool("Ladder", "Werner", new BigDecimal("1.99"), true, true, false));
        TOOL_INFO.put("JAKD", new Tool("Jackhammer", "DeWalt", new BigDecimal("2.99"), true, false, false));
        TOOL_INFO.put("JAKR", new Tool("Jackhammer", "Ridgid", new BigDecimal("2.99"), true, false, false));
    }

     /**
     * Checks out a tool for rental and generates a rental agreement.
     * <p>
     * This method checks out a tool for rental based on the provided parameters
     * and generates a rental agreement containing details such as rental duration,
     * charges, discounts, and final cost.
     * </p>
     *
     * @param toolCode         the code of the tool being rented
     * @param rentalDays       the number of days the tool is rented for
     * @param discountPercent  the discount percentage to be applied
     * @param checkoutDate     the date when the tool is checked out for rental
     * @return a rental agreement containing details of the rental transaction
     * @throws IllegalArgumentException if the tool code is invalid,
     *                                  rental days are less than 1, or
     *                                  discount percentage is out of range (0-100)
     */

    public RentalAgreement checkout(String toolCode, int rentalDays, int discountPercent, LocalDate checkoutDate) {
        Tool tool = TOOL_INFO.get(toolCode);
        if (tool == null) {
            throw new IllegalArgumentException("Invalid tool code: " + toolCode);
        }

        if (rentalDays < 1) {
            throw new IllegalArgumentException("Rental days must be 1 or greater");
        }

        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100");
        }

        LocalDate dueDate = checkoutDate.plusDays(rentalDays);
        int chargeDays = chargeDays(tool, rentalDays, checkoutDate);
        BigDecimal preDiscountCharge = calculatePreDiscountCharge(tool, chargeDays);
        BigDecimal discountAmount = calculateDiscountAmount(preDiscountCharge, discountPercent);
        BigDecimal finalCharge = preDiscountCharge.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        return new RentalAgreement(toolCode, tool.getType(), tool.getBrand(), rentalDays, checkoutDate, dueDate,
                tool.getDailyCharge(), preDiscountCharge, discountPercent, discountAmount, finalCharge, chargeDays);
    }

    /**
     * Calculates the pre-discount charge for renting a tool.
     * <p>
     * This method calculates the total charge for renting a tool before applying any discounts,
     * based on the daily charge of the tool and the number of chargeable days.
     * </p>
     *
     * @param tool        the tool being rented
     * @param chargeDays  the number of chargeable days for renting the tool
     * @return the pre-discount charge for renting the tool
     */
    public BigDecimal calculatePreDiscountCharge(Tool tool, int chargeDays) {


        return tool.getDailyCharge().multiply(BigDecimal.valueOf(chargeDays));
    }

    /**
     * Calculates the number of chargeable days for renting a tool.
     * <p>
     * This method calculates the number of days for which the customer is charged
     * for renting a tool based on the specified rental period, current date, and
     * the tool's charging policies regarding weekends and holidays.
     * </p>
     *
     * @param tool          the tool being rented
     * @param rentalDays    the number of days the tool is rented for
     * @param currentDate   the current date when the rental begins
     * @return the number of chargeable days for renting the tool
     */

    public int chargeDays(Tool tool, int rentalDays, LocalDate currentDate) {
        int  nonChargeDays = 0;
        LocalDate endDate = currentDate.plusDays(rentalDays);

        if(!tool.isWeekendCharge()) {
            nonChargeDays += weekendInRange(currentDate, endDate);
        }
        if(!tool.isHolidayCharge()) {
            nonChargeDays += holidaysInRange(currentDate, endDate);
        }
        return rentalDays - nonChargeDays;
    }

    /**
     * Calculates the discount amount based on the pre-discount charge and discount percentage.
     * <p>
     * This method calculates the amount of discount to be applied to the pre-discount charge
     * based on the specified discount percentage.
     * </p>
     *
     * @param preDiscountCharge  the pre-discount charge for renting the tool
     * @param discountPercent    the discount percentage to be applied
     * @return the amount of discount to be deducted from the pre-discount charge
     */
    public BigDecimal calculateDiscountAmount(BigDecimal preDiscountCharge, int discountPercent) {
        BigDecimal discountMultiplier = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100));
        return preDiscountCharge.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if a given date is a holiday in the United States.
     * <p>
     * This method checks if the specified date is a holiday in the United States,
     * specifically Independence Day (observed on July 4th) and Labor Day
     * (observed on the first Monday in September). It adjusts the observance of
     * Independence Day for weekends.
     * </p>
     *
     * @param date the date to check
     * @return {@code true} if the date is a holiday, {@code false} otherwise
     */
    public boolean isHoliday(LocalDate date) {
        LocalDate independenceDay = LocalDate.of(date.getYear(), 7, 4);

        // Adjust Independence Day for weekend observance
        if (independenceDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
            independenceDay = independenceDay.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        } else if (independenceDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            independenceDay = independenceDay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }

        // Check for Independence Day and Labor Day
        return date.isEqual(independenceDay) ||
                date.getMonth() == Month.SEPTEMBER &&
                        date.getDayOfWeek() == DayOfWeek.MONDAY &&
                        date.with(TemporalAdjusters.firstDayOfMonth()).isBefore(date);
    }

    /**
     * Counts the number of weekend days in the specified date range.
     * <p>
     * This method counts the number of weekend days (Saturdays and Sundays)
     * within the specified date range, inclusive of both the start and end dates.
     * </p>
     *
     * @param startDate the start date of the date range
     * @param endDate   the end date of the date range
     * @return the number of weekend days in the date range
     */
    public  int weekendInRange(LocalDate startDate, LocalDate endDate) {
        int weekendCount = 0;
        // Check if the day is weekend
        while(startDate.isBefore(endDate.plusDays(1))) {
            if(startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                weekendCount++;
            startDate = startDate.plusDays(1);
        }
        return weekendCount;
    }

    /**
     * Counts the number of holidays in the specified date range.
     * <p>
     * This method counts the number of holidays within the specified date range,
     * inclusive of both the start and end dates. It throws an IllegalArgumentException
     * if the start date is after the end date.
     * </p>
     *
     * @param startDate the start date of the date range
     * @param endDate   the end date of the date range
     * @return the number of holidays in the date range
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public int holidaysInRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        int holidayCount = 0;
        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate.plusDays(1))) {
            if (isHoliday(currentDate)) {
                holidayCount++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return holidayCount;
    }

}
