/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdfdata;

import java.util.ArrayList;
import java.util.List;
import static javafx.scene.input.KeyCode.TAB;
import static org.apache.jena.assembler.JA.FileManager;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 *
 * @author karii
 */
public class RdfService {
  
    public List<Resource> queryDBPedia(String recursoSubject) {
        
        String serviceStr = "http://dbpedia.org/sparql";
        
	String queryStr = "PREFIX dbpedia-pt: <http://pt.dbpedia.org/resource/>\r\n"
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
                        + "select distinct ?recursoSubject where {?recursoSubject owl:sameAs dbpedia-pt:"+ recursoSubject +" } LIMIT 10";
        Query query = QueryFactory.create(queryStr);
        List<Resource> resultados = new ArrayList<>();
        
        //Começa a exeução remota
        try ( 
            
            QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceStr, query) ) {
            //Coloca timeout do dbpedia
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
 
            //Executa a query
            ResultSet rs = qexec.execSelect();    
            //ResultSetFormatter.out(System.out, rs, query);
            while (rs.hasNext()) { 
                resultados.add(rs.next().getResource("recursoSubject"));
            }
            qexec.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultados;
    }
    
    public List<Resource> queryDBpedia(String serviceStr, String queryStr, String dado) {
        
        
        Query query = QueryFactory.create(queryStr);
        List<Resource> resultados = new ArrayList<>();
        
        //Começa a exeução remota
        try ( 
            
            QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceStr, query) ) {
            //Coloca timeout do dbpedia
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
 
            //Executa a query
            ResultSet rs = qexec.execSelect();    
            ResultSetFormatter.out(System.out, rs, query);
            while (rs.hasNext()) { 
                resultados.add(rs.next().getResource(dado));
                System.out.println(rs.next().getResource(dado).toString());
            }
            qexec.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultados;
    }

}
