package com.fixmate.service;

import com.fixmate.config.AppProperties;
import com.fixmate.dto.request.PaymentVerificationRequest;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Booking;
import com.fixmate.model.Payment;
import com.fixmate.payment.MockPaymentGatewayService;
import com.fixmate.payment.PaymentGatewayService;
import com.fixmate.payment.PaymentOrderResponse;
import com.fixmate.payment.RazorpayPaymentGatewayService;
import com.fixmate.repository.BookingRepository;
import com.fixmate.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final MockPaymentGatewayService mockPaymentGateway;
    private final RazorpayPaymentGatewayService razorpayPaymentGateway;
    private final AppProperties appProperties;

    public PaymentService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          MockPaymentGatewayService mockPaymentGateway,
                          RazorpayPaymentGatewayService razorpayPaymentGateway,
                          AppProperties appProperties) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.mockPaymentGateway = mockPaymentGateway;
        this.razorpayPaymentGateway = razorpayPaymentGateway;
        this.appProperties = appProperties;
    }

    private PaymentGatewayService resolveGateway() {
        String mode = appProperties.getPayment().getMode();
        String rzpKey = appProperties.getPayment().getRazorpayKeyId();
        if ("RAZORPAY".equalsIgnoreCase(mode) && rzpKey != null && !rzpKey.trim().isEmpty()) {
            return razorpayPaymentGateway;
        }
        return mockPaymentGateway;
    }

    @Transactional
    public PaymentOrderResponse createPaymentOrder(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new BadRequestException("Booking has already been paid for");
        }

        PaymentGatewayService gateway = resolveGateway();
        PaymentOrderResponse order = gateway.createOrder(bookingId, booking.getTotalAmount(), "INR");

        // Save initial payment intent
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 18).toUpperCase());
        payment.setPaymentMethod("UPI");
        payment.setPaymentGateway(gateway.getGatewayType());
        payment.setGatewayOrderId(order.getOrderId());
        payment.setAmount(booking.getTotalAmount());
        payment.setCurrency("INR");
        payment.setStatus("PENDING");

        paymentRepository.save(payment);

        return order;
    }

    @Transactional
    public boolean verifyPayment(PaymentVerificationRequest req) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Payment payment = paymentRepository.findByBookingId(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment intent not found for booking"));

        PaymentGatewayService gateway = resolveGateway();
        boolean verified = gateway.verifyPaymentSignature(
            req.getRazorpayOrderId() != null ? req.getRazorpayOrderId() : payment.getGatewayOrderId(),
            req.getRazorpayPaymentId() != null ? req.getRazorpayPaymentId() : req.getTransactionReference(),
            req.getRazorpaySignature()
        );

        if (!verified) {
            paymentRepository.updateStatus(payment.getPaymentId(), "FAILED", null);
            bookingRepository.updatePaymentStatus(booking.getBookingId(), "FAILED");
            throw new BadRequestException("Payment verification failed. Invalid gateway signature or transaction state.");
        }

        // Success Transition
        paymentRepository.updateStatus(payment.getPaymentId(), "SUCCESS", LocalDateTime.now());
        bookingRepository.updatePaymentStatus(booking.getBookingId(), "PAID");
        bookingRepository.saveStatusHistory(booking.getBookingId(), booking.getBookingStatus(), booking.getBookingStatus(), null, "Payment successfully confirmed via " + req.getPaymentMethod());

        return true;
    }

    @Transactional
    public boolean simulateMockSuccess(Long bookingId, String method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setTransactionReference("TXN-MOCK-" + System.currentTimeMillis());
        payment.setPaymentMethod(method != null ? method : "UPI");
        payment.setPaymentGateway("MOCK");
        payment.setGatewayOrderId("order_mock_" + System.currentTimeMillis());
        payment.setGatewayPaymentId("pay_mock_" + System.currentTimeMillis());
        payment.setAmount(booking.getTotalAmount());
        payment.setCurrency("INR");
        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        bookingRepository.updatePaymentStatus(booking.getBookingId(), "PAID");
        bookingRepository.saveStatusHistory(booking.getBookingId(), booking.getBookingStatus(), booking.getBookingStatus(), null, "Mock payment completed via " + method);

        return true;
    }
}
