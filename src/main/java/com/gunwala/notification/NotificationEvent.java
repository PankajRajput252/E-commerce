package com.gunwala.notification;

/**
 * "Something happened in the store that admins should hear about." Publish with {@link NotificationService#send};
 * it is stored for every admin and pushed only after the publishing transaction commits, so rolled-back
 * actions never notify. End users don't get these notifications.
 *
 * <pre>
 * notifications.send(NotificationEvent.toAdmins()
 *         .actor(customerEmail, null)
 *         .category(NotificationEvent.ORDER)
 *         .action("placed an order")
 *         .title("₹1,500 · 2 items")
 *         .reference("Order BW-123").status("Awaiting payment")
 *         .link("/admin/shipments").group("order:BW-123"));
 * </pre>
 */
public final class NotificationEvent {

    public static final String PAYMENT = "PAYMENT";
    public static final String ORDER = "ORDER";
    public static final String SHIPPING = "SHIPPING";
    public static final String CONSULTATION = "CONSULTATION";
    public static final String USER = "USER";
    public static final String LISTING = "LISTING";
    public static final String SUPPORT = "SUPPORT";
    public static final String REVIEW = "REVIEW";

    String actorId;           // nodeId or email of who did it
    String actorName;
    String category = "SYSTEM";
    String action;
    String title;
    String reference;
    String status;
    String link;
    String groupKey;

    private NotificationEvent() {
    }

    /** Every admin. The admin who did it (if it was an admin) is left out. */
    public static NotificationEvent toAdmins() {
        return new NotificationEvent();
    }

    /** Who did it: nodeId or email (resolved to a name if {@code name} is null). Omit for system events. */
    public NotificationEvent actor(String idOrEmail, String name) {
        this.actorId = idOrEmail;
        this.actorName = name;
        return this;
    }

    public NotificationEvent category(String category) {
        this.category = category;
        return this;
    }

    public NotificationEvent action(String action) {
        this.action = action;
        return this;
    }

    public NotificationEvent title(String title) {
        this.title = title;
        return this;
    }

    public NotificationEvent reference(String reference) {
        this.reference = reference;
        return this;
    }

    public NotificationEvent status(String status) {
        this.status = status;
        return this;
    }

    public NotificationEvent link(String link) {
        this.link = link;
        return this;
    }

    public NotificationEvent group(String groupKey) {
        this.groupKey = groupKey;
        return this;
    }

    /** "₹1,500" / "₹1,500.50" in Indian digit grouping. */
    public static String inr(java.math.BigDecimal amount) {
        if (amount == null) return "₹0";
        java.text.NumberFormat f = java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("en-IN"));
        f.setMinimumFractionDigits(amount.stripTrailingZeros().scale() > 0 ? 2 : 0);
        f.setMaximumFractionDigits(2);
        return "₹" + f.format(amount);
    }
}
