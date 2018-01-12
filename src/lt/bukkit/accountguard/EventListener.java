package lt.bukkit.accountguard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class EventListener implements Listener {

	Main plugin;

	EventListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void AGLoginEvent(PlayerLoginEvent e) {
		if (plugin.ip.isSet(e.getPlayer().getName().toLowerCase())) {
			for (String se : plugin.ip.getString(e.getPlayer().getName().toLowerCase()).split("\\|")) {
				if (se.equals(e.getAddress().getHostAddress())){
					//Bukkit.getServer().getPluginManager().callEvent(new LoginEvent(e.getPlayer()));
					return;
				}
			}
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, plugin.doesntmatch);
		}
	}
}
