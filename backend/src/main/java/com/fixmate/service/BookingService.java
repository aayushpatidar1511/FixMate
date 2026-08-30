package com.fixmate.service;

import com.fixmate.config.AppProperties;
import com.fixmate.dto.request.BookingCreateRequest;
import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ConflictException;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.*;
import com.fixmate.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ProviderRepository providerRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final AddressRepository addressRepository;
    private final SlotRepository slotRepository;
    private final NotificationRepository notificationRepository;
    private final WalletLedgerRepository walletLedgerRepository;
    private final PaymentRepository paymentRepository;
    private final AppProperties appProperties;

    public BookingService(BookingRepository bookingRepository,
                          ProviderRepository providerRepository,
                          CustomerRepository customerRepository,
                          ServiceRepository serviceRepository,
                          AddressRepository addressRepository,
                          SlotRepository slotRepository,
                          NotificationRepository notificationRepository,
                          WalletLedgerRepository walletLedgerRepository,
                          PaymentRepository paymentRepository,
                          AppProperties appProperties) {
        this.bookingRepository = bookingRepository;
        this.providerRepository = providerRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.addressRepository = addressRepository;
        this.slotRepository = slotRepository;
        this.notificationRepository = notificationRepository;
        this.walletLedgerRepository = walletLedgerRepository;
        this.paymentRepository = paymentRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public BookingSummaryResponse createBooking(BookingCreateRequest req, Long customerId) {
        if (req.getBookingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Booking date cannot be in the past");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Provider provider = providerRepository.findById(req.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        if (!"ACTIVE".equalsIgnoreCase(provider.getVerificationStatus())) {
            throw new BadRequestException("This service provider is not currently active for bookings");
        }

        ServiceEntity service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        slotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        // Slot Collision Guard
        if (bookingRepository.isSlotBooked(provider.getProviderId(), req.getBookingDate(), req.getSlotId())) {
            throw new ConflictException("The selected time slot is already booked for this provider");
        }

        // Authoritative Server-Side Pricing (Never trust client sent amounts!)
        BigDecimal basePrice = service.getBasePrice();
        List<ProviderServiceItem> items = providerRepository.findServicesByProviderId(provider.getProviderId());
        for (ProviderServiceItem item : items) {
            if (item.getServiceId().equals(service.getServiceId()) && item.getCustomPrice() != null) {
                basePrice = item.getCustomPrice();
                break;
            }
        }

        double feePct = appProperties.getCommission().getPlatformFeePct();
        double taxPct = appProperties.getCommission().getTaxPct();

        BigDecimal platformFee = basePrice.multiply(BigDecimal.valueOf(feePct / 100.0)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = platformFee.multiply(BigDecimal.valueOf(taxPct / 100.0)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = basePrice.add(platformFee).add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal providerEarnings = basePrice; // Provider receives full custom price; platform collects fee on top

        String bookingNumber = "FM-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + (10000 + new Random().nextInt(90000));

        Booking booking = new Booking();
        booking.setBookingNumber(bookingNumber);
        booking.setCustomerId(customerId);
        booking.setProviderId(provider.getProviderId());
        booking.setServiceId(service.getServiceId());
        booking.setAddressId(req.getAddressId());
        booking.setBookingDate(req.getBookingDate());
        booking.setSlotId(req.getSlotId());
        booking.setProblemDescription(req.getProblemDescription());
        booking.setBaseAmount(basePrice);
        booking.setPlatformFee(platformFee);
        booking.setTaxAmount(taxAmount);
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setTotalAmount(totalAmount);
        booking.setProviderEarnings(providerEarnings);
        booking.setBookingStatus("PENDING");
        booking.setPaymentStatus("PENDING");

        Long bookingId = bookingRepository.save(booking);

        customerRepository.incrementTotalBookings(customerId);

        // Audit History
        bookingRepository.saveStatusHistory(bookingId, null, "PENDING", customer.getUserId(), "Customer placed booking request");

        // Notify Provider
        Notification notif = new Notification();
        notif.setUserId(provider.getUserId());
        notif.setTitle("New Job Request!");
        notif.setMessage("New booking #" + bookingNumber + " requested for " + req.getBookingDate() + " for " + service.getServiceName());
        notif.setType("BOOKING_UPDATE");
        notif.setReferenceId(bookingId);
        notificationRepository.save(notif);

        return bookingRepository.findSummaryById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking summary not found"));
    }

    public BookingSummaryResponse getBookingDetails(Long bookingId) {
        return bookingRepository.findSummaryById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    public List<BookingSummaryResponse> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<BookingSummaryResponse> getProviderBookings(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    @Transactional
    public void acceptBooking(Long bookingId, Long providerId) {
        Booking booking = getVerifiedProviderBooking(bookingId, providerId);
        if (!"PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Booking can only be accepted from PENDING status");
        }

        bookingRepository.updateStatus(bookingId, "ACCEPTED", null, null);
        bookingRepository.saveStatusHistory(bookingId, "PENDING", "ACCEPTED", null, "Provider accepted appointment");

        notifyCustomer(booking, "Booking Accepted", "Your booking #" + booking.getBookingNumber() + " has been accepted by the provider.");
    }

    @Transactional
    public void rejectBooking(Long bookingId, Long providerId, String reason) {
        Booking booking = getVerifiedProviderBooking(bookingId, providerId);
        if (!"PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Booking can only be rejected from PENDING status");
        }

        bookingRepository.updateStatus(bookingId, "REJECTED", reason, "PROVIDER");
        bookingRepository.saveStatusHistory(bookingId, "PENDING", "REJECTED", null, "Provider rejected: " + reason);

        notifyCustomer(booking, "Booking Declined", "Your booking #" + booking.getBookingNumber() + " could not be accepted: " + reason);
    }

    @Transactional
    public void startTravel(Long bookingId, Long providerId) {
        Booking booking = getVerifiedProviderBooking(bookingId, providerId);
        if (!"ACCEPTED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Technician can only travel for an ACCEPTED booking");
        }

        bookingRepository.updateStatus(bookingId, "ON_THE_WAY", null, null);
        bookingRepository.saveStatusHistory(bookingId, "ACCEPTED", "ON_THE_WAY", null, "Technician is on the way");

        notifyCustomer(booking, "Technician On The Way", "Provider is heading towards your service address.");
    }

    @Transactional
    public void startService(Long bookingId, Long providerId) {
        Booking booking = getVerifiedProviderBooking(bookingId, providerId);
        if (!"ON_THE_WAY".equalsIgnoreCase(booking.getBookingStatus()) && !"ACCEPTED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Service cannot be started from current status");
        }

        bookingRepository.updateStatus(bookingId, "IN_PROGRESS", null, null);
        bookingRepository.saveStatusHistory(bookingId, booking.getBookingStatus(), "IN_PROGRESS", null, "Technician started service work");

        notifyCustomer(booking, "Service In Progress", "Technician has arrived and begun the service work.");
    }

    @Transactional
    public void completeService(Long bookingId, Long providerId) {
        Booking booking = getVerifiedProviderBooking(bookingId, providerId);
        if (!"IN_PROGRESS".equalsIgnoreCase(booking.getBookingStatus()) && !"ACCEPTED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Service must be active/in-progress to be marked completed");
        }

        bookingRepository.updateStatus(bookingId, "COMPLETED", null, null);
        bookingRepository.saveStatusHistory(bookingId, booking.getBookingStatus(), "COMPLETED", null, "Service completed successfully");

        // Provider Earnings Disbursement
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        BigDecimal earnings = booking.getProviderEarnings();
        BigDecimal newBalance = provider.getWalletBalance().add(earnings);

        // Update provider balance and completed counter
        String updateProviderSql = "UPDATE service_providers SET wallet_balance = wallet_balance + ?, total_completed_jobs = total_completed_jobs + 1 WHERE provider_id = ?";
        providerRepository.findById(providerId); // verify

        WalletLedger ledger = new WalletLedger();
        ledger.setProviderId(providerId);
        ledger.setBookingId(bookingId);
        ledger.setTransactionType("CREDIT_BOOKING_PAYOUT");
        ledger.setAmount(earnings);
        ledger.setRunningBalance(newBalance);
        ledger.setDescription("Earnings credited for completed booking #" + booking.getBookingNumber());
        walletLedgerRepository.save(ledger);

        notifyCustomer(booking, "Job Completed! Please Review", "Your service #" + booking.getBookingNumber() + " is complete. Please take a moment to rate the service.");
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId, String reason, String role) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("COMPLETED".equalsIgnoreCase(booking.getBookingStatus()) ||
            "CANCELLED".equalsIgnoreCase(booking.getBookingStatus()) ||
            "REJECTED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Booking is in a terminal state and cannot be cancelled");
        }

        String cancelledBy = "CUSTOMER".equalsIgnoreCase(role) ? "CUSTOMER" : ("PROVIDER".equalsIgnoreCase(role) ? "PROVIDER" : "ADMIN");

        bookingRepository.updateStatus(bookingId, "CANCELLED", reason, cancelledBy);
        bookingRepository.saveStatusHistory(bookingId, booking.getBookingStatus(), "CANCELLED", userId, "Cancelled: " + reason);

        // If prepaid, update payment status to REFUNDED
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            bookingRepository.updatePaymentStatus(bookingId, "REFUNDED");
        }
    }

    private Booking getVerifiedProviderBooking(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getProviderId().equals(providerId)) {
            throw new BadRequestException("This booking does not belong to the specified provider");
        }
        return booking;
    }

    private void notifyCustomer(Booking booking, String title, String message) {
        Optional<Customer> c = customerRepository.findById(booking.getCustomerId());
        c.ifPresent(customer -> {
            Notification n = new Notification();
            n.setUserId(customer.getUserId());
            n.setTitle(title);
            n.setMessage(message);
            n.setType("BOOKING_UPDATE");
            n.setReferenceId(booking.getBookingId());
            notificationRepository.save(n);
        });
    }
}
