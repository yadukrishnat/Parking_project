package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("car-parking-99d88-firebase-adminsdk-fbsvc-5fc1e661d6.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Firebase key file not found in resources!");
            }

//            InputStream serviceAccount =
//                    getClass().getClassLoader().getResourceAsStream("firebase-car-parking-99d88-firebase-adminsdk-fbsvc-5fc1e661d6.json-key.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("🔥 Firebase initialized successfully!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
