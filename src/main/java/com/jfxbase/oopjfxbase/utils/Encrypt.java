package com.jfxbase.oopjfxbase.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        /* MessageDigest instance for hashing using SHA256 algorithm */
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // getBytes(StandardCharsets.UTF_8) method converts the input string into an array of bytes using UTF-8 encoding.
        // The digest method is then called to compute the hash. The result is a byte array representing the hash.
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        /* Convert byte array of hash into digest */
        //The 1 as the first argument signifies that the byte array is positive, avoiding the sign bit interpretation.
        BigInteger number = new BigInteger(1, hash);

        /* Convert the digest into hex value */
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // we add 0's if the lengts < 32
        // sha-256 requires 32 characters minim
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        // return the pass in a string form (the pass has a hex value)
        return hexString.toString();
    }
}
