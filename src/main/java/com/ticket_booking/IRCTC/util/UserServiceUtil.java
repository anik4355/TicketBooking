package com.ticket_booking.IRCTC.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
// import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;

public class UserServiceUtil {
    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    public static boolean checkPassword(String plainPassword, String hashPassword){
            return BCrypt.checkpw(plainPassword, hashPassword);
    }

}
