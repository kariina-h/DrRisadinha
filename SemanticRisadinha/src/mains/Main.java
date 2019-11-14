/*
 * Amostra de código para obtenção de dados de blogger
 * usando a Blogger API v3.0
 * documentada em https://developers.google.com/blogger/docs/3.0/using
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */
package mains;

import bloggerdata.BlogHeader;
import bloggerdata.BlogJsonAccess;
import bloggerdata.DadosPost;
import rdfdata.RdfGraph;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import rdfdata.RdfRepository;
import rdfdata.RdfService;
import risadinha.ResumoStore;

/**
 *
 * @author ricarte at ft.unicamp.br
 * @author Karina Hagiwara
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String key = "";
            //Pega o JSON dos dados do Blog: Fale com Dr. Risadinha
            BlogJsonAccess blog = new BlogJsonAccess("http://www.drrisadinha.org.br/",
                    key);
            BlogHeader header = blog.getBlogHeader();

            System.out.println("Blog " + header.getName()
                    + " tem " + header.getNumberOfPosts() + " artigos.");
            LocalDateTime updated = header.getLastUpdated();
            System.out.println("Última atualização em: "
                    + updated.format(DateTimeFormatter.ofPattern("ccc dd MMM yyyy HH:mm")));
            
            RdfRepository tdb = new RdfRepository("MyDatabases/Dataset");
            tdb.load();
           RdfGraph graph = new RdfGraph();
            
            //verifica se o rdf do blog já existe
            if(graph.blogExists(String.valueOf(header.getId()), tdb)==true){
                //existe, então é só atualizar dados
                //id dos posts já existentes no tdb
                List<String> idPosts = graph.getIdPosts(tdb);
                tdb.end();
                tdb.write();
                //id dos posts novos
                List<DadosPost> postList = blog.getPosts(header);
                for (DadosPost post : postList) {
                    if(!idPosts.contains(String.valueOf(post.getId()))){
                        post.setPostContentData();
                        tdb.add(graph.addPost(post));
                        idPosts.add(String.valueOf(post.getId()));
                    }
                }
                tdb.commit();
                
            }else{
                //System.out.println("Não existe");
                //não existe, criar dado rdf do blog
                List<DadosPost> postList = blog.getPosts(header);
                for (DadosPost post : postList) {
                    post.setPostContentData();
                }
                graph.create(postList, header, tdb);
            
            }
            //Verificar o conteúdo do banco
            tdb.end();
            tdb.load();
          
          
            /*tdb.query("PREFIX DC:<http://purl.org/dc/elements/1.1/>"
                    + "SELECT * "
                    + "where {"
                    + "?p DC:title \"Qual a causa do sangramento do nariz? \" . "
                    + "?p DC:subject ?sub . "
                    + "?p DC:identifier ?id  }");*/
            
             /*tdb.query("SELECT ?t ?u"
                    + "where {?p DC:title ?t ."
                    + "?p DC:source ?u ."
                    + "?p DC:subject \"Unhas\"}");*/
            
           
            tdb.export();

            
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

}

    /*
    List<DadosPost> postList = blog.getPosts(header);
        for (DadosPost post : postList) {
            post.setPostContentData();
            //System.out.println(post.getTitle()+" - "+post.getImageUrl());
            String[] labels = post.getLabels();
            if (labels!=null){
                for (String label:labels){
                    System.out.println(" - "+label);
                }
            }else{
                    System.out.println("NÃOOOOOOOO TEM LABEL");
            }
        }
    */