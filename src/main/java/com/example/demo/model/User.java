package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // ✅ Avoid reserved keyword "user"
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Column(unique = true)
    private String firebaseUid;

    @Column(name = "user_type")
    private String userType = "USER";

    // ✅ One user can have many lands
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Land> lands;

    // ✅ Store multiple FCM tokens for this user
    @ElementCollection
    @CollectionTable(name = "user_fcm_tokens", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "fcm_token")
    private List<String> fcmTokens = new ArrayList<>();

    public User(String username, String password) {

        this.username = username;
        this.password = password;
    }
}
