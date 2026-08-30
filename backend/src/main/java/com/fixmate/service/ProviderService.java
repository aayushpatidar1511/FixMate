package com.fixmate.service;

import com.fixmate.dto.request.SlotConfigDto;
import com.fixmate.dto.response.ProviderCardResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Provider;
import com.fixmate.model.ProviderServiceItem;
import com.fixmate.model.Slot;
import com.fixmate.model.WalletLedger;
import com.fixmate.repository.ProviderRepository;
import com.fixmate.repository.SlotRepository;
import com.fixmate.repository.WalletLedgerRepository;
import com.fixmate.util.GeoUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final SlotRepository slotRepository;
    private final WalletLedgerRepository walletLedgerRepository;

    public ProviderService(ProviderRepository providerRepository,
                           SlotRepository slotRepository,
                           WalletLedgerRepository walletLedgerRepository) {
        this.providerRepository = providerRepository;
        this.slotRepository = slotRepository;
        this.walletLedgerRepository = walletLedgerRepository;
    }

    public List<ProviderCardResponse> searchProviders(String city, Long categoryId, Long serviceId,
                                                      Double lat, Double lon, Double radiusKm) {
        List<Provider> providers;

        if (lat != null && lon != null) {
            providers = providerRepository.findNearby(lat, lon, radiusKm, serviceId, city);
        } else {
            providers = providerRepository.findAllActive(city, categoryId, serviceId);
        }

        List<ProviderCardResponse> cards = new ArrayList<>();
        for (Provider p : providers) {
            cards.add(buildProviderCard(p, lat, lon));
        }
        return cards;
    }

    public ProviderCardResponse getProviderCard(Long providerId) {
        Provider p = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));
        return buildProviderCard(p, null, null);
    }

    private ProviderCardResponse buildProviderCard(Provider p, Double refLat, Double refLon) {
        ProviderCardResponse card = new ProviderCardResponse();
        card.setProviderId(p.getProviderId());
        card.setFullName(p.getFullName());
        card.setBio(p.getBio());
        card.setExperienceYears(p.getExperienceYears());
        card.setCity(p.getCity());
        card.setState(p.getState());
        card.setPincode(p.getPincode());
        card.setRatingAvg(p.getRatingAvg());
        card.setRatingCount(p.getRatingCount());
        card.setTotalCompletedJobs(p.getTotalCompletedJobs());

        if (p.getDistanceKm() != null) {
            card.setDistanceKm(p.getDistanceKm());
        } else if (refLat != null && refLon != null && p.getLatitude() != null && p.getLongitude() != null) {
            double dist = GeoUtils.calculateDistanceKm(
                BigDecimal.valueOf(refLat), BigDecimal.valueOf(refLon),
                p.getLatitude(), p.getLongitude()
            );
            card.setDistanceKm(dist);
        }

        List<ProviderServiceItem> items = providerRepository.findServicesByProviderId(p.getProviderId());
        List<ProviderCardResponse.OfferedServiceDto> offered = new ArrayList<>();
        for (ProviderServiceItem item : items) {
            if (Boolean.TRUE.equals(item.getIsAvailable())) {
                offered.add(new ProviderCardResponse.OfferedServiceDto(
                    item.getServiceId(),
                    item.getServiceName(),
                    item.getCategoryName(),
                    item.getCustomPrice()
                ));
            }
        }
        card.setServices(offered);
        return card;
    }

    public List<Slot> getAvailableSlots(Long providerId, LocalDate date) {
        return slotRepository.findAvailableSlotsForProviderOnDate(providerId, date);
    }

    public void updateSchedule(Long providerId, SlotConfigDto dto) {
        slotRepository.updateProviderSlotsForDay(providerId, dto.getDayOfWeek(), dto.getActiveSlotIds());
    }

    public void setCustomPrice(Long providerId, Long serviceId, BigDecimal price) {
        providerRepository.addOrUpdateService(providerId, serviceId, price);
    }

    public Map<String, Object> getEarningsAndLedger(Long providerId) {
        Provider p = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        List<WalletLedger> ledger = walletLedgerRepository.findByProviderId(providerId);

        Map<String, Object> response = new HashMap<>();
        response.put("providerId", p.getProviderId());
        response.put("fullName", p.getFullName());
        response.put("walletBalance", p.getWalletBalance());
        response.put("totalCompletedJobs", p.getTotalCompletedJobs());
        response.put("ratingAvg", p.getRatingAvg());
        response.put("ledger", ledger);
        return response;
    }
}
