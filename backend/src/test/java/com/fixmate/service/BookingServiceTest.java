package com.fixmate.service;

import com.fixmate.config.AppProperties;
import com.fixmate.dto.request.BookingCreateRequest;
import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ConflictException;
import com.fixmate.model.*;
import com.fixmate.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private SlotRepository slotRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private WalletLedgerRepository walletLedgerRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private AppProperties appProperties;

    @InjectMocks
    private BookingService bookingService;

    private Customer mockCustomer;
    private Provider mockProvider;
    private ServiceEntity mockService;
    private Address mockAddress;
    private Slot mockSlot;
    private AppProperties.Commission mockCommission;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setCustomerId(1L);
        mockCustomer.setUserId(10L);

        mockProvider = new Provider();
        mockProvider.setProviderId(2L);
        mockProvider.setUserId(20L);
        mockProvider.setVerificationStatus("ACTIVE");
        mockProvider.setWalletBalance(new BigDecimal("1000.00"));

        mockService = new ServiceEntity();
        mockService.setServiceId(3L);
        mockService.setServiceName("AC Jet Cleaning");
        mockService.setBasePrice(new BigDecimal("500.00"));

        mockAddress = new Address();
        mockAddress.setAddressId(4L);

        mockSlot = new Slot();
        mockSlot.setSlotId(5L);

        mockCommission = new AppProperties.Commission();
        mockCommission.setPlatformFeePct(10.0);
        mockCommission.setTaxPct(18.0);
    }

    @Test
    @DisplayName("Should successfully calculate pricing quote and create booking")
    void testCreateBookingSuccess() {
        BookingCreateRequest req = new BookingCreateRequest();
        req.setProviderId(2L);
        req.setServiceId(3L);
        req.setAddressId(4L);
        req.setSlotId(5L);
        req.setBookingDate(LocalDate.now().plusDays(1));
        req.setProblemDescription("AC not cooling");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(mockProvider));
        when(serviceRepository.findById(3L)).thenReturn(Optional.of(mockService));
        when(addressRepository.findById(4L)).thenReturn(Optional.of(mockAddress));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(mockSlot));
        when(bookingRepository.isSlotBooked(eq(2L), any(LocalDate.class), eq(5L))).thenReturn(false);
        when(providerRepository.findServicesByProviderId(2L)).thenReturn(Collections.emptyList());
        when(appProperties.getCommission()).thenReturn(mockCommission);
        when(bookingRepository.save(any(Booking.class))).thenReturn(101L);

        BookingSummaryResponse expectedSummary = new BookingSummaryResponse();
        expectedSummary.setBookingId(101L);
        expectedSummary.setTotalAmount(new BigDecimal("559.00"));
        when(bookingRepository.findSummaryById(101L)).thenReturn(Optional.of(expectedSummary));

        BookingSummaryResponse result = bookingService.createBooking(req, 1L);

        assertNotNull(result);
        assertEquals(101L, result.getBookingId());
        assertEquals(new BigDecimal("559.00"), result.getTotalAmount());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when slot is already booked")
    void testSlotCollisionThrowsConflict() {
        BookingCreateRequest req = new BookingCreateRequest();
        req.setProviderId(2L);
        req.setServiceId(3L);
        req.setAddressId(4L);
        req.setSlotId(5L);
        req.setBookingDate(LocalDate.now().plusDays(1));
        req.setProblemDescription("Double booking test");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(mockProvider));
        when(serviceRepository.findById(3L)).thenReturn(Optional.of(mockService));
        when(addressRepository.findById(4L)).thenReturn(Optional.of(mockAddress));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(mockSlot));
        when(bookingRepository.isSlotBooked(eq(2L), any(LocalDate.class), eq(5L))).thenReturn(true);

        assertThrows(ConflictException.class, () -> bookingService.createBooking(req, 1L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject booking if date is in the past")
    void testPastDateBookingThrowsBadRequest() {
        BookingCreateRequest req = new BookingCreateRequest();
        req.setBookingDate(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(req, 1L));
    }

    @Test
    void testPrintBcryptHash() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String generated = enc.encode("Password@123");
        System.out.println("GEN_BCRYPT_HASH=" + generated);
        System.out.println("MATCH_TEST=" + enc.matches("Password@123", "$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC"));
    }
}
