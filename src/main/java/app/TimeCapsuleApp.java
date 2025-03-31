package app;

import database.TimeCapsuleDAO;
import models.TimeCapsule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TimeCapsuleApp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        TimeCapsuleDAO dao = new TimeCapsuleDAO();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🌟 Welcome to the Digital Time Capsule!");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Write your secret message: ");
        String message = scanner.nextLine();

        LocalDateTime openDateTime = null;
        while (openDateTime == null) {
            System.out.print("Enter the date and time to open (YYYY-MM-DD HH:MM): ");
            String inputDate = scanner.nextLine();

            try {
                openDateTime = LocalDateTime.parse(inputDate, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use 'YYYY-MM-DD HH:MM'. Example: 2025-04-01 14:30");
            }
        }

        TimeCapsule capsule = new TimeCapsule(name, message, openDateTime);
        dao.saveCapsule(capsule);
        System.out.println("✅ Your time capsule has been saved!");

        // Check every minute for messages that should be opened
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<TimeCapsule> capsules = dao.getReadyToOpenCapsules();
                for (TimeCapsule cap : capsules) {
                    System.out.println("🔔 Reminder: " + cap.getUserName() + ", your message is ready to be opened!");
                    System.out.println("📜 Message: " + cap.getMessage());
                }
            }
        }, 0, 60 * 1000); // Runs every minute
    }
}
