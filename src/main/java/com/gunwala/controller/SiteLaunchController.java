package com.gunwala.controller;

import com.gunwala.model.Util;
import com.gunwala.model.entitities.gunwala.SiteLaunch;
import com.gunwala.repo.gunwala.SiteLaunchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

// One-time website launch used by the frontend /launch page.
// Under /api/v1/auth/ so it is public (see SecurityConfig).
@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin("*")
public class SiteLaunchController {

    @Autowired
    private SiteLaunchRepository siteLaunchRepository;

    @GetMapping("/launchStatus")
    public Map<String, Object> launchStatus() {
        SiteLaunch launch = siteLaunchRepository.findById(SiteLaunch.SINGLETON_ID).orElse(null);
        return response(launch != null, launch != null ? launch.getLaunchedAt() : null, false);
    }

    // Only the first call launches the site; later calls report it was already launched
    @PostMapping("/launchWebsite")
    public Map<String, Object> launchWebsite() {
        int inserted = siteLaunchRepository.insertLaunchIfAbsent(Util.getCurrentUTCTimestampString());
        SiteLaunch launch = siteLaunchRepository.findById(SiteLaunch.SINGLETON_ID).orElse(null);
        return response(true, launch != null ? launch.getLaunchedAt() : null, inserted == 0);
    }

    private Map<String, Object> response(boolean launched, String launchedAt, boolean alreadyLaunched) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("launched", launched);
        body.put("launchedAt", launchedAt);
        body.put("alreadyLaunched", alreadyLaunched);
        return body;
    }
}
