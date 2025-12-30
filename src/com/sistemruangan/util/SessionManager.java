package com.sistemruangan.util;

import com.sistemruangan.model.User;

/**
 * Session Manager untuk menyimpan informasi user yang sedang login
 */
public class SessionManager {
    private static User currentUser = null;
    
    /**
     * Set user yang sedang login
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Get user yang sedang login
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Cek apakah user sudah login
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Logout user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Get user ID
     */
    public static int getUserId() {
        return currentUser != null ? currentUser.getId() : 0;
    }
    
    /**
     * Get username
     */
    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }
    
    /**
     * Get nama lengkap
     */
    public static String getNamaLengkap() {
        return currentUser != null ? currentUser.getNamaLengkap() : "";
    }
}