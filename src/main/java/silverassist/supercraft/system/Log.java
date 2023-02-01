package silverassist.supercraft.system;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.SuperCraft;
import silverassist.supercraft.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

public class Log {
    public static final JavaPlugin plugin = SuperCraft.getInstance();
    public static void write(String id, Player p, ItemStack item, String typeID){
        Date date = new Date();
        String dateStr = (date.getYear()+1900)+"/"+(date.getMonth()+1)+"/"+date.getDate()+"-"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        String name = Objects.requireNonNull(item.getItemMeta()).hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString();

        File logFile = new File(plugin.getDataFolder(),"log/"+id+".csv");
        boolean isFirstWrite = false;
        if(!logFile.exists()){
            try {
                Path path = Paths.get(plugin.getDataFolder().getPath()+"/log");
                if(!Files.exists(path))Files.createDirectories(path);
                logFile.createNewFile();
                isFirstWrite = true;
            }catch (IOException e){
                Util.sendConsole("[SuperCraft]" + id + "のlogファイルの作成に失敗しました", Util.MessageType.ERROR);
                e.printStackTrace();
                playerLogErrorMessage(p,dateStr);
            }
        }
        if(!logFile.isFile()){
            Util.sendConsole("[SuperCraft]logファイル『"+logFile.getPath()+"』はファイルではありません", Util.MessageType.ERROR);
            playerLogErrorMessage(p,dateStr);
            return;
        }else if(!logFile.canWrite()){
            Util.sendConsole("[SuperCraft]logファイル『"+logFile.getPath()+"』は書き込み不可のファイルです", Util.MessageType.ERROR);
            playerLogErrorMessage(p,dateStr);
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            String writeData = dateStr+","+p.getName()+","+p.getUniqueId()+","+name.replace(',','.')+","+item.getAmount()+","+typeID+","+item.toString().replace(",",";")+", \n";
            if(isFirstWrite)fileWriter.write("Time,Player,UUID,ItemName,Amount,TypeID,ItemStack\n");
            fileWriter.write(writeData);
            fileWriter.close();
        }catch (IOException e){
            Util.sendConsole("[SuperCraft]logファイル『"+logFile.getPath()+"』への書き込みに失敗しました", Util.MessageType.ERROR);
            e.printStackTrace();
            playerLogErrorMessage(p,dateStr);
            return;
        }

    }

    private static void playerLogErrorMessage(Player p, String date){
        p.sendMessage("§4§lこの文章が表示された場合は、以下の内容を運営までお伝えください。(スクリーンショット推奨)");
        p.sendMessage("§c§l"+date+" -> SuerCraftでエラーが発生");
        p.sendMessage("§c§lコンソールを至急確認してください");
    }
}