//
//package models;
//
//import org.bson.Document;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class TimeCapsule {
//    private String userName;
//    private String message;
//    private LocalDateTime openDateTime;
//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    public TimeCapsule(String userName, String message, LocalDateTime openDateTime) {
//        this.userName = userName;
//        this.message = message;
//        this.openDateTime = openDateTime;
//    }
//
//    public Document toDocument() {
//        return new Document("userName", userName)
//                .append("message", message)
//                .append("openDateTime", openDateTime.format(FORMATTER));
//    }
//
//    public static TimeCapsule fromDocument(Document doc) {
//        String userName = doc.getString("userName");
//        String message = doc.getString("message");
//        String openDateTimeStr = doc.getString("openDateTime");
//
//        if (openDateTimeStr == null || openDateTimeStr.isEmpty()) {
//            System.out.println("⚠️ Warning: Document missing 'openDateTime': " + doc);
//            return null; // Prevents crashing on bad data
//        }
//
//        LocalDateTime openDateTime;
//        try {
//            openDateTime = LocalDateTime.parse(openDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
//        } catch (Exception e) {
//            System.out.println("❌ Invalid date format in DB: " + openDateTimeStr);
//            return null; // Prevents crashing on bad date formats
//        }
//
//        return new TimeCapsule(userName, message, openDateTime);
//    }
//
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public LocalDateTime getOpenDateTime() {
//        return openDateTime;
//    }
//}

//Deepseek version
package models;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeCapsule {
    private ObjectId id;
    private String userName;
    private String message;
    private LocalDateTime openDateTime;
    private boolean isOpened;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TimeCapsule(String userName, String message, LocalDateTime openDateTime) {
        this.userName = userName;
        this.message = message;
        this.openDateTime = openDateTime;
        this.isOpened = false;
    }

    public static TimeCapsule fromDocument(Document doc) {
        if (doc == null) return null;

        try {
            ObjectId id = doc.getObjectId("_id");
            String userName = doc.getString("userName");
            String message = doc.getString("message");
            String dateTimeStr = doc.getString("openDateTime");
            Boolean isOpened = doc.getBoolean("isOpened", false);

            if (userName == null || message == null || dateTimeStr == null) {
                return null;
            }

            LocalDateTime openDateTime;
            try {
                openDateTime = LocalDateTime.parse(dateTimeStr, FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + dateTimeStr);
                return null;
            }

            TimeCapsule capsule = new TimeCapsule(userName, message, openDateTime);
            capsule.setId(id);
            capsule.setOpened(isOpened);
            return capsule;
        } catch (Exception e) {
            System.err.println("Error creating TimeCapsule from document: " + e.getMessage());
            return null;
        }
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getUserName() { return userName; }
    public String getMessage() { return message; }
    public LocalDateTime getOpenDateTime() { return openDateTime; }
    public boolean isOpened() { return isOpened; }
    public void setOpened(boolean opened) { isOpened = opened; }
}