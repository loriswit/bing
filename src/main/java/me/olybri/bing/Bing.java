package me.olybri.bing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

public class Bing
{
    private static final String VERSION = "1.12.1";
    
    private static class ItemData
    {
        int type = 0;
        int meta = 0;
        String name = "";
        String text_type = "";
    }
    
    private static class ItemNotFoundException extends Exception
    {
        ItemNotFoundException(ItemData itemData)
        {
            super(itemData.text_type + " (id=" + itemData.type + ")");
        }
    }
    
    public static void main(String args[])
    {
        try
        {
            if(args.length == 0)
            {
                System.out.print("Downloading item list... ");
                URL url = new URL("http://minecraft-ids.grahamedgecombe.com/items.json");
                
                String filename = "tmp." + Instant.now().toEpochMilli() + ".json";
                File inputFile = new File(filename);
                FileUtils.copyURLToFile(url, inputFile);
                System.out.println("Done!");
                
                toYaml(filename, "items.yml");
                inputFile.delete();
                
                checkYaml("items.yml");
            }
            else
            {
                String filenameIn = args[0];
                String filenameOut = filenameIn.replaceAll("\\.\\w*$", ".yml");
                
                if(args.length == 2)
                    filenameOut = args[1];
                
                toYaml(filenameIn, filenameOut);
                checkYaml(filenameOut);
            }
        }
        catch(Exception e)
        {
            System.out.println();
            System.out.println("\033[1;31m" + e.getClass().getSimpleName() + "\033[0m: " + e.getMessage());
        }
    }
    
    private static void toYaml(String filenameIn, String filenameOut) throws Exception
    {
        List<ItemData> itemDataList;
        
        System.out.print("Parsing JSON file... ");
        Path filePath = Paths.get(filenameIn);
        String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        
        Type type = new TypeToken<List<ItemData>>() {}.getType();
        itemDataList = new Gson().fromJson(content, type);
        System.out.println("Done!");
        
        System.out.print("Generating item list... ");
        BufferedWriter writer = new BufferedWriter(new FileWriter(filenameOut));
        writer.write("version: " + VERSION + "\n\n");
        
        ItemData previous = null;
        for(ItemData itemData : itemDataList)
        {
            switch(itemData.type)
            {
                case 37: // insert block 36
                    writer.write("\nPISTON_MOVING_PIECE:\n  0: Piston Extension\n");
                    break;
                
                case 383:
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
    
    private static void checkYaml(String filename) throws IOException, InvalidConfigurationException
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
                    + material.name() + ":0 (id=" + material.getId() + ")");
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
