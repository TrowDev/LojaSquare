package me.trow.lojasquare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.trow.lojasquare.api.ProductPreActiveEvent;
import me.trow.lojasquare.listener.ListenerLoginAuthme;
import me.trow.lojasquare.listener.ListenerLoginNormal;
import me.trow.lojasquare.listener.ProdutoListener;
import me.trow.lojasquare.utils.ItemInfo;
import me.trow.lojasquare.utils.LojaSquare;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin{
	
	private static Main pl;
	private static int tempoChecarItensAposLogin;
	private static HashMap<String, Long> ultimaChecagem = new HashMap<>();
	private static List<String> produtosConfigurados = new ArrayList<>();
	private static LojaSquare ls;
	
	public void onEnable() {
		ConsoleCommandSender b = Bukkit.getConsoleSender();
		try{
			pl=this;
			saveDefaultConfig();
			ls = new LojaSquare();
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			b.sendMessage("§3[LojaSquare] §bAtivado...");
			b.sendMessage("§3Criador: §bTrow");
			b.sendMessage("§bDesejo a voce uma otima experiencia com o §dLojaSquare§b.");
			// Carregando todos os grupos de produtos configurados
			b.sendMessage("§3[LojaSquare] §bIniciando o carregamento dos nomes dos grupos de itens para serem entregues...");
			for(String v:getConfig().getConfigurationSection("Grupos").getKeys(false)){
				produtosConfigurados.add(v);
				b.sendMessage("§3[LojaSquare] §bGrupo carregado: §a"+v);
			}
			b.sendMessage("§3[LojaSquare] §bGrupos de entregas foram carregados com sucesso!");
			// Definindo outras variaveis para nao ir buscar na Config.yml toda vez que for necessario.
			tempoChecarItensAposLogin=getConfig().getInt("Config.Tempo_Checar_Compras_Apos_Login");
			// INICIO definindo variaveis do LojaSquare
			b.sendMessage("§3[LojaSquare] §bDefinindo variaveis de conexao com o site §dLojaSquare§b...");
			ls.setCredencial(getKeyAPI());
			ls.setConnectionTimeout(getConfig().getInt("LojaSquare.Connection_Timeout",1500));
			ls.setReadTimeout(getConfig().getInt("LojaSquare.Read_Timeout",3000));
			b.sendMessage("§3[LojaSquare] §bVariaveis definidas com sucesso!");
			// FIM definindo variaveis do LojaSquare
			b.sendMessage("§3[LojaSquare] §bDefinindo sistema de entrega apos login OU apos entrar no servidor.");
			if(Bukkit.getPluginManager().getPlugin("AuthMe")!=null){
				b.sendMessage("§3[LojaSquare] §bAs entregas serao feitas apos o player fazer login com senha. AuthMe encontrado! Hook habilitado.");
				Bukkit.getPluginManager().registerEvents(new ListenerLoginAuthme(), this);
			}else{
				b.sendMessage("§3[LojaSquare] §bAs entregas serao feitas apos o player entrar no servidor. AuthMe nao encontrado! Hook nao habilitado.");
				Bukkit.getPluginManager().registerEvents(new ListenerLoginNormal(), this);
			}
			Bukkit.getPluginManager().registerEvents(new ProdutoListener(), this);
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		}catch (Exception e){
			b.sendMessage("§4[LojaSquare] §cErro ao iniciar o plugin LojaSquare.");
			b.sendMessage("§4[LojaSquare] §cErro: §a"+e.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() {
		ConsoleCommandSender b = Bukkit.getConsoleSender();
		b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		b.sendMessage("§3[LojaSquare] §bDesativado...");
		b.sendMessage("§3Criador: §bTrow");
		b.sendMessage("§bAgradeco por usar meu(s) plugin(s)");
		b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
	}
	
	public void checarPlayer(final Player p){
		if(p==null){
			pl.print("§4[LojaSquare] §cchecarPlayer > parametro Player p nulo!");
			return;
		}
		new BukkitRunnable() {
			public void run() {
				if(!Main.getInstance().dentroDaUltimaChecagem(p.getName())){
					ultimaChecagem.put(p.getName(), System.currentTimeMillis()/1000);
					List<ItemInfo> itens = getLojaSquare().getEntregasPlayer(p.getName());
					if(itens!=null&&itens.size()>0){
						for(final ItemInfo item:itens){
							if(!produtoConfigurado(item.getGrupo())){
								p.sendMessage(getMsg("Msg.Produto_Nao_Configurado").replace("@grupo", item.getGrupo()));
								continue;
							}
							new BukkitRunnable() {
								public void run() {
									ProductPreActiveEvent pae = new ProductPreActiveEvent(p, item);
									Bukkit.getPluginManager().callEvent(pae);
								}
							}.runTask(pl);
						}
					}
				}
			}
		}.runTaskLaterAsynchronously(Main.getInstance(), 20*getTempoChecarComprasAposLogin());
	}
	
	public LojaSquare getLojaSquare(){
		return ls;
	}
	
	public String getKeyAPI(){
		return getMsg("LojaSquare.Key_API");
	}
	
	public boolean dentroDaUltimaChecagem(String nick){
		if(ultimaChecagem.containsKey(nick)){
			long now = System.currentTimeMillis()/1000;
			long bef = ultimaChecagem.get(nick);
			if(now-bef<60){
				return true;
			}
			ultimaChecagem.remove(nick);
			return false;
		}
		return false;
	}
	
	public int getTempoChecarComprasAposLogin(){
		if(tempoChecarItensAposLogin<=0) return 10;
		return tempoChecarItensAposLogin;
	}
	
	public boolean produtoConfigurado(String grupo){
		return produtosConfigurados.contains(grupo);
	}
	
	public String getMsg(String s){
		if(getConfig().getString(s)==null){
			Bukkit.broadcastMessage("§cLinha nao encontrada na config: §a"+s);
			return "";
		}
		return getConfig().getString(s).replace("&", "§");
	}
	
	public void print(String s){
		Bukkit.getConsoleSender().sendMessage(s);
	}
	
	public static Main getInstance(){
		return pl;
	}
	
}
