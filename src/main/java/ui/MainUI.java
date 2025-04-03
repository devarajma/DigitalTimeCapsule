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


//
//package ui;
//
//import database.TimeCapsuleDAO;
//import javafx.animation.PauseTransition;
//import javafx.util.Duration;
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
//import java.util.*;
//
//public class MainUI extends Application {
//    private final TimeCapsuleDAO dao = new TimeCapsuleDAO();
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//    private final Set<LocalDateTime> notifiedCapsules = new HashSet<>(); // Track already notified capsules
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
//
//                // Hide status message after 2 seconds
//                PauseTransition pause = new PauseTransition(Duration.seconds(2));
//                pause.setOnFinished(event -> statusLabel.setText(""));
//                pause.play();
//            } catch (Exception ex) {
//                statusLabel.setText("❌ Invalid date/time format!");
//            }
//        });
//
//
//        // Check Messages Button Action (manual check, no beep)
//        checkButton.setOnAction(e -> showMessages(false));
//
//        // Auto-check every minute (only plays beep for new messages)
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> showMessages(true)); // Only beeps for new reminders
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
//    // Show messages (with optional beep sound for new reminders)
//    private void showMessages(boolean playSound) {
//        List<TimeCapsule> capsules = dao.getReadyToOpenCapsules();
//        StringBuilder messages = new StringBuilder();
//
//        boolean newReminder = false; // Track if there's a new reminder
//
//        for (TimeCapsule capsule : capsules) {
//            if (!notifiedCapsules.contains(capsule.getOpenDateTime())) { // New message detected
//                newReminder = true;
//                notifiedCapsules.add(capsule.getOpenDateTime()); // Mark as notified
//            }
//            messages.append("\n🔔 From: ").append(capsule.getUserName())
//                    .append("\n📖 ").append(capsule.getMessage()).append("\n");
//        }
//
//        if (messages.length() > 0) {
//            showPopup("📜 Opened Messages:\n" + messages);
//            if (playSound && newReminder) { // Only beep if it's a new reminder
//                playBeepSound();
//            }
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

// Deep seek version

package ui;

import database.TimeCapsuleDAO;
import javafx.stage.Modality;
import models.TimeCapsule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import javafx.stage.Window;
import java.util.WeakHashMap;

public class MainUI extends Application {
    private final TimeCapsuleDAO dao = new TimeCapsuleDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Set<String> notifiedCapsules = new HashSet<>();
    private ListView<TimeCapsule> capsuleListView;
    private TextField searchField;

    @Override
    public void start(Stage primaryStage) {
        // Register the primary stage 
        StageManager.registerStage(primaryStage);
        primaryStage.setTitle("📜 Digital Time Capsule");

        // Create main container
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(20));
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);

        // Create form components
        Label nameLabel = new Label("Your Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Label messageLabel = new Label("Your Message:");
        TextArea messageField = new TextArea();
        messageField.setPromptText("Write your secret message...");
        messageField.setPrefRowCount(3);

        Label dateLabel = new Label("Open Date:");
        DatePicker datePicker = new DatePicker();

        Label timeLabel = new Label("Open Time:");
        TextField timeField = new TextField();
        timeField.setPromptText("HH:MM (24-hour format)");

        // Capsule list view
        capsuleListView = new ListView<>();
        capsuleListView.setPrefHeight(200);
        capsuleListView.setCellFactory(param -> new ListCell<TimeCapsule>() {
            @Override
            protected void updateItem(TimeCapsule item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (Opens: %s)",
                            item.getUserName(),
                            item.getMessage().substring(0, Math.min(30, item.getMessage().length())),
                            item.getOpenDateTime().format(formatter)));
                }
            }
        });

        // Search functionality
        searchField = new TextField();
        searchField.setPromptText("Search capsules...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCapsuleList());

        // Buttons
        Button saveButton = new Button("💾 Save Capsule");
        saveButton.setStyle("-fx-font-weight: bold");

        Button checkButton = new Button("🔍 Check Messages Now");
        checkButton.setStyle("-fx-font-weight: bold");

        // Status label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold");

        // Save Button Action
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String message = messageField.getText().trim();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            String time = timeField.getText().trim();

            if (name.isEmpty() || message.isEmpty() || date.isEmpty() || time.isEmpty()) {
                showStatus(statusLabel, "❌ Please fill all fields!", "error");
                return;
            }

            try {
                LocalDateTime openDateTime = LocalDateTime.parse(date + " " + time, formatter);
                if (openDateTime.isBefore(LocalDateTime.now())) {
                    showStatus(statusLabel, "❌ Open date must be in the future!", "error");
                    return;
                }

                TimeCapsule capsule = new TimeCapsule(name, message, openDateTime);
                dao.saveCapsule(capsule);
                showStatus(statusLabel, "✅ Time capsule saved successfully!", "success");
                refreshCapsuleList();
                clearForm(nameField, messageField, datePicker, timeField);
            } catch (DateTimeParseException ex) {
                showStatus(statusLabel, "❌ Invalid date/time format!", "error");
            }
        });

        // Check Messages Button Action
        checkButton.setOnAction(e -> showMessages(true));

        // Auto-check every minute
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showMessages(true));
            }
        }, 0, 60 * 1000);

        // Layout
        mainGrid.add(nameLabel, 0, 0);
        mainGrid.add(nameField, 1, 0);
        mainGrid.add(messageLabel, 0, 1);
        mainGrid.add(messageField, 1, 1);
        mainGrid.add(dateLabel, 0, 2);
        mainGrid.add(datePicker, 1, 2);
        mainGrid.add(timeLabel, 0, 3);
        mainGrid.add(timeField, 1, 3);
        mainGrid.add(saveButton, 0, 4, 2, 1);
        mainGrid.add(new Label("Your Capsules:"), 0, 5, 2, 1);
        mainGrid.add(searchField, 0, 6, 2, 1);
        mainGrid.add(capsuleListView, 0, 7, 2, 1);
        mainGrid.add(checkButton, 0, 8, 2, 1);
        mainGrid.add(statusLabel, 0, 9, 2, 1);

        // Load existing capsules
        refreshCapsuleList();

        Scene scene = new Scene(mainGrid, 500, 600);
        scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class StageManager {
        private static final WeakHashMap<Window, Void> openStages = new WeakHashMap<>();

        public static void registerStage(Window stage) {
            openStages.put(stage, null);
        }

        public static Window getActiveStage() {
            // Find the last focused window that's still showing
            for (Window window : openStages.keySet()) {
                if (window.isFocused() && window.isShowing()) {
                    return window;
                }
            }
            // Fallback to any showing window
            for (Window window : openStages.keySet()) {
                if (window.isShowing()) {
                    return window;
                }
            }
            return null;
        }
    }

    private void showStatus(Label label, String message, String type) {
        label.setText(message);
        String color = type.equals("error") ? "#c62828" : "#2e7d32";
        label.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold", color));
    }

    private void clearForm(TextField name, TextArea message, DatePicker date, TextField time) {
        Platform.runLater(() -> {
            name.clear();
            message.clear();
            date.setValue(null);
            time.clear();
        });
    }


    // In the showMessages method:
    private void showMessages(boolean playSound) {
        List<TimeCapsule> capsules = dao.getReadyToOpenCapsules();
        StringBuilder messages = new StringBuilder();
        boolean newReminder = false;

        for (TimeCapsule capsule : capsules) {
            if (capsule == null) continue;

            if (!notifiedCapsules.contains(capsule.getId().toString())) {
                newReminder = true;
                notifiedCapsules.add(capsule.getId().toString());
                dao.markAsOpened(capsule.getId());
            }

            messages.append("\n🔔 From: ").append(capsule.getUserName())
                    .append("\n⏰ Opened at: ").append(LocalDateTime.now().format(formatter))
                    .append("\n📖 Message: ").append(capsule.getMessage()).append("\n");
        }

        if (messages.length() > 0) {
            showPopup("📜 Opened Messages:\n" + messages);
            if (playSound && newReminder) {
                playBeepSound();
            }
            refreshCapsuleList();
        }
    }

    // In the refreshCapsuleList method:
    private void refreshCapsuleList() {
        List<TimeCapsule> allCapsules = dao.getAllCapsules();
        String searchTerm = searchField.getText().toLowerCase();

        List<TimeCapsule> filtered = allCapsules.stream()
                .filter(c -> c != null &&
                        (c.getUserName().toLowerCase().contains(searchTerm) ||
                                c.getMessage().toLowerCase().contains(searchTerm)))
                .sorted(Comparator.comparing(TimeCapsule::getOpenDateTime))
                .collect(Collectors.toList());

        capsuleListView.getItems().setAll(filtered);
    }

    private void playBeepSound() {
        try {
            URL soundURL = getClass().getResource("/beep.wav");
            if (soundURL == null) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

//    private void showPopup(String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//            alert.setTitle("🔔 Time Capsule Reminder");
//            alert.setHeaderText("🎉 You have new messages!");
//            alert.showAndWait();
//        });
//    }

    private void showPopup(String message) {
        Platform.runLater(() -> {
            Window ownerWindow = StageManager.getActiveStage();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle("🔔 Time Capsule Reminder");
            alert.setHeaderText("🎉 You have new messages!");

            // Set owner window if available
            if (ownerWindow != null) {
                alert.initOwner(ownerWindow);
            }

            // Make sure the alert stays on top
            alert.initModality(Modality.APPLICATION_MODAL);

            // Position near the owner window
            if (ownerWindow != null) {
                alert.setX(ownerWindow.getX() + 50);
                alert.setY(ownerWindow.getY() + 50);
            }

            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}