package com.gunwala.entity;

/** Where the order is in the Shiprocket flow. Each step is saved, so a retry resumes from the last one that worked. */
public enum ShippingStatus {
    /** Not paid yet, or auto-ship is switched off. */
    NOT_STARTED,
    /** Contains licence-required items: never sent to Shiprocket automatically; the team handles it offline. */
    MANUAL_REVIEW,
    /** A Shiprocket call is in progress. */
    PROCESSING,
    /** Order exists in Shiprocket (has shiprocketOrderId + shipmentId). */
    ORDER_CREATED,
    /** Courier assigned, AWB (tracking number) generated. */
    AWB_ASSIGNED,
    /** Pickup requested from the courier. Final automatic step. */
    PICKUP_SCHEDULED,
    /** A step failed; see shippingError. Admin can retry. */
    FAILED
}
