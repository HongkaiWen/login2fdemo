package com.github.hongkaiwen.login2f.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base32;

public class GoogleUtil {

    public static final String SYSTEM_ACCOUNT = "hongkai";
    public static final String ISSUER = "github";

    public static String getRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        // make the secret key more human-readable by lower-casing and
        // inserting spaces between each group of 4 characters
        return secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
    }

    /**
     * @param secretKey Base32 encoded secret key (may have optional whitespace)
     * @param account The user's account name. e.g. an email address or a username
     * @param issuer The organization managing this account
     * @return
     *
     * @see https://github.com/google/google-authenticator/wiki/Key-Uri-Format
     */
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        try {
            return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, int height, int width, OutputStream out)
        throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
            width, height);
        MatrixToImageWriter.writeToStream(matrix, "png", out);
    }


}
