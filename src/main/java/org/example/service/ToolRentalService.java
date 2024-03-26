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

    private BigDecimal calculatePreDiscountCharge(Tool tool, int chargeDays) {


        return tool.getDailyCharge().multiply(BigDecimal.valueOf(chargeDays));
    }

    private int chargeDays(Tool tool, int rentalDays, LocalDate currentDate) {
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

    private BigDecimal calculateDiscountAmount(BigDecimal preDiscountCharge, int discountPercent) {
        BigDecimal discountMultiplier = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100));
        return preDiscountCharge.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     *
     * @param date
     * @return if the give date is a holiday or not
     */
    private boolean isHoliday(LocalDate date) {
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
     *
     * @param startDate
     * @param endDate
     * @return count of weekend days
     */
    private  int weekendInRange(LocalDate startDate, LocalDate endDate) {
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
     *
     * @param startDate
     * @param endDate
     * @return count of holidays in the range of startDate and endDate
     */
    private int holidaysInRange(LocalDate startDate, LocalDate endDate) {
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
