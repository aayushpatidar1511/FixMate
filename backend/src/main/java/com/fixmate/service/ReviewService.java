package com.fixmate.service;

import com.fixmate.dto.request.ReviewRequest;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ConflictException;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Booking;
import com.fixmate.model.Review;
import com.fixmate.repository.BookingRepository;
import com.fixmate.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Long submitReview(ReviewRequest req, Long customerId) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new BadRequestException("You can only review your own bookings");
        }

        if (!"COMPLETED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BadRequestException("Reviews can only be submitted for COMPLETED services");
        }

        if (reviewRepository.existsByBookingId(req.getBookingId())) {
            throw new ConflictException("A review has already been submitted for this booking");
        }

        Review review = new Review();
        review.setBookingId(booking.getBookingId());
        review.setCustomerId(customerId);
        review.setProviderId(booking.getProviderId());
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        return reviewRepository.save(review);
    }

    public List<Review> getProviderReviews(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }
}
