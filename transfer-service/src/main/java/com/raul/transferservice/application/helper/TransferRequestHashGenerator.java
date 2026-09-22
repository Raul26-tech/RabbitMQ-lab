package com.raul.transferservice.application.helper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

public final class TransferRequestHashGenerator {

    private TransferRequestHashGenerator(){}

    public static String generate(
            UUID senderId,
            UUID receiverId,
            BigDecimal amount
    ){
        String normlizedAmount = amount.stripTrailingZeros().toPlainString();

        String raw = senderId + "|" + receiverId + "|" + normlizedAmount;

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e){
            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }

}