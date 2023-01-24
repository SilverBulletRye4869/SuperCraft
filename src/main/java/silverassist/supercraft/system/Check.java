package silverassist.supercraft.system;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import silverassist.supercraft.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Check {
    private static final ItemStack AIR = new ItemStack(Material.AIR,0);
    private static int[] slotPlace = Util.getRectSlotPlaces(0,5,5);

    public static String table(Inventory inv){
        int[] side = rectSlot(inv);
        Map<String,ItemStack[][]> candidate = new HashMap<>(Recipe.getRawItems());
        if(side == null)return null;
        new HashMap<>(candidate).forEach((key, value) ->{
            if(value.length!= (side[1]-side[0]) || value[0].length!= (side[3]-side[2])){
                candidate.remove(key);
                return;
            }
            System.out.println(Arrays.toString(side));
            for(int i =side[0];i<side[1];i++){
                int h = i - side[0];
                for(int j = side[2];j<side[3];j++){
                    ItemStack item = inv.getItem(slotPlace[i*5+j]) == null ? AIR : inv.getItem(slotPlace[i*5+j]);
                    int w= j -side[2];
                    if(value.length <= h || value[h].length <= w || !item.equals(value[h][w])){
                        candidate.remove(key);
                        return;
                    };
                }
            }
        });
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
                    if (i % 5 == 4) break;
                    i = 5 * side[0] + (i % 5 + 1);
                }
            }else{
                side[2] = i%5;
                break;
            }
        }

        if(side[2]==5)return null;
        for(int i = 5*side[1]- 1;true;){
            if(inv.getItem(slotPlace[i]) == null){
                if((i-=5)<5*side[0]){
                    if(i%5==side[2])break;
                    i = 5 * (side[1]-1) +((i+5)%5-1);
                }
            }else{
                side[3] = i %5 +1;
                break;
            }
        }
        return side;
    }
}
