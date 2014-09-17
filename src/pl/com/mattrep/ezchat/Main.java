package pl.com.mattrep.ezchat;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main
  extends JavaPlugin
  implements Listener
{
  private PermissionsEx pex;
  public HashMap<Player, BukkitTask> blockChat = new HashMap();
  public List<String> blockedWords = getConfig().getStringList("no-swear");
  
  public void onEnable()
  {
    this.pex = ((PermissionsEx)Bukkit.getPluginManager().getPlugin("PermissionsEx"));
    saveDefaultConfig();
    Bukkit.getPluginManager().registerEvents(this, this);
  }
  
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent event)
  {
    final Player p = event.getPlayer();
    if ((!getConfig().getBoolean("enabled")) && 
      (!p.hasPermission("ezchat.bypass")))
    {
      p.sendMessage(Tools.fixColors("&6Chat is &4disabled."));
      event.setCancelled(true);
      return;
    }
    for (String words : this.blockedWords) {
        if (event.getMessage().toLowerCase().contains(words.toLowerCase())) {
          event.setMessage(event.getMessage().toLowerCase().replaceAll(words.toLowerCase(), "*****"));
        }
      }
    if ((this.blockChat.get(p) != null) && 
      (!p.hasPermission("ezchat.bypass")))
    {
      p.sendMessage(Tools.fixColors("&6You can only chat once per &c" + getConfig().getInt("cooldown") + " secounds&6!"));
      event.setCancelled(true);
      return;
    }
    BukkitTask task = Bukkit.getScheduler().runTaskLater(this, 
      new Runnable()
      {
        public void run()
        {
          if (Main.this.blockChat.get(p) != null) {
            Main.this.blockChat.remove(p);
          }
        }
      }, 20L * getConfig().getInt("cooldown"));
    
    this.blockChat.put(p, task);

        event.setCancelled(true);
        if(p.hasPermission("ezchat.chat")){
	        String pex_prefix = this.pex.getUser(p).getPrefix();
	        
	        String message = getConfig().getString("chat").replaceAll("&", "§");
	        message = message.replaceAll("%player%", p.getName());
	        message = message.replaceAll("%pex_prefix%", Tools.fixColors(pex_prefix));
	        message = message.replaceAll("%message%", event.getMessage());
	        Bukkit.broadcastMessage(message);
	        return;
        }
  }
}
