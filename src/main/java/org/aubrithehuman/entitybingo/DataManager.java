package org.aubrithehuman.entitybingo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.event.entity.EntityTameEvent;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages file IO & Deletion for other data classes.
 */
public class DataManager {
    private static DumperOptions options;

    private static EntityBingo plugin;

    public static void init() {
        plugin = EntityBingo.getInstance();
    }

    private static String dataFolder = "plugins" + File.separator + "EntityBingo" + File.separator + "data";

    /**
     * Saves data into a .yml format
     *
     * @param filePath - File's path after <PLUGIN>/Data/
     * @param map      - Map to be saved
     */
    public static void saveData(String filePath, HashMap<String, Object> map) {
//        File file = getFile(filePath);
//        Bukkit.getLogger().log(Level.WARNING, "scoreboard.yml printout1");
//        Bukkit.getLogger().log(Level.WARNING, "Error Print isAbsolute: " + (file == null ? "" : file.isAbsolute()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print isFile: " + (file == null ? "" : file.isFile()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print canWrite: " + (file == null ? "" : file.canWrite()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print exists: " + (file == null ? "" : file.exists()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print canRead: " + (file == null ? "" : file.canRead()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print isHidden: " + (file == null ? "" : file.isHidden()));
//        Bukkit.getLogger().log(Level.WARNING, "Error Print path: " + (file == null ? "" : file.getPath()));
//        PrintWriter writer = null;
//        try {
//            file.setWritable(true);
//            Bukkit.getLogger().log(Level.WARNING, "scoreboard.yml printout2");
//            Bukkit.getLogger().log(Level.WARNING, "Error Print isAbsolute: " + file.isAbsolute());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print isFile: " + file.isFile());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print canWrite: " + file.canWrite());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print exists: " + file.exists());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print canRead: " + file.canRead());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print isHidden: " + file.isHidden());
//            Bukkit.getLogger().log(Level.WARNING, "Error Print path: " + file.getPath());
//            writer = new PrintWriter(file);
//        } catch (FileNotFoundException e) {
//            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered FileNotFound error when creating PrintWriter for " + filePath);
//            e.printStackTrace();
//            return;
//        } catch (NullPointerException e) {
//            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered NullPointer error when creating PrintWriter for " + filePath);
//            e.printStackTrace();
//            return;
//        }
//
//        if (options == null) {
//            options = new DumperOptions();
//            options.setIndent(2);
//            options.setPrettyFlow(true);
//            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        }
//
//        Yaml yaml = new Yaml(options);
//        yaml.dump(map, writer);
//        writer.flush();
//        writer.close();

        MemorySection data = (MemorySection) EntityBingo.getInstance().getConfig().get("scores");
        Map<String, Object> raw = (Map<String, Object>) map.get("scores");
        for (String s : raw.keySet()) {
            Bukkit.getLogger().info("saving: " + s + ": " + raw.get(s));
            data.set(s, raw.get(s));
        }
        Bukkit.getLogger().info(data.toString());

        try {
            EntityBingo.getInstance().getConfig().save("plugins" + File.separator + "EntityBingo" + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the data back as a HashMap
     *
     * @param filePath - FilePath of data
     * @return HashMap of data or null
     */
    public static Map<String, Object> getData(String filePath) {
        File file = getFile(filePath);
        return getData(file);
    }

    /**
     * Gets the data back as a HashMap
     *
     * @param file - Data file
     * @return HashMap of data or null
     */
    public static Map<String, Object> getData(File file) {
//        if (!file.exists()) {
//            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Attempted to retrieve non-existant file: " + file.getPath());
//            return null;
//        }
//
//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered error when creating FileInputStream for " + file.getPath());
//            e.printStackTrace();
//            return null;
//        }
//        Yaml yaml = new Yaml();
//        HashMap<String, Object> data = yaml.load(inputStream);
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            EntityBingo.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
//            e.printStackTrace();
//        }

        try {
            EntityBingo.getInstance().getConfig().load("plugins" + File.separator + "EntityBingo" + File.separator + "config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        MemorySection data = ((MemorySection) EntityBingo.getInstance().getConfig().get("scores"));
        Bukkit.getLogger().info(data.toString());
        Bukkit.getLogger().info(data.getValues(true).toString());
        Map<String,Object> raw = new HashMap<>();
        raw.put("scores", data.getValues(true));
        return raw;
    }

    /**
     * Deletes the specified file
     *
     * @param filePath - FilePath of deletion
     * @return Boolean of success
     */
    public static boolean deleteFile(String filePath) {
        File file = getFile(filePath);

        if (!file.exists()) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Attempted to retrieve non-existant file: " + filePath + ".yml");
            return false;
        }

        try {
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts filePath into File
     *
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
                EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered error when creating file - " + path);
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

}
