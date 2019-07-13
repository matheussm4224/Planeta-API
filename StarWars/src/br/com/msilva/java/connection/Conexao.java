package br.com.msilva.java.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


/*
 * Esta classe faz a conexão com o banco de dados.
 * 
 */
public class Conexao {
	
	private MongoClient mongoClient; // variavel com MongoClient.
	private MongoDatabase database; // variavel com MongoDatabase.
	
	/*
	 * Contrutor que inicia a conexão com banco .
	 * @autor -  Matheus da silva Menezes.
	 */
	public Conexao() {
		try {
			this.mongoClient = MongoClients.create("mongodb://localhost:27017");
			this.database = mongoClient.getDatabase("star_wars");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	/*obterDataBase*/
	/*
	 * Este Metodo retorna a referencia do banco de dados conectado.
	 * 
	 * @return - Referencia do banco de dados.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	public MongoDatabase obterDataBase() {
		return this.database;
	}
	
	/*fecharConexao*/
	/*
	 * Este Metodo Encerra a conexão com o banco de dados.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	public void fecharConexao() {
		this.mongoClient.close();
	}

}
