package me.olybri.bing;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Bukkit Item Names Generator
 */
class Bing
{
    /**
     * Current Bukkit version
     */
    public static final String VERSION = "1.12.1";
    
    /**
     * Entry point.
     */
    public static void main(String args[])
    {
        ItemNameList items = new ItemNameList();
        String filenameOut;
        
        List<String> arguments = new LinkedList<>(Arrays.asList(args));
        
        boolean icons = false;
        if(!arguments.isEmpty() && arguments.get(0).equals("-icons"))
        {
            icons = true;
            arguments.remove(0);
        }
        
        try
        {
            if(arguments.isEmpty())
            {
                System.out.print("Downloading item list... ");
                URL url = new URL("http://minecraft-ids.grahamedgecombe.com/items.json");
                
                String filenameIn = "tmp." + Instant.now().toEpochMilli() + ".json";
                File inputFile = new File(filenameIn);
                FileUtils.copyURLToFile(url, inputFile);
                System.out.println("Done!");
                
                items.load(filenameIn);
                inputFile.delete();
                
                filenameOut = "items.yml";
            }
            else
            {
                String filenameIn = arguments.get(0);
                
                if(arguments.size() == 1)
                    filenameOut = filenameIn.replaceAll("\\.\\w*$", ".yml");
                else
                    filenameOut = arguments.get(1);
                
                items.load(filenameIn);
            }
            
            items.toYaml(filenameOut);
            
            if(icons)
            {
                System.out.print("Downloading icon list... ");
                URL url = new URL("http://minecraft-ids.grahamedgecombe.com/items.zip");
                
                String filenameIn = "tmp." + Instant.now().toEpochMilli() + ".zip";
                File inputFile = new File(filenameIn);
                FileUtils.copyURLToFile(url, inputFile);
                System.out.println("Done!");
                
                System.out.print("Extracting icon list... ");
                ZipFile zipFile = new ZipFile(inputFile);
                zipFile.extractAll("icons");
                inputFile.delete();
                System.out.println("Done!");
                
                System.out.print("Renaming icon files... ");
                File folder = new File("icons");
                for(File file : folder.listFiles())
                {
                    String[] id = file.getName().split("[-.]");
                    Material material = Material.getMaterial(Integer.parseInt(id[0]));
                    short meta = Short.parseShort(id[1]);
                    File newFile = new File("icons/" + material.name() + "-" + meta + ".png");
                    file.renameTo(newFile);
                }
                System.out.println("Done!");
            }
        }
        catch(Exception e)
        {
            System.out.println();
            System.out.println("\033[1;31m" + e.getClass().getSimpleName() + "\033[0m: " + e.getMessage());
        }
    }
}
