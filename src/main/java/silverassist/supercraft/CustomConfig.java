package silverassist.supercraft;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomConfig {
    private static Map<String, YamlConfiguration> config = new HashMap<>();
    private static JavaPlugin plugin = SuperCraft.getInstance();
    private static HashSet<String> existSet = new HashSet<>();

    public static YamlConfiguration getYmlByID(String id) {
        if(!config.containsKey(id)){
            if(!reloadYmlByID(id))return null;
        }
        return config.get(id);
    }

    public static boolean existYml(String id){
        if(existSet.contains(id))return true;
        if(new File(plugin.getDataFolder(),"data/"+id+".yml").exists()){existSet.add(id);return true;}
        return false;
    }

    public static YamlConfiguration createYmlByID(String id){
        File file = new File(plugin.getDataFolder(),"data/"+id+".yml");
        try {
            file.createNewFile();
        }catch (IOException e){
            System.err.println("[TradeShop]id: "+id+"のymlファイルの作成に失敗しました");
            e.printStackTrace();
            return null;
        }
        return getYmlByID(id);
    }

    static boolean deleteYmlByID(String id){
        File file = new File(plugin.getDataFolder(),"data/"+id+".yml");
        boolean result = file.delete();
        if(result){
            config.remove(id);
            existSet.remove(id);
        }
        return result;
    }

    public static boolean reloadYmlByID(String id){
        File file = new File(plugin.getDataFolder(),"data/"+id+".yml");
        if(!file.exists())return false;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        config.put(id,y);
        return true;
    }

    public static void saveYmlByID(String id){
        try{
            config.get(id).save(new File(plugin.getDataFolder(),"data/" + id + ".yml"));
        }catch (IOException e){
            System.err.println("[TradeShop]ガチャ『"+id+"』の保存に失敗しました。:"+e);
        }
    }
}
