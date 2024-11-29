package taskmanager.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import taskmanager.exceptions.AuthenticationFailed;

public class SessionManager {

    private static final String SESSION_FILE = "userSession.jwt";

    // Save JWT token to a file
    public static void saveSessionToken(String token) {
        if (token == null || token.isEmpty()) {
            System.out.println("Warning: Attempted to save an empty or null token.");
            return; // Don't write invalid tokens
        }

        try (FileOutputStream out = new FileOutputStream(SESSION_FILE)) {
            out.write(token.getBytes());
            System.out.println("Token saved successfully: " + token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load JWT token from the file
    public static String loadSessionToken() {
        try (FileInputStream in = new FileInputStream(SESSION_FILE)) {
            byte[] data = new byte[(int) in.available()];
            in.read(data);
            return new String(data);
        } catch (IOException e) {
            System.out.println("Unauthenticated.");
        }
        return null;
    }

    public static void clearSessionToken() {
        try {
            new File(SESSION_FILE).delete();
            System.out.println("Session token cleared.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
