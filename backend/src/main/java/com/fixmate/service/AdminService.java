package com.fixmate.service;

import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.dto.response.DashboardStatsResponse;
import com.fixmate.model.Complaint;
import com.fixmate.model.Customer;
import com.fixmate.model.Provider;
import com.fixmate.repository.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public AdminService(CustomerRepository customerRepository,
                        ProviderRepository providerRepository,
                        BookingRepository bookingRepository,
                        PaymentRepository paymentRepository,
                        ComplaintRepository complaintRepository,
                        UserRepository userRepository,
                        JdbcTemplate jdbcTemplate) {
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalCustomers(customerRepository.count());
        stats.setTotalProviders(providerRepository.count());
        stats.setActiveProviders(providerRepository.countByVerificationStatus("ACTIVE"));
        stats.setPendingProviders(providerRepository.countByVerificationStatus("PENDING_VERIFICATION"));

        stats.setTotalBookings(bookingRepository.count());
        stats.setCompletedBookings(bookingRepository.countByStatus("COMPLETED"));
        stats.setCancelledBookings(bookingRepository.countByStatus("CANCELLED"));
        stats.setActiveBookings(bookingRepository.count() - stats.getCompletedBookings() - stats.getCancelledBookings());

        BigDecimal gross = paymentRepository.sumRevenue();
        stats.setGrossRevenue(gross);
        stats.setPlatformRevenue(gross.multiply(new BigDecimal("0.10"))); // 10% platform take-rate

        stats.setOpenComplaints(complaintRepository.countByStatus("OPEN"));

        // Status Map
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("COMPLETED", stats.getCompletedBookings());
        statusMap.put("CANCELLED", stats.getCancelledBookings());
        statusMap.put("ACTIVE", stats.getActiveBookings());
        stats.setBookingsByStatus(statusMap);

        // Monthly Revenue Query
        Map<String, BigDecimal> monthly = new HashMap<>();
        String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') as m, SUM(total_amount) as total FROM bookings WHERE booking_status = 'COMPLETED' GROUP BY m ORDER BY m DESC LIMIT 6";
        jdbcTemplate.query(sql, (rs) -> {
            monthly.put(rs.getString("m"), rs.getBigDecimal("total"));
        });
        stats.setMonthlyRevenue(monthly);

        return stats;
    }

    public void updateProviderVerification(Long providerId, String status) {
        providerRepository.updateVerificationStatus(providerId, status);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public List<BookingSummaryResponse> getAllBookings(String status) {
        return bookingRepository.findAll(status);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public void updateUserStatus(Long userId, String status) {
        userRepository.updateStatus(userId, status);
    }
}
