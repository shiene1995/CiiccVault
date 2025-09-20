package com.ciiccvault.CiiccVault;

import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashers {

    public static final int SALT_LENGTH = 16;
    public static final int HASH_LENGTH = 32;

    public static String hashPassword(char[] password, byte[] salt) {
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(1)
                .withMemoryAsKB(65536) // 64 MB
                .withIterations(3);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password, hash);

        return Base64.getEncoder().encodeToString(hash);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        byte[] saltFromDB = Base64.getDecoder().decode(salt); //in the String is salt from DB
        String inputHash = hashPassword(inputPassword.toCharArray(), saltFromDB);
        return storedHash.equals(inputHash);
    }

    public static boolean verifyUsername(String usernameInput, String usernameDB){
        return usernameDB.equals(usernameInput);
    }

    public static void main(String[] args) {
        // Registration (both salt and hashed should be SAVE in database)
        String password = "zyrinne";
        byte[] salt = generateSalt();
        String hashed = hashPassword(password.toCharArray(), salt);

        System.out.println("Salt (Base64): " + Base64.getEncoder().encodeToString(salt));
        System.out.println("Hash (Base64): " + hashed);

        // Login verification
        String saltFromDB = "0NVORnht1aeP2J9nNMQVWg=="; //in the String is salt from DB
        String hashFromDB = "YHFTS44Fm7sBvEHAVafvexvfnSJGhwJa88zVpPDKfhE="; //in the String is hash from DB

        boolean match = verifyPassword(password, hashFromDB, saltFromDB); //this is how to use verify the Password
        System.out.println("Password match? " + match);
    }


}
