package com.gunwala.service.ServiceImpl;

import com.gunwala.shipRocket.ShiprocketServiceProxy;
import com.gunwala.shipRocket.model.ShipRocketTokenResponse;
import com.gunwala.shipRocket.model.ShiprocketTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ShiprocketTokenManager {

    @Autowired
    private ShiprocketServiceProxy shiprocketServiceProxy;

    @Value("${shiprocket.email}")
    private String email;

    @Value("${shiprocket.password}")
    private String password;

    private String cachedToken = null;
    private long tokenExpiry = 0;

    /**
     * Returns a valid Shiprocket token, refreshing it if expired.
     * Token is valid for 10 days — we refresh 1 hour before expiry.
     */
    public synchronized String getValidToken() {
        long now = System.currentTimeMillis();

        if (cachedToken != null && now < tokenExpiry - 3_600_000L) {
            return cachedToken;
        }

        // ── Build request using YOUR existing DTO ────────────────────────────
        ShiprocketTokenRequest request = new ShiprocketTokenRequest();
        request.setEmail(email);
        request.setPassword(password);

        ShipRocketTokenResponse response = shiprocketServiceProxy.generateOrderTokenResponse(request);

        // ── Adjust getter name to match your DTO ──────────────────────────────
        cachedToken = response.getToken();
        tokenExpiry = now + 864_000_000L; // 10 days

        System.out.println("✅ Shiprocket token refreshed");
        return cachedToken;
    }

    /**
     * Builds the standard auth header map needed for every Shiprocket call.
     */
    public Map<String, String> authHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getValidToken());
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * Force a token refresh — call this if a request fails with 401.
     */
    public synchronized void invalidateToken() {
        cachedToken = null;
        tokenExpiry = 0;
    }
}
