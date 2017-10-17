# Bukkit Item Names Generator
This is a small tool that generates item names and icon files for each *Bukkit* material. Compatible with *Bukkit* version 1.12.

Generated lists can be downloaded [here](https://olybri.github.io/bing/).

### Building
Simply run `./build.sh` (requires *JDK* 9 and *Maven* 3.5).

### Usage
- `java -jar bing.jar` downloads the item list and generates a YAML file.
- `java -jar bing.jar <input> [output]` takes a JSON item list (*input*) and generates a YAML file (*output*).

Options:
- `-icons` downloads and renames icon files into an *icons* folder.

### Icon direct link
Any icon file can be accessed via a direct link of the following form: https://olybri.github.io/bing/icons/MATERIAL-META.png

### Credits
[Minecraft item list](http://minecraft-ids.grahamedgecombe.com/api) © 2010-2017 Graham Edgecombe
