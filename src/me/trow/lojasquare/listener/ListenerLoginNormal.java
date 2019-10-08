package me.trow.lojasquare.listener;

import me.trow.lojasquare.Main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerLoginNormal implements Listener{
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Main.getInstance().checarPlayer(e.getPlayer());
	}
	
}
