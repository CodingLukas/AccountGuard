package lt.bukkit.accountguard;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	public File ipFile = new File("plugins/AccountGuard", "ip.yml");
	public FileConfiguration ip = YamlConfiguration.loadConfiguration(ipFile);
	public File messagesFile;
	public FileConfiguration messages;
	public FileConfiguration config;
	public String languageFileName;
	public String doesntmatch;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		updateConfig();
		
		final Main plugin = this;
		final File file = this.getFile();
		if (getConfig().getBoolean("auto-update")) {
			getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
				@Override
				public void run() {
					try {
						Metrics metrics = new Metrics(plugin);
						metrics.start();
					} catch (IOException e) {
					}
					getLogger().info("Checking for a new update..");
					new Updater(plugin, 67561, file, Updater.UpdateType.DEFAULT, true);
				}
			});
		}
		languageFileName = "messages_" + getConfig().getString("language") + ".yml";
		createDefaultFiles();
		registerListener();
	}

	@Override
	public void onDisable() {
		try {
			ip.save(ipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createDefaultFiles() {
		saveDefaultConfig();
		messagesFile = new File(getDataFolder(), languageFileName);
		try {
			if (!messagesFile.exists()) {
				saveResource(languageFileName, true);
			}
			messages = YamlConfiguration.loadConfiguration(messagesFile);
			doesntmatch = repl(messages.getString("doesntmatch"));
			getCommand("accountguard").setExecutor(new AccountGuardCommand(this, repl(messages.getString("noperm"))));
		} catch (IllegalArgumentException e) {
			getLogger().info("Error: There is no " + languageFileName + " translation file. Using default translation file messages_en.yml.");
			languageFileName = "messages_en.yml";
			createDefaultFiles();
		}
	}

	public void updateConfig() {
		if (getConfig().getInt("cfg-version") != 4) {
			saveResource("config.yml", true);
			reloadConfig();
		}
	}

	public void registerListener() {
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
	}

	public String repl(String r) {
		return r.replace("&", "\u00a7");
	}
}
