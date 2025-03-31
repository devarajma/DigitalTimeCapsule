package models;

import org.bson.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeCapsule {
    private String userName;
    private String message;
    private LocalDateTime openDateTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TimeCapsule(String userName, String message, LocalDateTime openDateTime) {
        this.userName = userName;
        this.message = message;
        this.openDateTime = openDateTime;
    }

    public Document toDocument() {
        return new Document("userName", userName)
                .append("message", message)
                .append("openDateTime", openDateTime.format(FORMATTER));
    }

    public static TimeCapsule fromDocument(Document doc) {
        String userName = doc.getString("userName");
        String message = doc.getString("message");
        String openDateTimeStr = doc.getString("openDateTime");

        if (openDateTimeStr == null || openDateTimeStr.isEmpty()) {
            System.out.println("⚠️ Warning: Document missing 'openDateTime': " + doc);
            return null; // Prevents crashing on bad data
        }

        LocalDateTime openDateTime;
        try {
            openDateTime = LocalDateTime.parse(openDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            System.out.println("❌ Invalid date format in DB: " + openDateTimeStr);
            return null; // Prevents crashing on bad date formats
        }

        return new TimeCapsule(userName, message, openDateTime);
    }


    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOpenDateTime() {
        return openDateTime;
    }
}
