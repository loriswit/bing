package me.olybri.bing;// Created by Loris Witschard on 8/27/2017.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List containing item names.
 */
class ItemNameList
{
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
    public ItemNameList()
    {
    }
    
    /**
     * Constructs an item name list and load a JSON file.
     *
     * @param filename The JSON input filename
     * @throws IOException           if the JSON file cannot be read
     * @throws ItemNotFoundException if an item does not match any of the defined material
     */
    public ItemNameList(String filename) throws IOException, ItemNotFoundException
    {
        load(filename);
    }
    
    /**
     * Load a JSON file containing items names.
     *
     * @param filename The JSON input filename
     * @throws IOException           if the JSON file cannot be read
     * @throws ItemNotFoundException if an item does not match any of the defined material
     */
    public void load(String filename) throws IOException, ItemNotFoundException
    {
        System.out.print("Parsing JSON file... ");
        Path filePath = Paths.get(filename);
        String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        
        Type type = new TypeToken<List<ItemData>>() {}.getType();
        List<ItemData> itemDataList = new Gson().fromJson(content, type);
        System.out.println("Done!");
        
        System.out.print("Generating item list... ");
        for(ItemData itemData : itemDataList)
        {
            Material material = Material.getMaterial(itemData.type);
            if(material == null)
                throw new ItemNotFoundException(itemData);
            
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
            
            addName(material, itemData.meta, name);
        }
        
        names.remove(Material.MONSTER_EGG);
        
        // add missing names
        addName(Material.PISTON_MOVING_PIECE, 0, "Piston Extension");
        addName(Material.MONSTER_EGG, 0, "Spawn Egg");
        addName(Material.END_CRYSTAL, 0, "End Crystal");
        addName(Material.KNOWLEDGE_BOOK, 0, "Knowledge Book");
        
        System.out.println("Done!");
    }
    
    /**
     * Write the item names to a YAML file.
     *
     * @param filename The YAML output filename
     * @throws IOException if the YAML file cannot be written
     */
    public void toYaml(String filename) throws IOException
    {
        System.out.print("Writing to YAML file... ");
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("# Bukkit Item Names\n");
        writer.write("# Generated with bing (https://github.com/olybri/bing)\n\n");
        writer.write("version: " + Bing.VERSION + "\n");
        
        boolean missing = false;
        for(Material material : Material.values())
        {
            Map<Integer, String> materialNames = names.get(material);
            if(materialNames == null)
            {
                if(!missing)
                {
                    System.out.println();
                    missing = true;
                }
                System.out.println("\033[33mMissing item\033[0m: "
                    + material.name() + " (id=" + material.getId() + ")");
                continue;
            }
            
            writer.write("\n" + material.name() + ":\n");
            
            for(Map.Entry<Integer, String> name : materialNames.entrySet())
                writer.write("  " + name.getKey() + ": " + name.getValue() + "\n");
        }
        
        writer.close();
        
        System.out.println("Done!");
    }
    
    /**
     * Adds a name to the list.
     *
     * @param material The item material
     * @param meta     The item meta
     * @param name     The item name
     */
    private void addName(Material material, int meta, String name)
    {
        names.putIfAbsent(material, new HashMap<>());
        names.get(material).put(meta, name);
    }
    
    private Map<Material, Map<Integer, String>> names = new HashMap<>();
    
    private static class ItemData
    {
        int type = 0;
        int meta = 0;
        String name = "";
        String text_type = "";
    }
}
