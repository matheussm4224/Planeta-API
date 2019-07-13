package br.com.msilva.java.dao;

import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.msilva.java.model.Planeta;

/*
 * Esta classe faz a manipulação dos dados contido no banco de dados.
 * @autor -  Matheus da silva Menezes.
 * 
 */
public class PlanetasDAO {
	
	MongoCollection<BasicDBObject> collection; // variavel de estanciação da collection
	
	
	/*
	 * Contrutor que referencia qual collection o banco irá referencia.
	 * 
	 * @param database - Conexão com database.
	 * @autor -  Matheus da silva Menezes.
	 */
	public PlanetasDAO(MongoDatabase database) {
		this.collection = database.getCollection("planetas", BasicDBObject.class); //conexão a collection planeta.
	}
	
	/*obterPlanetas*/
	/*
	 * Este método faz a consulta de todos os planetas do banco .
	 * 
	 * @return - retorna um FindIterable de objetos json.
	 * @autor -  Matheus da silva Menezes.
	 */
	public FindIterable<BasicDBObject> obterPlanetas(){
		return this.collection.find(); // consultad no banco buscando por todos os planetas.
	}
	
	/*obterPlaneta*/
	/*
	 * Este método faz a consulta de planetas pelo nome.
	 * 
	 * @param nome - String com o nome do planeta para consulta.
	 * @return  - retorna o planeta com o nome correspondente.
	 * @autor -  Matheus da silva Menezes.
	 */
	public FindIterable<BasicDBObject> obterPlaneta(String nome){
		BasicDBObject document = new BasicDBObject("nome", Pattern.compile("^"+nome+"$" , Pattern.CASE_INSENSITIVE)); // criação do obejeto nome para consulta.
		return this.collection.find(document); // retorna a consulta do banco de dados pelo nome.
	}
	
	/*obterPlanetaId*/
	/*
	 * Este método faz consulta de planetas por id.
	 * 
	 * @param id - String com o id do planeta para consulta.
	 * @see BasicDBObject - clase de tranformação em objeto  json.
	 * @return - se o id for valido retornara o objeto json do planeta,
	 * caso o id não seja valido retorna um objeto json com a mensagem id invalido.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	public BasicDBObject obterPlanetaId(String id){
		if(this.idValido(id) == true) {// se o id passado e valido
			return this.collection.find(new BasicDBObject("_id", new ObjectId(id))).first(); //retorna o resultado da consulta por id. 
		}else {
			return new BasicDBObject("Error", "O id e invalido!"); // retorna esse json se o id e invalido.
		}
	}
	
	
	/*adicionarPlaneta*/
	/*
	 * Este método adiciona um objeto do tipo planeta ao banco.
	 * 
	 * @param planeta - Objeto Planeta
	 * @return - se a inserção for u sucesso ele retornara true.
	 * @throw - MongoWriteException, MongoWriteConcernException, MongoException .
	 * @see BasicDBObject - clase de tranformação em objeto  json.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	public boolean adicionarPlaneta(Planeta planeta) throws MongoWriteException, MongoWriteConcernException, MongoException  {
			BasicDBObject objeto = new BasicDBObject("nome", planeta.getNome()); // criando um objeto json contedo nome, clima e terreno.
			objeto.append("clima", planeta.getClima());
			objeto.append("terreno", planeta.getTerreno());
			objeto.append("qtdFilmes", planeta.getQtdfilmes());
			this.collection.insertOne(objeto); // inserção de um dado no banco. 
			return true; // retorna true se a inserção foi um sucesso.
	}
	
	
	/*deletarPlaneta*/
	/*
	 * Este método deleta um planeta pelo id no banco.
	 * 
	 * @param id - String com o id do planeta para a remoção.
	 * @return - retorna um objeto json com o objeto deletado ou mensagem com id invalido.
	 * @see BasicDBObject - clase de tranformação em objeto  json.
	 * @autor -  Matheus da silva Menezes.
	 */
	public BasicDBObject deletarPlaneta(String id) {
		if(this.idValido(id) == true) { // se o id passado e valido.
			return this.collection.findOneAndDelete(new BasicDBObject("_id", new ObjectId(id))); // consulta e remoção de um planeta por id. Retorna um objeto json com o planeta deletado. 
		}else {
			return new BasicDBObject("Error", "O id é invalido!"); // retorna um objeto json se o id e invalido.
		}
	}
	
	/*idValido*/
	/*
	 * Este metodo verifica se o id passado e um objectId.
	 * 
	 * @param id - String com o id para a verificação.
	 * @return - retorna um boolean com valor true se valido e false se invalido.
	 * @autor -  Matheus da silva Menezes.
	 */
	private boolean idValido(String id) {
		return ObjectId.isValid(id);  // verificando se o id e valido.
	}
	
	
	
}
