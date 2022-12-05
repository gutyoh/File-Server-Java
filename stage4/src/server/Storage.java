package server;

import common.Request;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Storage {
    private final String dataDir;
    private final String metadataDir;
    private final String metadataFilename;
    private final Map<Integer, String> idToFilename;

    public Storage() {
        // create server-side directories, if don't exist: mkdirs is idempotent
        dataDir = "./src/server/data"; // tests require this specific location
        new File(dataDir).mkdirs();

        metadataDir = "./src/server/metadata";
        new File(metadataDir).mkdirs();
        metadataFilename = "idToFilename.ser";

        if (doesFileExist(metadataFilename, true)) {
            idToFilename = deserializeMetadata();
        } else {
            idToFilename = new HashMap<>();
        }
    }

    public void serializeMetadata() {
        try {
            FileOutputStream fos = new FileOutputStream(metadataDir + "/" + metadataFilename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idToFilename);
        } catch (IOException e) {
            System.out.printf("Exception during serialization:\n%s", e.getMessage());
        }
    }

    private Map<Integer, String> deserializeMetadata() {
        try {
            FileInputStream fis = new FileInputStream(metadataDir + "/" + metadataFilename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Map) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean doesFileExist(String fileName) {
        return doesFileExist(fileName, false);
    }

    public boolean doesFileExist(String fileName, boolean isMetadataFile) {
        if (isMetadataFile) {
            return new File(metadataDir + "/" + fileName).exists();
        } else {
            return new File(dataDir + "/" + fileName).exists();
        }
    }

    public boolean doesFileExist(int fileId) {
        String fileName = idToFilename.getOrDefault(fileId, "NOT_FOUND_IN_METADATA");
        return doesFileExist(fileName);
    }

    public byte[] getFileContent(String fileName) {
        try {
            File file = new File(dataDir + "/" + fileName);
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getFileContent(int fileId) {
        String fileName = idToFilename.get(fileId);
        return getFileContent(fileName);
    }

    public boolean delete(String fileName) {
        File file = new File(dataDir + "/" + fileName);
        try {
            idToFilename.values().remove(fileName); // remove ID to file name metadata
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int fileId) {
        String fileName = idToFilename.getOrDefault(fileId, "NOT_FOUND_IN_METADATA");
        return delete(fileName);
    }

    public int persist(Request requestFromClient) {
        File file = new File(dataDir + "/" + requestFromClient.getFileName());
        int fileId = idToFilename.size() == 0 ? 0 : new TreeSet<>(idToFilename.keySet()).last() + 1; // ensure new ID is unique

        try {
            idToFilename.put(fileId, requestFromClient.getFileName()); // write metadata first, file op may take long
            Files.write(file.toPath(), requestFromClient.getFileContent());
        } catch (IOException e) {
            idToFilename.remove(fileId); // metadata cleanup on failure
            System.out.printf("Exception during file write operation:\n%s", e.getMessage());
        }

        return fileId;
    }
}