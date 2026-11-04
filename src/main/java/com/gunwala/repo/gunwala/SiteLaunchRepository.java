package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.SiteLaunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SiteLaunchRepository extends JpaRepository<SiteLaunch, Integer> {

    // INSERT IGNORE: returns 1 for the click that launched the site, 0 if it was already launched
    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO site_launch (SITE_LAUNCH_PK_ID, LAUNCHED_AT) VALUES (1, :launchedAt)", nativeQuery = true)
    int insertLaunchIfAbsent(String launchedAt);
}
