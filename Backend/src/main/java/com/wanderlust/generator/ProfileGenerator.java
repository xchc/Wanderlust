package com.wanderlust.generator;

import java.util.Random;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.User;

public class ProfileGenerator {

    private static final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Random random = new Random();

    protected static String randomString() {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(130)+10; // random length [10..140]
        for( int i = 0; i < length; i++ ) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static UserProfile newProfile(User from) {
        return new UserProfile(from, randomString(), randomString(), null);
    }

}
