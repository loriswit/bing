package me.olybri.bing;// Created by Loris Witschard on 8/27/2017.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * List containing item names.
 */
class ItemNameList
{
    private List<ItemData> itemDataList;
    
    private static class ItemData
    {
        int type = 0;
        int meta = 0;
        String name = "";
        String text_type = "";
    }
    
    /**
     * Exception thrown if an item does not match any of the defined material.
     */
    public static class ItemNotFoundException extends Exception
    {
        ItemNotFoundException(ItemData itemData)
        {
            super(itemData.text_type + " (id=" + itemData.type + ")");
        }
    }
    
    /**
     * Constructs an item name list.
     */
    ItemNameList()
    {
    }
    
    /**
     * Constructs an item name list and load a JSON file.
     *
     * @param filename The JSON input filename
     * @throws IOException if the JSON file cannot be read
     */
    ItemNameList(String filename) throws IOException
    {
        load(filename);
    }
    
    /**
     * Load a JSON file containing items names.
     *
     * @param filename The JSON input filename
     * @throws IOException if the JSON file cannot be read
     */
    public void load(String filename) throws IOException
    {
        System.out.print("Parsing JSON file... ");
        Path filePath = Paths.get(filename);
        String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        
        Type type = new TypeToken<List<ItemData>>() {}.getType();
        itemDataList = new Gson().fromJson(content, type);
        System.out.println("Done!");
    }
    
    /**
     * Write the item names to a YAML file.
     *
     * @param filename The YAML output filename
     * @throws IOException           if the YAML file cannot be written
     * @throws ItemNotFoundException if an item does not match any of the defined material
     */
    public void toYaml(String filename) throws IOException, ItemNotFoundException
    {
        System.out.print("Generating item list... ");
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("# Bukkit Item Names\n");
        writer.write("# Generated with bing (https://github.com/olybri/bing)\n\n");
        writer.write("version: " + Bing.VERSION + "\n\n");
        
        ItemData previous = null;
        for(ItemData itemData : itemDataList)
        {
            switch(itemData.type)
            {
                case 37: // insert block 36
                    writer.write("\nPISTON_MOVING_PIECE:\n  0: Piston Extension\n");
                    break;
                
                case 383: // only use generic name
                    if(itemData.meta == 4)
                        writer.write("\nMONSTER_EGG:\n  0: Spawn Egg\n");
                    continue;
                
                case 427: // insert block 426
                    writer.write("\nEND_CRYSTAL:\n  0: End Crystal\n");
                    break;
                
                case 2256: // insert block 453
                    writer.write("\nKNOWLEDGE_BOOK:\n  0: Knowledge Book\n");
            }
            
            if(previous != null && previous.type != itemData.type)
                writer.newLine();
            
            if(previous == null || previous.type != itemData.type)
            {
                Material mat = Material.getMaterial(itemData.type);
                if(mat == null)
                    throw new ItemNotFoundException(itemData);
                else
                    writer.write(mat.name() + ":" + "\n");
            }
            
            String name = itemData.name
                .replace("Hardened Clay", "Terracotta")
                .replace("(light)", "(Light)")
                .replace("(heavy)", "(Heavy)")
                .replace("Mob Head (Skeleton)", "Skeleton Skull")
                .replace("Mob Head (Wither Skeleton)", "Wither Skeleton Skull")
                .replace("Mob Head (Zombie)", "Zombie Head")
                .replace("Mob Head (Human)", "Head")
                .replace("Mob Head (Creeper)", "Creeper Head")
                .replace("Mob Head (Dragon)", "Dragon Head")
                .replace("Redstone Lamp (inactive)", "Redstone Lamp")
                .replace("Redstone Torch (on)", "Redstone Torch");
            
            writer.write("  " + itemData.meta + ": " + name + "\n");
            
            previous = itemData;
        }
        System.out.println("Done!");
        
        writer.close();
    }
    
    /**
     * Check if a YAML file contains every materials.
     *
     * @param filename The YAML input filename
     * @throws IOException                   if the YAML file cannot be read
     * @throws InvalidConfigurationException if the YAML file is ill-formed
     */
    public static void checkYaml(String filename) throws IOException, InvalidConfigurationException
    {
        System.out.print("Checking item list... ");
        FileConfiguration file = new YamlConfiguration();
        file.load(filename);
        
        int count = 0;
        boolean success = true;
        for(Material material : Material.values())
        {
            if(file.getString(material.name() + ".0") == null)
            {
                if(success)
                {
                    System.out.println();
                    success = false;
                }
                System.out.println("\033[33mMissing item\033[0m: "
                    + material.name() + " (id=" + material.getId() + ")");
            }
            else
                ++count;
        }
        if(success)
            System.out.println("Done! (" + count + "/" + Material.values().length + ")");
        else
            System.out.println("Failed! (" + count + "/" + Material.values().length + ")");
    }
}
