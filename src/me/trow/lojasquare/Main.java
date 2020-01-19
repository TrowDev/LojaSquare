package me.trow.lojasquare;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.trow.lojasquare.api.ProductPreActiveEvent;
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
	private static int tempoChecarItens;
	private static List<String> produtosConfigurados = new ArrayList<>();
	private static LojaSquare ls;
	private static String servidor;
	private static boolean debug;
	
	public void onEnable() {
		ConsoleCommandSender b = Bukkit.getConsoleSender();
		try{
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			pl=this;
			saveDefaultConfig();
			String keyapi = getKeyAPI();
			ls = new LojaSquare();
			if(!checarIPCorreto(b, keyapi)) return;
			debug = getConfig().getBoolean("LojaSquare.Debug",true);
			servidor = getConfig().getString("LojaSquare.Servidor",null);
			if(!checarServidorConfigurado(b)) return;
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
			tempoChecarItens = getConfig().getInt("Config.Tempo_Checar_Compras",60);
			// INICIO definindo variaveis do LojaSquare
			b.sendMessage("§3[LojaSquare] §bDefinindo variaveis de conexao com o site §dLojaSquare§b...");
			ls.setCredencial(keyapi);
			ls.setConnectionTimeout(getConfig().getInt("LojaSquare.Connection_Timeout",1500));
			ls.setReadTimeout(getConfig().getInt("LojaSquare.Read_Timeout",3000));
			ls.setDebug(debug);
			b.sendMessage("§3[LojaSquare] §bVariaveis definidas com sucesso!");
			// FIM definindo variaveis do LojaSquare
			b.sendMessage("§3[LojaSquare] §bIniciando checagem automatica de entregas...");
			Bukkit.getPluginManager().registerEvents(new ProdutoListener(), this);
			checarEntregas();
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		}catch (Exception e){
			e.printStackTrace();
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
	
	public boolean checarIPCorreto(ConsoleCommandSender b,String nome){
		String result = getLojaSquare().get("/v1/autenticar");
		boolean bo = result.contains("true");
		if (!bo) {
			b.sendMessage("§3[LojaSquare] §cDesativado...");
			b.sendMessage("§3Criador: §3Trow");
			b.sendMessage("§cMotivo: " + result);
			b.sendMessage("§3Key-API: §a"+nome);
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		}
		b.sendMessage("§3[LojaSquare] §bIP da maquina configurado!");
		return true;
	}
	
	public boolean checarServidorConfigurado(ConsoleCommandSender b){
		if(servidor==null||servidor.equalsIgnoreCase("Nome-Do-Servidor")){
			b.sendMessage("§4[LojaSquare] §cDesativando...");
			b.sendMessage("§4[LojaSquare] §cPara que o plugin seja ativado com sucesso, e necessario configurar o nome do seu servidor na config.yml");
			b.sendMessage("§4[LojaSquare] §cAtualmente o nome do servidor esta definido como: §a"+(servidor==null?"§4NAO DEFINIDO":servidor));
			b.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			Bukkit.getPluginManager().disablePlugin(getInstance());
			return false;
		}
		return true;
	}
	
	public void checarEntregas(){
		new BukkitRunnable() {
			public void run() {
				List<ItemInfo> itens = getLojaSquare().getTodasEntregas();
				printDebug("");
				printDebug("§3[LojaSquare] §bItens Size: §a"+itens.size());
				if(itens!=null&&itens.size()>0){
					for(final ItemInfo item:itens){
						printDebug("§3[LojaSquare] §bItem: §a"+item.toString()+" §b// subServer: §a"+item.getSubServidor()+" // Servidor: §d"+servidor);
						if(!item.getSubServidor().equalsIgnoreCase(servidor)) continue;
						final Player p = Bukkit.getPlayer(item.getPlayer());
						printDebug("§3[LojaSquare] §bPlayer: §a"+item.getPlayer()+"§b // P NULL? §a"+(p==null));
						if(p==null&&!getConfig().getBoolean("Grupos."+item.getGrupo()+".Ativar_Com_Player_Offline",false)){
							boolean b = item.getProduto().equalsIgnoreCase("DISPUTA")&&item.getGrupo().equalsIgnoreCase("DISPUTA");
							boolean b1 = item.getProduto().equalsIgnoreCase("RESOLVIDO")&&item.getGrupo().equalsIgnoreCase("RESOLVIDO");
							if(!b&&!b1) continue;
						}
						if(!produtoConfigurado(item.getGrupo())&&p!=null){
							printDebug("§3[LojaSquare] §bProduto §a"+item.getGrupo()+"§b nao configurado!");
							p.sendMessage(getMsg("Msg.Produto_Nao_Configurado").replace("@grupo", item.getGrupo()));
							continue;
						}
						new BukkitRunnable() {
							public void run() {
								printDebug("§3[LojaSquare] §bPre Product Active Event");
								ProductPreActiveEvent pae = new ProductPreActiveEvent(p==null?null:p, item);
								Bukkit.getPluginManager().callEvent(pae);
							}
						}.runTask(pl);
					}
				}
			}
		}.runTaskTimerAsynchronously(pl, 20*10, 20*getTempoChecarItens());
	}
	
	public static void printDebug(String s){
		if(debug){
			for(Player p:getOnlinePlayers()){
				if(p.isOp()||p.hasPermission("lojasquare.debug")){
					p.sendMessage(s);
				}
			}
		}
	}
	
	public static Player[] getOnlinePlayers() {
		try {
			Method method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object players = method.invoke(null);
			if (players instanceof Player[]) {
				return (Player[]) players;
			} else {
				Collection<?> c = ((Collection<?>) players);
				return c.toArray(new Player[c.size()]);
			}

		} catch (Exception e) {
		}
		return null;
	}
	
	public int getTempoChecarItens(){
		if(tempoChecarItens<=0) return 60;
		return tempoChecarItens;
	}
	
	public LojaSquare getLojaSquare(){
		return ls;
	}
	
	public String getKeyAPI(){
		return getMsg("LojaSquare.Key_API");
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
