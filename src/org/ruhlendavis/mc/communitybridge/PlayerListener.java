package org.ruhlendavis.mc.communitybridge;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ruhlendavis.mc.utility.Log;

public class PlayerListener implements Listener
{
	private Configuration config;
	private Log log;
	private WebApplication webapp;

	/**
	 * Constructor
	 *
	 * @param Log The log object passed in from onEnable().
	 * @param Configuration The configuration object passed in from onEnable().
	 * @param WebApplication The web application object passed in from onEnable().
	 */
	public PlayerListener(Log log, Configuration config, WebApplication webapp)
	{
		this.config = config;
		this.log = log;
		this.webapp = webapp;
	}

	/**
	 * This method is called by CraftBukkit as the player connects to the server.
	 * We perform the initial linking here so that we can reject the login if
	 * linking-kick-unregistered is turned on.
	 *
	 * @param AsyncPlayerPreLoginEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		String playerName = event.getName();
		webapp.onPreLogin(playerName);
		if (webapp.isPlayerRegistered(playerName))
		{
			log.fine(playerName + " linked to web application user ID #" + webapp.getUserID(playerName) + ".");
		}
		else
		{
			if (config.linkingKickUnregistered)
			{
				log.info(playerName + " kicked because they are not registered.");
				event.setKickMessage(config.messages.get("link-unregistered-player"));
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			} // if config.linkingKickUnregistered
		} // if isPlayerRegistered
	} // onPlayerPreLogin

	/**
	 * This method is called by CraftBukkit as the player joins the server.
	 *
	 * @param PlayerJoinEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = event.getPlayer().getName();
		if (webapp.isPlayerRegistered(playerName))
		{
			if (config.linkingNotifyRegistered)
			{
				String message = config.messages.get("link-registered-player");
				player.sendMessage(ChatColor.GREEN + message);
			}
		}
		else
		{
			if (config.linkingNotifyUnregistered)
			{
				String message = config.messages.get("link-unregistered-player");
				player.sendMessage(ChatColor.RED + message);
			} // if config.linkingNotifyUnregistered
		} // if isPlayerRegistered
	}

	/**
	 * This method is called by CraftBukkit when a player quits/disconnects.
	 *
	 * @param PlayerQuitEvent The event object (see CraftBukkit API).
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		webapp.onQuit(player.getName());
	} // onPlayerQuit
} // PlayerListener class


//		if (config.statisticsTrackingEnabled)
//		{
//			int id = Main.getUserId(player.getName());
//			if (id > 0)
//			{
//				Main.updateStatistics(id, player);
//
//				if (config.onlinestatusEnabled)
//				{
//					try
//					{
//						if (config.multiTables && config.multiTablesUseKey)
//						{
//							Main.sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.multi_table_value_field + " = '" + config.onlinestatusValueOffline + "' WHERE " + config.multi_table_user_id_field + " = '" + id + "' and " + config.multi_table_key_field +" = '" + config.onlinestatusKeyValue + "'");
//						}
//						else if(config.multiTables)
//						{
//							Main.sql.updateQuery("UPDATE " + config.multi_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOffline + "' WHERE " + config.multi_table_user_id_field + " = '" + id + "'");
//						}
//						else
//						{
//							Main.sql.updateQuery("UPDATE " + config.users_table + " SET " + config.onlinestatusColumn + " = '" + config.onlinestatusValueOnline + "' WHERE " + config.user_id_field + " = '" + id + "'");
//						}
//					}
//					catch (MalformedURLException e)
//					{
//						e.printStackTrace();
//					}
//					catch (InstantiationException e)
//					{
//						e.printStackTrace();
//					}
//					catch (IllegalAccessException e)
//					{
//						Main.log.severe("Broken Set User Offline SQL Query, check your config.yml");
//						e.printStackTrace();
//					}
//				}
//			}
//		}
