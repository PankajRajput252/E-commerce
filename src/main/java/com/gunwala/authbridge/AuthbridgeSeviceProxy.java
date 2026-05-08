package com.gunwala.authbridge;

import com.gunwala.authbridge.model.AuthbridgeReportDetail;
import com.gunwala.authbridge.model.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="authbridge",url="https://api-staging.authbridge.com")
public interface AuthbridgeSeviceProxy {

    @GetMapping("/v2/AuthApi/generate_token")
    public TokenResponse generateToken(@RequestHeader ("username") String username, @RequestHeader ("Password") String Password);

    @GetMapping("/v2/AuthApi/get_details/{requestId}")
    AuthbridgeReportDetail generateAuthbridgeReportDetail(@RequestHeader("username") String username, @RequestHeader("token") String token, @PathVariable String requestId);
}
