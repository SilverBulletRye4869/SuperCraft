package silverassist.supercraft.system;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import silverassist.supercraft.Util;

import java.util.Map;
import java.util.Set;

public class Check {
    private static int[] slotPlace = Util.getRectSlotPlaces(0,5,5);
    public static String table(Inventory inv){
        ItemStack[] invItems = new ItemStack[25];
        int[] side = rectSlot(inv);
        int place = 0;
        Map<String,ItemStack[]> candidate = Recipe.getRawMaterials();
        for(int i =side[0];i<side[1];i++){
            for(int j = side[2];j<side[3];j++,place++){
                ItemStack item = inv.getItem(slotPlace[i*5+j]);
                candidate.forEach((key,value) ->{
                    if(!item.equals(value))candidate.remove(key);
                });
            }
        }
        Set<String> keys = candidate.keySet();
        if(keys.size() ==1){
            return (String)keys.toArray()[0];
        }else if(keys.size()>1){
            Util.sendConsole("§c§l「"+keys+"」のレシピが被っています。", Util.MessageType.ERROR);
            return "__Err__";
        }
        return null;
    }

    public static int[] rectSlot(Inventory inv){
        int[] side = new int[]{5,5,5,5};
        for(int i = 0;i<25;i++){
            if(inv.getItem(slotPlace[i])==null)continue;
            side[0] = i/5;
            break;
        }
        if(side[0]==5)return null;
        for(int i = 24;i>=5*side[0];i--){
            if(inv.getItem(slotPlace[i])==null)continue;
            side[1] = i/5+1;
            break;
        }

        for(int i= 5*side[0];true;){
            if(inv.getItem(slotPlace[i])==null){
                if((i+=5)>=5*side[1]) {
                    if (i % 4 == 0) break;
                    i = 5 * side[0] + (i % 5 + 1);
                }
            }else{
                side[2] = i%5;
                break;
            }
        }
        if(side[2]==5)return null;
        for(int i = 5*side[1] -1;true;){
            if(inv.getItem(slotPlace[i]) == null){
                if((i-=5)<5*side[0]){
                    if(i%side[2]==0)break;
                    i = 5 * side[1] +(i%5-1);
                }
            }else{
                side[3] = i %5 +1;
                break;
            }
        }
        return side;
    }
}
