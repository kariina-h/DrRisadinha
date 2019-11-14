/*
 *  Semantic risadinha
 *  FT - UNICAMP
 */
package rdfdata;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb.TDBFactory;

/**
 * Armazenamento de dados em RDF.
 * 
 * @author Karina Hagiwara
 * @author ricarte at ft.unicamp.br
 */
public class RdfRepository {
    private final String directory;
    private final Dataset dataset;
    
    public RdfRepository(String dir) {
        directory = dir;
        dataset = TDBFactory.createDataset(directory);
    }
    
    public void add(Model model) {
        Model modelTdb = dataset.getDefaultModel();
        modelTdb.add(model);
        dataset.close();
    }
    
    public void load() {
        dataset.begin(ReadWrite.READ);
    }
    
    public void write() {
        dataset.begin(ReadWrite.WRITE);
    }
    
    public void commit() {
        dataset.commit();
    }
    
    public void end() {
        dataset.end();
    }
    
    public void query(String qs1) {
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
            ResultSet rs = qExec.execSelect();
            ResultSetFormatter.out(rs);
            
        }
    }    
    
    public List<String> queryList(String qs1, String str) {
        List<String> resultados = new ArrayList<>();
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
            ResultSet rs = qExec.execSelect();
            /*QuerySolution qs = rs.next();
            Literal literal;
            literal = qs.getLiteral("identifier");
            ResultSetFormatter.out(rs);*/
            while (rs.hasNext()) { 
                resultados.add(rs.next().get(str).toString());
            }
	}
            return resultados;
    }
        
    
    public void export() {

        OutputStream out;
        try {
            out = new FileOutputStream("risadinha.ttl");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RdfGraph.class.getName()).log(Level.SEVERE, null, ex);
            out = System.out;
        }

        RDFDataMgr.write(out, dataset.getDefaultModel(), RDFFormat.TURTLE_PRETTY);
    }
    
}
