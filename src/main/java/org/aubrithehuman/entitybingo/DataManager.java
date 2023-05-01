package org.aubrithehuman.entitybingo;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

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
     *
     * @param filePath - File's path after <PLUGIN>/Data/
     * @param map      - Map to be saved
     */
    public static void saveData(String filePath, HashMap<String, Object> map) {
        File file = getFile(filePath);
        Bukkit.getLogger().log(Level.WARNING, "scoreboard.yml printout1");
        Bukkit.getLogger().log(Level.WARNING, "Error Print isAbsolute: " + (file == null ? "" : file.isAbsolute()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print isFile: " + (file == null ? "" : file.isFile()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print canWrite: " + (file == null ? "" : file.canWrite()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print exists: " + (file == null ? "" : file.exists()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print canRead: " + (file == null ? "" : file.canRead()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print isHidden: " + (file == null ? "" : file.isHidden()));
        Bukkit.getLogger().log(Level.WARNING, "Error Print path: " + (file == null ? "" : file.getPath()));
        PrintWriter writer = null;
        try {
            file.setWritable(true);
            Bukkit.getLogger().log(Level.WARNING, "scoreboard.yml printout2");
            Bukkit.getLogger().log(Level.WARNING, "Error Print isAbsolute: " + file.isAbsolute());
            Bukkit.getLogger().log(Level.WARNING, "Error Print isFile: " + file.isFile());
            Bukkit.getLogger().log(Level.WARNING, "Error Print canWrite: " + file.canWrite());
            Bukkit.getLogger().log(Level.WARNING, "Error Print exists: " + file.exists());
            Bukkit.getLogger().log(Level.WARNING, "Error Print canRead: " + file.canRead());
            Bukkit.getLogger().log(Level.WARNING, "Error Print isHidden: " + file.isHidden());
            Bukkit.getLogger().log(Level.WARNING, "Error Print path: " + file.getPath());
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered FileNotFound error when creating PrintWriter for " + filePath);
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered NullPointer error when creating PrintWriter for " + filePath);
            e.printStackTrace();
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
        writer.flush();
        writer.close();
    }

    /**
     * Gets the data back as a HashMap
     *
     * @param filePath - FilePath of data
     * @return HashMap of data or null
     */
    public static HashMap<String, Object> getData(String filePath) {
        File file = getFile(filePath);
        return getData(file);
    }

    /**
     * Gets the data back as a HashMap
     *
     * @param file - Data file
     * @return HashMap of data or null
     */
    public static HashMap<String, Object> getData(File file) {
        if (!file.exists()) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Attempted to retrieve non-existant file: " + file.getPath());
            return null;
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Encountered error when creating FileInputStream for " + file.getPath());
            e.printStackTrace();
            return null;
        }
        Yaml yaml = new Yaml();
        HashMap<String, Object> data = yaml.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            EntityBingo.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return data;
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

    /**
     * Creates Default Files
     */
    private static void genDefaultFiles() {
        List<String> paths = List.of(
                "scoreboard.yml"
        );

        for (String path : paths) {
            File file = new File(dataFolder + File.separator + path);
            if (!file.exists()) {
                HashMap<String, Object> raw = new HashMap<>();
                raw.put("scores", new HashMap<String, Object>());
                saveData(path, raw);
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
                "plugins" + File.separator + "EntityBingo" + File.separator + "data"
        );

        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
                EntityBingo.getInstance().getLogger().info("Couldn't find folder \"" + path + "\", generated default.");
            }
        }
    }

}
