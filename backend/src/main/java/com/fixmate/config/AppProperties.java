package com.fixmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "fixmate")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Commission commission = new Commission();
    private Payment payment = new Payment();
    private Cors cors = new Cors();

    public static class Jwt {
        private String secret = "FixMateSecureTokenSecretKeyWithMoreThan256BitsForSecurityCompliantSigningAlgorithms2026";
        private long expirationMs = 86400000L;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }

    public static class Commission {
        private double platformFeePct = 10.0;
        private double taxPct = 18.0;

        public double getPlatformFeePct() { return platformFeePct; }
        public void setPlatformFeePct(double platformFeePct) { this.platformFeePct = platformFeePct; }
        public double getTaxPct() { return taxPct; }
        public void setTaxPct(double taxPct) { this.taxPct = taxPct; }
    }

    public static class Payment {
        private String mode = "MOCK";
        private String razorpayKeyId = "";
        private String razorpayKeySecret = "";

        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getRazorpayKeyId() { return razorpayKeyId; }
        public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }
        public String getRazorpayKeySecret() { return razorpayKeySecret; }
        public void setRazorpayKeySecret(String razorpayKeySecret) { this.razorpayKeySecret = razorpayKeySecret; }
    }

    public static class Cors {
        private List<String> allowedOrigins = List.of("*");

        public List<String> getAllOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Commission getCommission() { return commission; }
    public void setCommission(Commission commission) { this.commission = commission; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
}
