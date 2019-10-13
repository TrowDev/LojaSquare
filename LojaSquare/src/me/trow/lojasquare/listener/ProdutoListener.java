package me.trow.lojasquare.listener;

import me.trow.lojasquare.Main;
import me.trow.lojasquare.api.ProductActiveEvent;
import me.trow.lojasquare.api.ProductPreActiveEvent;
import me.trow.lojasquare.utils.ItemInfo;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ProdutoListener implements Listener{
	
	private static Main pl = Main.getInstance();
	
	@EventHandler
	public void preActive(ProductPreActiveEvent e){
		if(e.isCancelled()) return;
		ItemInfo ii = e.getItemInfo();
		if(pl.getLojaSquare().updateDelivery(ii)){
			pl.print("§6[LojaSquare] §ePreparando entrega do produto do compra: §7"+ii.toString());
			ProductActiveEvent pae = new ProductActiveEvent(e.getPlayer(), ii);
			Bukkit.getPluginManager().callEvent(pae);
		}else{
			pl.print("§4[LojaSquare] §cNao foi possivel atualizar o status da compra: §a"+ii.toString()+"§c para: 'Entregue'. Portanto, a entrega nao foi feita!");
		}
	}
	
	@EventHandler
	public void active(ProductActiveEvent e){
		ItemInfo ii = e.getItemInfo();
		if(e.isCancelled()){
			pl.print("§4[LojaSquare] §cAtivacao da compra: §a"+ii.toString()+"§c foi cancelada por meio do evento §aProductActiveEvent§c"
					+ ", mas ja foi marcado no site com status 'Entregue'.");
			return;
		}
		boolean isMoney = pl.getConfig().getBoolean("Grupos."+ii.getGrupo()+".Money");
		double qntMoney = 0;
		if(isMoney){
			qntMoney = pl.getConfig().getDouble("Grupos."+ii.getGrupo()+".Quantidade_De_Money")*ii.getQuantidade();
		}
		for(String cmds:pl.getConfig().getStringList("Grupos."+ii.getGrupo()+".Cmds_A_Executar")){
			cmds=cmds.replace("@money", (qntMoney>0?""+qntMoney:"")).replace("@grupo", ii.getGrupo());
			cmds=cmds.replace("@dias", ii.getDias()+"").replace("@player", ii.getPlayer());
			cmds=cmds.replace("@qnt", ii.getQuantidade()+"");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds);
		}
		pl.print("§3[LojaSquare] §bEntrega do produto §a"+ii.toString()+"§b concluida com sucesso!");
	}
	
}
