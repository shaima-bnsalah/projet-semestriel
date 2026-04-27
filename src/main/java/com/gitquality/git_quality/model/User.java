package com.gitquality.git_quality.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; 

@Data @AllArgsConstructor @NoArgsConstructor
public class User implements Serializable { 
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String email;
    private String password;
    private String verificationCode; 
    private boolean isVerified;  
}