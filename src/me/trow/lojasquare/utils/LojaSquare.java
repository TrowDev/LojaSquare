package me.trow.lojasquare.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LojaSquare {
	
	/*public static void main(String[] args) {
		LojaSquare ls = new LojaSquare();
		ls.setCredencial("wCkyUEK5CCVtaIaMy6iqhRphvqiyoh");
		List<ItemInfo> li = ls.getTodasEntregas();//getEntregasPlayer("Trow_Games");
		if(li.size()>0){
			for(ItemInfo ii:li){
				print(ii.toString());
				//ls.updateDelivery(ii);
			}
		}else{
			print("Nada para entregar ao player Trow_Games.");
		}
	}*/
	
	private int connectionTimeout;
	private int readTimeout;
	private String credencial;
	
	public LojaSquare(){
		connectionTimeout=1500;
		readTimeout=3000;
	}
	
	public String getCredencial(){
		return credencial;
	}
	
	public void setCredencial(String keyAPI){
		credencial=keyAPI;
	}
	
	public void setConnectionTimeout(int milisec){
		connectionTimeout=milisec;
	}
	
	public void setReadTimeout(int milisec){
		readTimeout=milisec;
	}
	
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
	
	public int getReadTimeout(){
		return readTimeout;
	}
	
	public boolean updateDelivery(ItemInfo ii){
		if(ii==null) return false;
		return update(String.format("/v1/queue/%s/%d", ii.getPlayer(),ii.getIDEntrega()));
	}
	
	public List<ItemInfo> getTodasEntregas(){
		String player = "*";
		List<ItemInfo> itens = new ArrayList<>();
		try{
			String result = get(String.format("/v1/queue/%s", player));
			JsonObject job = new JsonParser().parse(result).getAsJsonObject();
			for(int i=1;i<=job.entrySet().size();i++){
				try{
					ItemInfo ii = new Gson().fromJson(job.getAsJsonObject(i+""), ItemInfo.class);
					itens.add(ii);
				}catch (Exception e){
					print("[LojaSquare] Nao foi possivel processar o item "+job.getAsJsonObject(i+"").toString()+". Erro: "+e.getMessage());
				}
			}
			return itens;
		}catch (Exception e){
			return itens;
		}
	}
	
	public List<ItemInfo> getEntregasPlayer(String player){
		List<ItemInfo> itens = new ArrayList<>();
		try{
			String result = get(String.format("/v1/queue/%s", player));
			JsonObject job = new JsonParser().parse(result).getAsJsonObject();
			for(int i=1;i<=job.entrySet().size();i++){
				try{
					ItemInfo ii = new Gson().fromJson(job.getAsJsonObject(i+""), ItemInfo.class);
					itens.add(ii);
				}catch (Exception e){
					print("[LojaSquare] Nao foi possivel processar o item "+job.getAsJsonObject(i+"").toString()+". Erro: "+e.getMessage());
				}
			}
			return itens;
		}catch (Exception e){
			return itens;
		}
	}
	
	public String get(final String endpoint){
		HttpsURLConnection c = null;
		int statusCode = 0;
		try {
			final StringBuilder sb2 = new StringBuilder();
			final URL u = new URL(sb2
					.append("https://api.lojasquare.com.br/")
					.append(endpoint).toString());
			c = (HttpsURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Authorization", getCredencial());
			c.setRequestProperty("Content-Type", "application/json");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(getConnectionTimeout());
			c.setReadTimeout(getReadTimeout());
			c.connect();
			statusCode = c.getResponseCode();
			if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(c.getInputStream()));
				final StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				br.close();
				return sb.toString();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,ex);
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex2) {
					ex2.printStackTrace();
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex2);
				}
			}
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex3) {
					ex3.printStackTrace();
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex3);
				}
			}
		}
		return getResponseByCode(statusCode);
	}

	public boolean update(final String endpoint){
		HttpsURLConnection c = null;
		int statusCode = 0;
		try {
			final StringBuilder sb = new StringBuilder();
			final URL u = new URL(sb
					.append("https://api.lojasquare.com.br/")
					.append(endpoint).toString());
			c = (HttpsURLConnection) u.openConnection();
			c.setRequestMethod("PUT");
			c.setRequestProperty("Authorization", getCredencial());
			c.setRequestProperty("Content-Type", "application/json");
			c.setDoOutput(true);
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(getConnectionTimeout());
			c.setReadTimeout(getReadTimeout());
			c.connect();
			statusCode = c.getResponseCode();
			if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
				return true;
			}
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,ex);
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex2) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex2);
				}
			}
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex3) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex3);
				}
			}
		}
		print(getResponseByCode(statusCode));
		return false;
	}
	
	public static String getResponseByCode(int i){
		if(i==0){
			return "[LojaSquare] Servidor sem conexao com a internet.";
		}
		if(i==401){
			return "[LojaSquare] Conexao nao autorizada! Por favor, confira se a sua credencial esta correta.";
		}
		if(i==404){
			return "[LojaSquare] Nao foi encontrado nenhum registro para a requisicao efetuada.";
		}
		if(i==405){
			return "[LojaSquare] Erro ao autenticar sua loja! Verifique se sua assinatura e credencial estao ativas!";
		}
		if(i==406){
			return "[LojaSquare] Nao foi executada nenhuma atualizacao referente ao requerimento efetuado.";
		}
		return "[LojaSquare] Provavel falha causada por entrada de dados incompativeis com o requerimento efetuado. Status Code: "+i;
	}

	public static int parseInt(String a) {
		return Integer.parseInt(a);
	}

	public static void print(String a) {
		System.out.println(a);
	}

	public static void print(int a) {
		System.out.println(a);
	}

	public static void print(double a) {
		System.out.println(a);
	}

}
