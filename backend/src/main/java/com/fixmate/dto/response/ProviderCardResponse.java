package com.fixmate.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ProviderCardResponse {
    private Long providerId;
    private String fullName;
    private String bio;
    private Integer experienceYears;
    private String city;
    private String state;
    private String pincode;
    private BigDecimal ratingAvg;
    private Integer ratingCount;
    private Integer totalCompletedJobs;
    private Double distanceKm;

    // Services Offered
    private List<OfferedServiceDto> services;

    public static class OfferedServiceDto {
        private Long serviceId;
        private String serviceName;
        private String categoryName;
        private BigDecimal customPrice;

        public OfferedServiceDto() {}
        public OfferedServiceDto(Long serviceId, String serviceName, String categoryName, BigDecimal customPrice) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.categoryName = categoryName;
            this.customPrice = customPrice;
        }

        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public BigDecimal getCustomPrice() { return customPrice; }
        public void setCustomPrice(BigDecimal customPrice) { this.customPrice = customPrice; }
    }

    public ProviderCardResponse() {}

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg; }
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
    public Integer getTotalCompletedJobs() { return totalCompletedJobs; }
    public void setTotalCompletedJobs(Integer totalCompletedJobs) { this.totalCompletedJobs = totalCompletedJobs; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public List<OfferedServiceDto> getServices() { return services; }
    public void setServices(List<OfferedServiceDto> services) { this.services = services; }
}
