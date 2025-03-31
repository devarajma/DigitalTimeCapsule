//package ui;
//
//import database.TimeCapsuleDAO;
//import models.TimeCapsule;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import javax.sound.sampled.*;
//import java.io.IOException;
//import java.net.URL;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Objects;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MainUI extends Application {
//    private final TimeCapsuleDAO dao = new TimeCapsuleDAO();
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("📜 Digital Time Capsule");
//
//        // UI Elements
//        TextField nameField = new TextField();
//        nameField.setPromptText("Enter your name");
//
//        TextArea messageField = new TextArea();
//        messageField.setPromptText("Write your secret message...");
//
//        DatePicker datePicker = new DatePicker();
//        TextField timeField = new TextField();
//        timeField.setPromptText("HH:MM (24-hour format)");
//
//        Button saveButton = new Button("💾 Save Capsule");
//        Button checkButton = new Button("🔍 Check Messages");
//        Label statusLabel = new Label();
//
//        // Save Button Action
//        saveButton.setOnAction(e -> {
//            String name = nameField.getText().trim();
//            String message = messageField.getText().trim();
//            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
//            String time = timeField.getText().trim();
//
//            if (name.isEmpty() || message.isEmpty() || date.isEmpty() || time.isEmpty()) {
//                statusLabel.setText("❌ Please fill all fields!");
//                return;
//            }
//
//            try {
//                LocalDateTime openDateTime = LocalDateTime.parse(date + " " + time, formatter);
//                TimeCapsule capsule = new TimeCapsule(name, message, openDateTime);
//                dao.saveCapsule(capsule);
//                statusLabel.setText("✅ Time capsule saved!");
//            } catch (Exception ex) {
//                statusLabel.setText("❌ Invalid date/time format!");
//            }
//        });
//
//        // Check Messages Button Action
//        checkButton.setOnAction(e -> checkAndShowMessages());
//
//        // Auto-check every minute
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> checkAndShowMessages());  // Run on JavaFX thread
//            }
//        }, 0, 60 * 1000); // Runs every minute
//
//        // Layout
//        VBox layout = new VBox(10, nameField, messageField, datePicker, timeField, saveButton, checkButton, statusLabel);
//        layout.setPadding(new Insets(20));
//
//        Scene scene = new Scene(layout, 400, 400);
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ui/styles.css")).toExternalForm()); // Load custom styles
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    // Check messages and show a pop-up with a beep sound
//    private void checkAndShowMessages() {
//        List<TimeCapsule> capsules = dao.getReadyToOpenCapsules();
//        if (!capsules.isEmpty()) {
//            StringBuilder messages = new StringBuilder("📜 Opened Messages:\n");
//            for (TimeCapsule capsule : capsules) {
//                messages.append("\n🔔 From: ").append(capsule.getUserName())
//                        .append("\n📖 ").append(capsule.getMessage()).append("\n");
//            }
//            playBeepSound();
//            showPopup(messages.toString());
//        }
//    }
//
//    // Play beep sound
//    private void playBeepSound() {
//        try {
//            URL soundURL = getClass().getResource("/beep.wav");  // Corrected file path
//            if (soundURL == null) {
//                System.out.println("❌ Beep sound file not found!");
//                return;
//            }
//
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
//            Clip clip = AudioSystem.getClip();
//            clip.open(audioInputStream);
//            clip.start();
//        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//            System.out.println("❌ Error playing beep sound: " + e.getMessage());
//        }
//    }
//
//    // Show pop-up alert (Fixed JavaFX thread issue)
//    private void showPopup(String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//            alert.setTitle("🔔 Time Capsule Reminder");
//            alert.setHeaderText("🎉 Your Messages are Ready!");
//            alert.show();
//        });
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}



package ui;

import database.TimeCapsuleDAO;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import models.TimeCapsule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainUI extends Application {
    private final TimeCapsuleDAO dao = new TimeCapsuleDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Set<LocalDateTime> notifiedCapsules = new HashSet<>(); // Track already notified capsules

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("📜 Digital Time Capsule");

        // UI Elements
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        TextArea messageField = new TextArea();
        messageField.setPromptText("Write your secret message...");

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("HH:MM (24-hour format)");

        Button saveButton = new Button("💾 Save Capsule");
        Button checkButton = new Button("🔍 Check Messages");
        Label statusLabel = new Label();

        // Save Button Action
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String message = messageField.getText().trim();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            String time = timeField.getText().trim();

            if (name.isEmpty() || message.isEmpty() || date.isEmpty() || time.isEmpty()) {
                statusLabel.setText("❌ Please fill all fields!");
                return;
            }

            try {
                LocalDateTime openDateTime = LocalDateTime.parse(date + " " + time, formatter);
                TimeCapsule capsule = new TimeCapsule(name, message, openDateTime);
                dao.saveCapsule(capsule);
                statusLabel.setText("✅ Time capsule saved!");

                // Hide status message after 2 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> statusLabel.setText(""));
                pause.play();
            } catch (Exception ex) {
                statusLabel.setText("❌ Invalid date/time format!");
            }
        });


        // Check Messages Button Action (manual check, no beep)
        checkButton.setOnAction(e -> showMessages(false));

        // Auto-check every minute (only plays beep for new messages)
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showMessages(true)); // Only beeps for new reminders
            }
        }, 0, 60 * 1000); // Runs every minute

        // Layout
        VBox layout = new VBox(10, nameField, messageField, datePicker, timeField, saveButton, checkButton, statusLabel);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ui/styles.css")).toExternalForm()); // Load custom styles
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Show messages (with optional beep sound for new reminders)
    private void showMessages(boolean playSound) {
        List<TimeCapsule> capsules = dao.getReadyToOpenCapsules();
        StringBuilder messages = new StringBuilder();

        boolean newReminder = false; // Track if there's a new reminder

        for (TimeCapsule capsule : capsules) {
            if (!notifiedCapsules.contains(capsule.getOpenDateTime())) { // New message detected
                newReminder = true;
                notifiedCapsules.add(capsule.getOpenDateTime()); // Mark as notified
            }
            messages.append("\n🔔 From: ").append(capsule.getUserName())
                    .append("\n📖 ").append(capsule.getMessage()).append("\n");
        }

        if (messages.length() > 0) {
            showPopup("📜 Opened Messages:\n" + messages);
            if (playSound && newReminder) { // Only beep if it's a new reminder
                playBeepSound();
            }
        }
    }

    // Play beep sound
    private void playBeepSound() {
        try {
            URL soundURL = getClass().getResource("/beep.wav");  // Corrected file path
            if (soundURL == null) {
                System.out.println("❌ Beep sound file not found!");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("❌ Error playing beep sound: " + e.getMessage());
        }
    }

    // Show pop-up alert (Fixed JavaFX thread issue)
    private void showPopup(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle("🔔 Time Capsule Reminder");
            alert.setHeaderText("🎉 Your Messages are Ready!");
            alert.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
