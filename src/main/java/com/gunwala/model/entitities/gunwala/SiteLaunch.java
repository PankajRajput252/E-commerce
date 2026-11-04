package com.gunwala.model.entitities.gunwala;

import jakarta.persistence.*;

// Records the one-time "Launch Website" click from the frontend /launch page.
// The table holds at most one row: its primary key is always 1, so a second
// launch attempt can never insert another row.
@Entity
@Table(name = "site_launch")
public class SiteLaunch {
    public static final int SINGLETON_ID = 1;

    @Id
    @Column(name = "SITE_LAUNCH_PK_ID")
    private int siteLaunchPkId = SINGLETON_ID;

    @Column(name = "LAUNCHED_AT")
    private String launchedAt;

    public int getSiteLaunchPkId() {
        return siteLaunchPkId;
    }

    public void setSiteLaunchPkId(int siteLaunchPkId) {
        this.siteLaunchPkId = siteLaunchPkId;
    }

    public String getLaunchedAt() {
        return launchedAt;
    }

    public void setLaunchedAt(String launchedAt) {
        this.launchedAt = launchedAt;
    }
}
