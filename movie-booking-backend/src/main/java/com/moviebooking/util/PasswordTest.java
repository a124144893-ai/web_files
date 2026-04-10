package com.moviebooking.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "User1@123";
        String hash = "$2a$12$tXEd.UlvKVv8tBLXl1PwY.Dq2w3Cxuw0eMDEWrKGSFTn9kQ7PH1py";

        boolean matches = encoder.matches(password, hash);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Matches: " + matches);

        // Also test generating a new hash
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}