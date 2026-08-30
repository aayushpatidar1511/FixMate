package com.fixmate.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardStatsResponse {
    private Long totalCustomers;
    private Long totalProviders;
    private Long activeProviders;
    private Long pendingProviders;
    private Long totalBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long activeBookings;
    private BigDecimal grossRevenue;
    private BigDecimal platformRevenue;
    private Long openComplaints;

    // Charts data mapping
    private Map<String, Long> bookingsByStatus;
    private Map<String, BigDecimal> monthlyRevenue;

    public DashboardStatsResponse() {}

    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }
    public Long getTotalProviders() { return totalProviders; }
    public void setTotalProviders(Long totalProviders) { this.totalProviders = totalProviders; }
    public Long getActiveProviders() { return activeProviders; }
    public void setActiveProviders(Long activeProviders) { this.activeProviders = activeProviders; }
    public Long getPendingProviders() { return pendingProviders; }
    public void setPendingProviders(Long pendingProviders) { this.pendingProviders = pendingProviders; }
    public Long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Long totalBookings) { this.totalBookings = totalBookings; }
    public Long getCompletedBookings() { return completedBookings; }
    public void setCompletedBookings(Long completedBookings) { this.completedBookings = completedBookings; }
    public Long getCancelledBookings() { return cancelledBookings; }
    public void setCancelledBookings(Long cancelledBookings) { this.cancelledBookings = cancelledBookings; }
    public Long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(Long activeBookings) { this.activeBookings = activeBookings; }
    public BigDecimal getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(BigDecimal grossRevenue) { this.grossRevenue = grossRevenue; }
    public BigDecimal getPlatformRevenue() { return platformRevenue; }
    public void setPlatformRevenue(BigDecimal platformRevenue) { this.platformRevenue = platformRevenue; }
    public Long getOpenComplaints() { return openComplaints; }
    public void setOpenComplaints(Long openComplaints) { this.openComplaints = openComplaints; }
    public Map<String, Long> getBookingsByStatus() { return bookingsByStatus; }
    public void setBookingsByStatus(Map<String, Long> bookingsByStatus) { this.bookingsByStatus = bookingsByStatus; }
    public Map<String, BigDecimal> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(Map<String, BigDecimal> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
}
