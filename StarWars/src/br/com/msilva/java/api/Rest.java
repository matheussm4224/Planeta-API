package br.com.msilva.java.api;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;

import br.com.msilva.java.connection.Conexao;
import br.com.msilva.java.dao.PlanetasDAO;
import br.com.msilva.java.model.Planeta;

/*
 * Esta classe faz a comunicação da api com o usuario.
 * @autor -  Matheus da silva Menezes.
 * 
 */
@Path("/planetas")
public class Rest {
	
	private Conexao conn; //varival de conexão com o banco.
	
	public Rest() {
		conn = new Conexao(); //instância da classe de conexão.
	}
	
	/* obterPlanetas */
	/*
	 * Retorna uma listagem com todos os registro de planetas contido no banco. 
	 * Este método conecta no banco de dados nosql em mongodb://localhost:27017. Em seguida, 
	 * retorna as listagem de planetas para quem está consumindo a api.
	 * 
	 * @return Retorna um reponse para quem chamou com um lista de objeto json.
	 * @see PlanetaDAO - Manipulação de baco de dados.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obterPlanetas() {
		try {
			List<BasicDBObject> listaPlanetas = new ArrayList<BasicDBObject>(); // nova instância da lista de objeto Planeta.
			PlanetasDAO dao = new PlanetasDAO(conn.obterDataBase()); // nova instância do objeto PlanetaDAO, para manipulção de banco.
			Iterator<BasicDBObject> planetas = dao.obterPlanetas().iterator(); // obtendo dados do abnco de todos o planetas nele contido.
			while(planetas.hasNext()) {
				listaPlanetas.add(planetas.next()); // Interando sobre os objeto Planeta e adicionando a uma lista 
			}
			return Response.status(200).entity(listaPlanetas.toString()).build(); // retornando a lista de planetas.
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("{\"Error\": \"Error interno, contate um técnico!\"}").build();
		}finally {
			conn.fecharConexao(); // fechando conexão com o banco.
		}
	}
	
	/* obterPlaneta */
	/*
	 * Retorna um objeto json após o usuario fornerce o nome do planeta se o mesmo existir no banco.
	 * Este método conecta no banco de dados nosql em mongodb://localhost:27017. Em seguinda, retorna o objeto com base no nome passado.
	 * 
	 * @param nome - String nome do planeta.
	 * @return Retorna um reponse para quem chamou com objeto json.
	 * @see PlanetaDAO - Manipulação de baco de dados.
	 * @autor -  Matheus da silva Menezes.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find/{nome}")
	public Response obterPlaneta(@PathParam("nome") String nome) {
		try {
			PlanetasDAO dao  = new PlanetasDAO(conn.obterDataBase()); //nova instância do objeto PlanetaDAO, para manipulção de banco.
			Iterator<BasicDBObject> planeta = dao.obterPlaneta(nome).iterator(); //obtendo planeta com base no nome recebido
			if(planeta.hasNext()) {
				return Response.status(200).entity(planeta.next().toJson()).build(); //retorna o planeta consultado.
			}else {
				return Response.status(200).entity("{\"Info\": \"Nenhum dado encontardo!\"}").type(MediaType.APPLICATION_JSON).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("{\"Error\": \"Error interno, contate um técnico!\"}").build();
		}finally {
			conn.fecharConexao(); // fechando conexão com o banco.
		}
	}
	
	/* obterPlanetaId */
	/*
	 * Retorna um objeto de um planeta, após o usuario fornerce o id do planeta se o mesmo existir no banco.
	 * Este método conecta no banco de dados nosql em mongodb://localhost:27017. Em seguinda, retorna o objeto com base no id passado.
	 * 
	 * @param id - id do planeta.
	 * @return -Retorna um reponse para quem chamou com objeto json.
	 * @autor -  Matheus da silva Menezes.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public Response obterPlanetaId(@PathParam("id") String id) {
		try {
			PlanetasDAO dao = new PlanetasDAO(conn.obterDataBase()); //nova instância do objeto PlanetaDAO, para manipulção de banco.
			BasicDBObject planeta = dao.obterPlanetaId(id); //obtendo planeta com base no id recebido.
			if(planeta != null) { // verifica se o planeta buscado e diferente de nulo.
				return Response.status(200).entity(planeta.toJson()).build(); //retorna o planeta .
			}else {
				return Response.status(200).entity("{\"Info\": \"Nenhum dado encontardo!\"}").type(MediaType.APPLICATION_JSON).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("{\"Error\": \"Error interno, contate um técnico!\"}").build();
		}finally {
			conn.fecharConexao(); // fechando conexão com o banco.
		}
	}
	
	/* adicionarPlaneta */
	/* Retorna o objeto json adicionado no banco.
	 * Este método realiza um consulta por nome ao banco, se o planeta não existir ele irá criar no banco esse novo objeto Planeta passado por parametro. 
	 * Consome uma api chamada https://swapi.co para saber se o planeta já apareceu em algum filme do star wars antes de adicionar ao banco.
	 * 
	 * @param planeta - objeto Planeta contendo nome, clima e terreno.
	 * @return - Retorna um reponse para quem chamou com objeto json.
	 * @see PlanetaDAO - Manipulação de baco de dados.
	 * @autor -  Matheus da silva Menezes.
	 * 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response adicionarPlaneta(Planeta planeta) {
		try {
			PlanetasDAO dao = new PlanetasDAO(conn.obterDataBase()); //nova instância do objeto PlanetaDAO, para manipulção de banco.
			if(planeta.getNome() != null && planeta.getClima() != null && planeta.getTerreno() != null) { // Varifica se os dados enviados estão vazios .
				FindIterable<BasicDBObject> nomePlanetaExiste  = dao.obterPlaneta(planeta.getNome()); //obtendo planeta com base no nome.
				if(nomePlanetaExiste.first() == null) {// verifica de o planeta consultado e nulo 
					
					planeta = this.obterFilmePlanetaExiste(planeta); // Consulta a api https://swapi.co para descobrir a quantodades de filmes que o planeta esteve. 
					dao.adicionarPlaneta(planeta); // adiciona o planeta no banco de dados.
					return Response.status(200).entity(planeta).build();// retorna o objeto planeta adiciona no banco.
					
				}else {// se planeta existir no banco 
					String value = nomePlanetaExiste.first().get("_id").toString(); // pega o id do planeta .
					return Response.status(404).entity("{\"Error\": \"O planeta ja e existente!\", \"_id\" : \""+value+"\"}").type(MediaType.APPLICATION_JSON).build();
				}
			}else {
				return Response.status(200).entity("{\"Error\": \"Campo Vázio\"}").type(MediaType.APPLICATION_JSON).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("{\"Error\": \"Error interno, contate um técnico!\"}").build();
		}finally {
			conn.fecharConexao(); // fechando conexão com o banco.
		}
		
	}
	
	
	/* deletarPlaneta */
	/*
	 * Retorna o objeto json deletado no banco .
	 * Este método deleta o planeta se o mesmo existir, ao passar o id por parametro ele irá consultar no banco de dados nosql em 
	 * mongodb://localhost:27017 se o id existe no banco ele irá deletar o planeta referente. 
	 * Em seguida, retorna para o usuario o objeto do planeta deletado.
	 * 
	 * @param id - id do planeta.
	 * @return - Retorna um reponse para quem chamou com objeto json.
	 * @see PlanetaDAO - Manipulação de baco de dados.
	 * @autor -  Matheus da silva Menezes.
	 */
	@DELETE
	@Path("/{id}")
	public Response deletarPlaneta(@PathParam("id") String id) {
		try {
			PlanetasDAO dao = new PlanetasDAO(conn.obterDataBase()); //nova instância do objeto PlanetaDAO, para manipulção de banco.
			BasicDBObject planetaDeletado = dao.deletarPlaneta(id); // deleta um planeta com base em um id passado e retorna o planeta excluido.
			if(planetaDeletado != null) {// verifica se o plnaeta deletado e diferente de nulo.
				return Response.status(200).entity(planetaDeletado.toJson()).build(); // retorna o objeto do planeta deletado.
			}else {
				return Response.status(200).entity("{\"Info\": \"Nenhum dado encontardo!\"}").type(MediaType.APPLICATION_JSON).build(); //retorno de nunum dado enontrado.
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("{\"Error\": \"Error interno, contate um técnico!\"}").build(); // status de error interno.
		}finally {
			conn.fecharConexao(); // fechando conexão com o banco.
		}
	}
	
	/* obterFilmePlanetaExiste */
	/*
	 * Retorna um objeto planeta com a quantidade de filmes que o planeta passado apareceu.
	 * Este método consome dados da api https://swapi.co. 
	 * Em seguida, retorna a quantidade de vezes que o planeta apareceu nos filmes star wars.
	 *  
	 * @param planeta - Objeto Planeta.
	 * @return - retorna o objeto Planeta com a quantidade de filmes.
	 * @autor -  Matheus da silva Menezes.
	 */
	private Planeta obterFilmePlanetaExiste(Planeta planeta) throws IllegalArgumentException, NullPointerException, JsonSyntaxException {
			Client cliente = ClientBuilder.newClient(); // nova instância do client api.
			String resultado = cliente.target("https://swapi.co/api/planets/").queryParam("search", "{planeta}")
					.resolveTemplate("planeta", planeta.getNome()).request(MediaType.APPLICATION_JSON)
					.get(String.class); //faz a requisição a api https://swapi.co para obter os filmes que o objeto planeta ja passou.
			
			Gson gson = new Gson(); // nova instância do gson
			JsonObject json = gson.fromJson(resultado, JsonObject.class); // converte String em jsonObejct.
			if(json.getAsJsonArray("results").size() > 0) { // verifica se o array retornado pela requisição e maio que zero.
				JsonArray value = json.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray("films"); // Manipulação de jason para extração do array de results.
				planeta.setQtdfilmes(value.size()); // pega a quantidade de filmes que  o planeta apareceu.
			}else { // se o array retornado e zero.
				planeta.setQtdfilmes(json.getAsJsonArray("results").size()); // adiciona zero a quantidade de filmes.
			}

			return planeta; // retorna o objeto planeta.

	}

}
