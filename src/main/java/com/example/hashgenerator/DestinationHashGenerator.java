package com.example.hashgenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <0132cs211033> <src/main/resources/example.json>");
            System.exit(1);
        }
        String rollNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String filePath = args[1];

        try {

            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();


            String destinationValue = findFirstDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }


            String randomString = generateRandomString(8);


            String toHash = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(toHash);


            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String findFirstDestination(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().getAsString();
                } else {
                    String result = findFirstDestination(entry.getValue());
                    if (result != null) return result;
                }
            }
        } else if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                String result = findFirstDestination(element);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
