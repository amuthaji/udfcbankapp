package com.Banking.udfcbankapplication.entity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1234";
        String storedPassword = "$2a$10$sO53OBM.EJzfK3rNl5aeDuFQk0l7xDvhACLvc10FP6MojgnLI46vu"; // Copy from your database

        System.out.println("Matches: " + encoder.matches(rawPassword, storedPassword));
    }
}

//public class TestBCrypt {
//    public static void main(String[] args) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String rawPassword = "1234";
//        String hashedPassword = encoder.encode(rawPassword);
//
//        System.out.println("Raw: " + rawPassword);
//        System.out.println("Hashed: " + hashedPassword);
//        System.out.println("Matches: " + encoder.matches(rawPassword, hashedPassword)); // Should be true
//    }
//}


class PasswordReset {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = "8124"; // Set your new password here
        String hashedPassword = encoder.encode(newPassword);

        System.out.println("New Hashed Password: " + hashedPassword);
    }
}
