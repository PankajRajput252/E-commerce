package com.gunwala.consultation;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/** Request / response shapes for the consultation API. */
public final class ConsultationDtos {
    private ConsultationDtos() {}

    // ─── Topics ─────────────────────────────────────────────────────────────

    public record TopicResponse(Long id, String name, String description, boolean active, int sortOrder) {
        static TopicResponse of(ConsultationTopic t) {
            return new TopicResponse(t.getId(), t.getName(), t.getDescription(), t.isActive(), t.getSortOrder());
        }
    }

    public record TopicRequest(
            @NotBlank @Size(max = 80) String name,
            @Size(max = 300) String description,
            Boolean active,
            @Min(0) @Max(9999) Integer sortOrder
    ) {}

    // ─── Slots ──────────────────────────────────────────────────────────────

    /** What users see: only free, bookable slots. */
    public record SlotResponse(Long id, Instant startsAt, Instant endsAt, int durationMinutes) {
        static SlotResponse of(ConsultationSlot s) {
            return new SlotResponse(s.getId(), s.getStartsAt(), s.getEndsAt(), s.getDurationMinutes());
        }
    }

    /** What admins see: every slot, with who booked it. */
    public record AdminSlotResponse(Long id, Instant startsAt, Instant endsAt, int durationMinutes, boolean booked,
                                    Long bookingId, String bookedByName, String topicName) {}

    /**
     * Generate slots: every selected weekday between fromDate and toDate, from dayStart until dayEnd,
     * each durationMinutes long with breakMinutes between them. Times are in the consultation time zone.
     */
    public record GenerateSlotsRequest(
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            /** 1 = Monday … 7 = Sunday; empty = every day. */
            Set<@Min(1) @Max(7) Integer> weekdays,
            @NotNull LocalTime dayStart,
            @NotNull LocalTime dayEnd,
            @NotNull @Min(10) @Max(240) Integer durationMinutes,
            @Min(0) @Max(120) Integer breakMinutes
    ) {}

    public record GenerateSlotsResponse(int created, int skipped) {}

    // ─── Bookings ───────────────────────────────────────────────────────────

    public record BookRequest(
            @NotNull Long slotId,
            @NotNull Long topicId,
            @Pattern(regexp = "^$|^[+]?[0-9 ]{7,15}$", message = "Enter a valid phone number") String phone,
            @Size(max = 1000) String notes
    ) {}

    public record CancelRequest(@Size(max = 300) String reason) {}

    public record MeetLinkRequest(
            @NotBlank @Size(max = 300)
            @Pattern(regexp = "^https://\\S+$", message = "Enter the full https:// meeting link") String meetLink
    ) {}

    public record BookingResponse(
            Long id,
            Long slotId,
            Instant startsAt,
            Instant endsAt,
            int durationMinutes,
            Long topicId,
            String topicName,
            String status,
            String meetLink,
            String phone,
            String notes,
            String userName,
            String userEmail,
            Instant createdAt,
            Instant cancelledAt,
            String cancelledBy,
            String cancelReason
    ) {
        static BookingResponse of(ConsultationBooking b) {
            return new BookingResponse(b.getId(), b.getSlot() == null ? null : b.getSlot().getId(),
                    b.getStartsAt(), b.getEndsAt(), b.getDurationMinutes(),
                    b.getTopicId(), b.getTopicName(), b.getStatus().name(), b.getMeetLink(), b.getPhone(), b.getNotes(),
                    b.getUserName(), b.getUserEmail(), b.getCreatedAt(), b.getCancelledAt(),
                    b.getCancelledBy() == null ? null : b.getCancelledBy().name(), b.getCancelReason());
        }
    }

    // ─── Google account ─────────────────────────────────────────────────────

    /** configured = OAuth client set on the server; connected = an admin has linked the host Google account. */
    public record GoogleStatusResponse(boolean configured, boolean connected, String accountEmail, Instant connectedAt) {}

    public record AuthorizeUrlResponse(String url) {}
}
