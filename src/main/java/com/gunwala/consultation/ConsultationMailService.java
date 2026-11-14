package com.gunwala.consultation;

import jakarta.annotation.PreDestroy;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Consultation emails to the user (and a copy to the admin inbox). Sent on a background thread. */
@Slf4j
@Service
public class ConsultationMailService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final JavaMailSender mailSender;
    private final String from;
    private final String adminEmail;
    private final String frontendUrl;
    private final ZoneId zone;
    private final ExecutorService sender = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "consultation-mail");
        t.setDaemon(true);
        return t;
    });

    public ConsultationMailService(JavaMailSender mailSender,
                                   @Value("${spring.mail.username:}") String from,
                                   @Value("${consultation.admin-email:${spring.mail.username:}}") String adminEmail,
                                   @Value("${consultation.frontend-url:https://bandookwale.com}") String frontendUrl,
                                   @Value("${consultation.timezone:Asia/Kolkata}") String timezone) {
        this.mailSender = mailSender;
        this.from = from;
        this.adminEmail = adminEmail;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.zone = ZoneId.of(timezone);
    }

    @PreDestroy
    void shutdown() {
        sender.shutdown();
    }

    // ============================================================ emails

    public void bookingConfirmed(ConsultationBooking b) {
        String when = when(b);
        String meet = StringUtils.hasText(b.getMeetLink())
                ? button(b.getMeetLink(), "Join Google Meet")
                  + p("Meeting link: <a href=\"" + attr(b.getMeetLink()) + "\" style=\"color:#a87912\">" + esc(b.getMeetLink()) + "</a>")
                : p("<b>Your Google Meet link is on its way</b> — we'll email it to you before the consultation.");

        send(b.getUserEmail(),
                "Consultation booked: " + b.getTopicName() + " on " + shortWhen(b),
                layout("Your consultation is booked",
                        p("Hi " + esc(firstName(b)) + ",")
                                + p("Your consultation with Bandookwale is confirmed. Here are the details:")
                                + details(b, when)
                                + meet
                                + p("Please join a couple of minutes early. You'll also get a Google Calendar invite.")
                                + p("Can't make it? Cancel from <a href=\"" + attr(frontendUrl) + "/?view=consultation\" style=\"color:#a87912\">"
                                + "My consultations</a> on the website so someone else can take the slot.")));

        if (StringUtils.hasText(adminEmail)) {
            send(adminEmail,
                    "New consultation: " + b.getTopicName() + " — " + shortWhen(b),
                    layout("New consultation booking",
                            details(b, when)
                                    + p("Email: " + esc(b.getUserEmail()) + (StringUtils.hasText(b.getPhone()) ? "<br>Phone: " + esc(b.getPhone()) : ""))
                                    + (StringUtils.hasText(b.getNotes()) ? p("<b>Notes from the user:</b><br>" + esc(b.getNotes()).replace("\n", "<br>")) : "")
                                    + (StringUtils.hasText(b.getMeetLink()) ? p("Meet: <a href=\"" + attr(b.getMeetLink()) + "\">" + esc(b.getMeetLink()) + "</a>")
                                    : p("<b style=\"color:#b91c1c\">No Meet link yet.</b> Add one from Admin → Consultations."))));
        }
    }

    public void meetLinkReady(ConsultationBooking b) {
        send(b.getUserEmail(),
                "Google Meet link for your consultation on " + shortWhen(b),
                layout("Your Google Meet link",
                        p("Hi " + esc(firstName(b)) + ",")
                                + p("Here is the link for your consultation:")
                                + details(b, when(b))
                                + button(b.getMeetLink(), "Join Google Meet")
                                + p("Meeting link: <a href=\"" + attr(b.getMeetLink()) + "\" style=\"color:#a87912\">" + esc(b.getMeetLink()) + "</a>")));
    }

    public void bookingCancelled(ConsultationBooking b) {
        boolean byUser = b.getCancelledBy() == ConsultationBooking.CancelledBy.USER;
        send(b.getUserEmail(),
                "Consultation cancelled: " + b.getTopicName() + " on " + shortWhen(b),
                layout("Consultation cancelled",
                        p("Hi " + esc(firstName(b)) + ",")
                                + p(byUser ? "As requested, your consultation has been cancelled."
                                : "We're sorry — we had to cancel your consultation.")
                                + details(b, when(b))
                                + (!byUser && StringUtils.hasText(b.getCancelReason()) ? p("<b>Reason:</b> " + esc(b.getCancelReason())) : "")
                                + p("You can book another time whenever you like:")
                                + button(frontendUrl + "/?view=consultation", "Book a consultation")));

        if (byUser && StringUtils.hasText(adminEmail)) {
            send(adminEmail,
                    "Consultation cancelled by user: " + b.getTopicName() + " — " + shortWhen(b),
                    layout("A user cancelled their consultation", details(b, when(b)) + p("The slot is free again.")));
        }
    }

    // ============================================================ sending

    private void send(String to, String subject, String html) {
        if (!StringUtils.hasText(to)) return;
        sender.execute(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                if (StringUtils.hasText(from)) helper.setFrom(from, "Bandookwale");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(html, true);
                mailSender.send(message);
            } catch (Exception e) {
                log.warn("Consultation email \"{}\" to {} failed: {}", subject, to, e.getMessage());
            }
        });
    }

    // ============================================================ HTML

    private String when(ConsultationBooking b) {
        var start = b.getStartsAt().atZone(zone);
        var end = b.getEndsAt().atZone(zone);
        return DATE.format(start) + "<br>" + TIME.format(start) + " – " + TIME.format(end) + " " + zoneLabel();
    }

    private String shortWhen(ConsultationBooking b) {
        var start = b.getStartsAt().atZone(zone);
        return DateTimeFormatter.ofPattern("d MMM, h:mm a", Locale.ENGLISH).format(start) + " " + zoneLabel();
    }

    private String zoneLabel() {
        return "Asia/Kolkata".equals(zone.getId()) ? "IST" : zone.getId();
    }

    private String details(ConsultationBooking b, String when) {
        return "<table role=\"presentation\" style=\"width:100%;border-collapse:collapse;margin:16px 0;background:#fdf8ec;border-radius:10px\">"
                + row("Topic", esc(b.getTopicName()))
                + row("When", when)
                + row("Duration", b.getDurationMinutes() + " minutes")
                + (StringUtils.hasText(b.getUserName()) ? row("Name", esc(b.getUserName())) : "")
                + row("Booking no.", "#" + b.getId())
                + "</table>";
    }

    private static String row(String label, String valueHtml) {
        return "<tr><td style=\"padding:8px 14px;color:#6b7280;font-size:13px;width:110px;vertical-align:top\">" + label
                + "</td><td style=\"padding:8px 14px;color:#111827;font-size:14px;font-weight:600\">" + valueHtml + "</td></tr>";
    }

    private static String button(String href, String text) {
        return "<p style=\"margin:20px 0\"><a href=\"" + attr(href) + "\" style=\"display:inline-block;background:#c9931a;color:#ffffff;"
                + "text-decoration:none;font-weight:600;padding:12px 22px;border-radius:10px;font-size:15px\">" + esc(text) + "</a></p>";
    }

    private static String p(String html) {
        return "<p style=\"margin:0 0 12px;color:#374151;font-size:14px;line-height:1.6\">" + html + "</p>";
    }

    private static String layout(String heading, String content) {
        return "<!doctype html><html><body style=\"margin:0;background:#f3f4f6;font-family:Arial,Helvetica,sans-serif\">"
                + "<div style=\"max-width:560px;margin:0 auto;padding:24px 16px\">"
                + "<div style=\"background:#111827;border-radius:14px 14px 0 0;padding:18px 24px;color:#e0ab2c;font-size:18px;font-weight:700\">Bandookwale</div>"
                + "<div style=\"background:#ffffff;border-radius:0 0 14px 14px;padding:24px\">"
                + "<h1 style=\"margin:0 0 16px;font-size:20px;color:#111827\">" + esc(heading) + "</h1>"
                + content
                + "</div><p style=\"text-align:center;color:#9ca3af;font-size:12px;margin-top:16px\">This email was sent because a consultation was booked on Bandookwale.</p>"
                + "</div></body></html>";
    }

    private static String firstName(ConsultationBooking b) {
        String n = b.getUserName();
        return StringUtils.hasText(n) ? n.trim().split("\\s+")[0] : "there";
    }

    private static String esc(String s) {
        return s == null ? "" : HtmlUtils.htmlEscape(s);
    }

    private static String attr(String s) {
        return esc(s).replace("\"", "&quot;");
    }
}
