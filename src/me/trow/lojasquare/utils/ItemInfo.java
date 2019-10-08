package me.trow.lojasquare.utils;

public class ItemInfo {
	
	private String player,produto,servidor,grupo,codigo,status;
	private int statusID,dias,idEntrega,quantidade;
	private long atualizadoEm;
	
	public ItemInfo(String p){
		player=p;
	}
	
	public long getUltimoUpdate(){
		return atualizadoEm;
	}
	
	public void setUltimoUpdate(long l){
		atualizadoEm=l;
	}
	
	public String toString(){
		String a = "ItemInfo={player:"+player+",produto:"+produto+",servidor:"+servidor+",grupo:"+grupo+",codigo:"+codigo+",status:"+status
				+",statusID:"+statusID+",dias:"+dias+",idEntrega:"+idEntrega+",quantidade:"+quantidade+",lastUpdate:"+atualizadoEm+"}";
		return a;
	}
	
	public String getPlayer(){
		return player;
	}
	
	public void setPlayer(String s){
		player=s;
	}
	
	public String getProduto(){
		return produto;
	}
	
	public String getServidor(){
		return servidor;
	}
	
	public String getGrupo(){
		return grupo;
	}
	
	public String getCodigo(){
		return codigo;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String s){
		status=s;
	}
	
	public int getQuantidade(){
		return quantidade;
	}
	
	public int getIDEntrega(){
		return idEntrega;
	}
	
	public int getDias(){
		return dias;
	}
	
	public int getStatusID(){
		return statusID;
	}
	
}