package me.trow.lojasquare.listener;

import me.trow.lojasquare.Main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.xephi.authme.events.LoginEvent;

public class ListenerLoginAuthme implements Listener{
	
	@EventHandler
	public void onLogin(LoginEvent e){
		Main.getInstance().checarPlayer(e.getPlayer());
	}
	
}
