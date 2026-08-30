package com.fixmate.service;

import com.fixmate.dto.request.ComplaintRequest;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Booking;
import com.fixmate.model.Complaint;
import com.fixmate.repository.BookingRepository;
import com.fixmate.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final BookingRepository bookingRepository;

    public ComplaintService(ComplaintRepository complaintRepository, BookingRepository bookingRepository) {
        this.complaintRepository = complaintRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Long fileComplaint(ComplaintRequest req, Long customerId) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new BadRequestException("You can only raise complaints on your own bookings");
        }

        String complaintNumber = "CMP-" + System.currentTimeMillis() % 1000000 + "-" + (100 + new Random().nextInt(900));

        Complaint complaint = new Complaint();
        complaint.setComplaintNumber(complaintNumber);
        complaint.setBookingId(booking.getBookingId());
        complaint.setCustomerId(customerId);
        complaint.setSubject(req.getSubject());
        complaint.setDescription(req.getDescription());
        complaint.setStatus("OPEN");

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getCustomerComplaints(Long customerId) {
        return complaintRepository.findByCustomerId(customerId);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public void updateComplaintStatus(Long complaintId, String status, String remarks) {
        complaintRepository.updateStatus(complaintId, status, remarks);
    }
}
