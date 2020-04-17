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

import org.bukkit.Bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LojaSquare {
	
	public static void main(String[] args) {
		LojaSquare ls = new LojaSquare();
		ls.setCredencial("A6FRZEDm8m4DHQ71ruzeuKFLXGzkgn");
		List<ItemInfo> li = ls.getTodasEntregas();//getEntregasPlayer("Trow_Games");
		if(li.size()>0){
			for(ItemInfo ii:li){
				print(ii.toString());
				//ls.updateDelivery(ii);
			}
		}else{
			print("Nada para entregar ao player Trow_Games.");
		}
	}// */
	
	private int connectionTimeout;
	private int readTimeout;
	private String credencial;
	private boolean debug;
	
	public LojaSquare(){
		connectionTimeout=1500;
		readTimeout=3000;
		debug=false;
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

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean canDebug() {
		return debug;
	}
	
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
	
	public int getReadTimeout(){
		return readTimeout;
	}
	
	/**
	 * O metodo executa o update da entrega, informando na api que a entrega com ID 'x' foi realizada.
	 * @param ItemInfo
	 * @return Retornara true se o update da entrega for realizado com sucesso.
	 */
	public boolean updateDelivery(ItemInfo ii){
		if(ii==null) return false;
		return update(String.format("/v1/queue/%s/%d", ii.getPlayer(),ii.getIDEntrega()));
	}
	
	/**
	 * Retorna uma lista de produtos a serem entregues (Retorno para todos os players).
	 * @return List<ItemInfo> lista de produtos a serem entregues.
	 */
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
	
	/**
	 * Retorna uma lista de produtos a serem entregues para um player em especifico.
	 * @param player - Nick do player, usado como referencia para filtrar as entregas.
	 * @return List<ItemInfo> lista de produtos a serem entregues.
	 */
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
	
	/**
	 * Este metodo faz a requisicao na api, na requisicao do endpoint.
	 * @param endpoint - Caminho onde a chamada deve ser realizada.
	 * @return Retorna o JSON, caso tenha produtos a serem retornados.
	 */
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
			print("Status Code From "+endpoint+" : "+statusCode);
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
			if(canDebug()){
				ex.printStackTrace();
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,ex);
			}else{
				msgConsole("�4[LojaSquare] �cErro ao tentar conexao com o site. Erro: �a"+ex.getMessage());
			}
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex2) {
					if(canDebug()){
						ex2.printStackTrace();
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex2);
					}else{
						msgConsole("�4[LojaSquare] �cErro ao fechar a conexao com o site. Erro: �a"+ex2.getMessage());
					}
				}
			}
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex3) {
					if(canDebug()){
						ex3.printStackTrace();
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex3);
					}else{
						msgConsole("�4[LojaSquare] �cErro ao fechar a conexao com o site. Erro: �a"+ex3.getMessage());
					}
				}
			}
		}
		return getResponseByCode(statusCode);
	}

	/**
	 * Este metodo faz a requisicao de update na api.
	 * @param endpoint - Caminho onde a requisicao deve ser realizada.
	 * @return Retorna true, caso o update seja realizado com sucesso.
	 */
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
		}  catch (IOException ex) {
			if(canDebug()){
				ex.printStackTrace();
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,ex);
			}else{
				msgConsole("�4[LojaSquare] �cErro ao tentar conexao com o site. Erro: �a"+ex.getMessage());
			}
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex2) {
					if(canDebug()){
						ex2.printStackTrace();
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex2);
					}else{
						msgConsole("�4[LojaSquare] �cErro ao fechar a conexao com o site. Erro: �a"+ex2.getMessage());
					}
				}
			}
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex3) {
					if(canDebug()){
						ex3.printStackTrace();
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex3);
					}else{
						msgConsole("�4[LojaSquare] �cErro ao fechar a conexao com o site. Erro: �a"+ex3.getMessage());
					}
				}
			}
		}
		print(getResponseByCode(statusCode));
		return false;
	}
	
	public String getResponseByCode(int i){
		if(i==0){
			return "[LojaSquare] �cServidor sem conexao com a internet.";
		}else if(i==401){
			return "[LojaSquare] �cConexao nao autorizada! Por favor, confira se a sua credencial esta correta.";
		}else if(i==403){
			return "[LojaSquare] �cO IP enviado e diferente do que temos em nosso Banco de Dados. IP da sua Maquina: �a"+get("/v1/autenticar/ip");
		}else if(i==404){
			return "[LojaSquare] �cNao foi encontrado nenhum registro para a requisicao efetuada.";
		}else if(i==405){
			return "[LojaSquare] �cErro ao autenticar sua loja! Verifique se sua assinatura e credencial estao ativas!";
		}else if(i==406){
			return "[LojaSquare] �cNao foi executada nenhuma atualizacao referente ao requerimento efetuado.";
		}
		return "[LojaSquare] �cProvavel falha causada por entrada de dados incompativeis com o requerimento efetuado. Status Code: "+i;
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
	
	public void msgConsole(String s){
		Bukkit.getConsoleSender().sendMessage(s);
	}

}
