package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private SimpleDirectedWeightedGraph<Business,DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<String,Business> idMap;
	
	public Model() {
		this.dao= new YelpDao();
		this.vertici= new ArrayList<>();
		idMap= new HashMap<>();
	}
	
	public List<String> getCities(){
		return this.dao.getCities();
	}
	
	public void creaGrafo(String citta, Year anno) {
		this.grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo vertici
		this.vertici=this.dao.getLocaliAnnoCitta(citta, anno,idMap);
		Graphs.addAllVertices(this.grafo, this.vertici);
		//aggiungo archi
		List<Arco> archi=this.dao.getArco(citta, anno, idMap);
		for(Arco a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getB2(), a.getB1(), a.getPeso());
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Business getLocaleMigliore() {
		double max=0;
		Business migliore=null;
		for(Business b : this.vertici) {
			double val=0.0;
			for(DefaultWeightedEdge e1 : this.grafo.incomingEdgesOf(b)) {
				val+=this.grafo.getEdgeWeight(e1);
			}
			for(DefaultWeightedEdge e2 : this.grafo.outgoingEdgesOf(b)) {
				val-=this.grafo.getEdgeWeight(e2);
			}
			if(val>max) {
				max=val;
				migliore=b;
			}
		}
		return migliore;
	}
}
