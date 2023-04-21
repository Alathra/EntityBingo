package org.aubrithehuman.entitybingo;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Manages file IO & Deletion for other data classes.
 */
public class DataManager {
    private static DumperOptions options;

    private static EntityBingo plugin;

    public static void init() {
        plugin = EntityBingo.getInstance();

        createFolders();
        genDefaultFiles();
    }

    private static String dataFolder = "plugins" + File.separator + "EntityBingo" + File.separator + "data";

    /**
     * Saves data into a .yml format
     * @param filePath - File's path after <PLUGIN>/Data/
     * @param map - Map to be saved
     */
    public static void saveData(String filePath, HashMap<String,Object> map) {
        File file = getFile(filePath);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("Encountered error when creating PrintWriter for " + filePath);
            return;
        }
        
        if (options == null) {
            options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        }
        
        Yaml yaml = new Yaml(options);
        yaml.dump(map, writer);
        writer.close();
    }

    /**
     * Gets the data back as a HashMap
     * @param filePath - FilePath of data
     * @return HashMap of data or null
     */
    public static HashMap<String,Object> getData(String filePath) {
        File file = getFile(filePath);
        return getData(file);
    }

    /**
     * Gets the data back as a HashMap
     * @param file - Data file
     * @return HashMap of data or null
     */
    public static HashMap<String,Object> getData(File file) {
        if (!file.exists()) {
            Bukkit.getLogger().info("Attempted to retrieve non-existant file: " + file.getPath());
            return null;
        }
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("Encountered error when creating FileInputStream for " + file.getPath());
            return null;
        }
        Yaml yaml = new Yaml();
        HashMap<String, Object> data = yaml.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            Bukkit.getLogger().info(e.getMessage());
        }
        
        return data;
    }

    /**
     * Deletes the specified file
     * @param filePath - FilePath of deletion
     * @return Boolean of success
     */
    public static boolean deleteFile(String filePath) {
        File file = getFile(filePath);

        if (!file.exists()) {
            Bukkit.getLogger().info("Attempted to retrieve non-existant file: " + filePath + ".yml");
            return false;
        }
        
        try {
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts filePath into File
     * @param path - File's path within data folder
     * @return File or null
     */
    private static File getFile(String path) {
        path = dataFolder + File.separator + path;
        
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().info("Encountered error when creating file - " + path);
                return null;
            }
        }
        return file;
    }

    /**
     * Creates Default Files
     */
    private static void genDefaultFiles() {
        List<String> paths = Arrays.asList(
                "scoreboard.yml"
        );

        for (String path : paths) {
            File file = new File(dataFolder + File.separator + path);
            if (!file.exists()) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("players", new ArrayList<String>());
                saveData(path, data);
                EntityBingo.getInstance().getLogger().info("Couldn't find file \"" + path + "\", generated default.");
            } else {
                EntityBingo.getInstance().getLogger().info("Found existing scoreboard file at " + dataFolder + File.separator + path);
            }
        }
    }

    /**
     * Initial creation of all needed folders
     */
    private static void createFolders() {
        List<String> paths = Arrays.asList(
                "plugins" + File.separator + "EconomyTracker" + File.separator + "data"
        );

        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
                Bukkit.getLogger().info("Couldn't find folder \"" + path + "\", generated default.");
            }
        }
    }

}