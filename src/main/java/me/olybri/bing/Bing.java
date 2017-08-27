package me.olybri.bing;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.time.Instant;

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
        
        try
        {
            if(args.length == 0)
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
                String filenameIn = args[0];
                
                if(args.length == 1)
                    filenameOut = filenameIn.replaceAll("\\.\\w*$", ".yml");
                else
                    filenameOut = args[1];
                
                items.load(filenameIn);
            }
            
            items.toYaml(filenameOut);
        }
        catch(Exception e)
        {
            System.out.println();
            System.out.println("\033[1;31m" + e.getClass().getSimpleName() + "\033[0m: " + e.getMessage());
        }
    }
}
