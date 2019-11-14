/*
 *  Semantic risadinha
 *  FT - UNICAMP
 */
package rdfdata;

import bloggerdata.AllPosts;
import bloggerdata.BlogHeader;
import bloggerdata.DadosPost;
import bloggerdata.PostPublisher;
import java.text.Normalizer;
import java.util.List;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import risadinha.Resumo;
import risadinha.ResumoStore;

/**
 *
 * @author karii
 */
public class RdfGraph {

    private Dataset dataset;
    private final Model model;
    private final Resource resourceBlog;
    private Bag posts;
    
    public RdfGraph(){
        model = ModelFactory.createDefaultModel();
        resourceBlog = model.createResource(Namespaces.NS_RISADINHA + "BlogDrRisadinha");
    }

    public void create(List<DadosPost> postList, BlogHeader blogHeader, RdfRepository store) {
        store.end();
        store.write();
        //model = addBlog(blogHeader);
        store.add(addBlog(blogHeader));
        int count=0;
        for (DadosPost post: postList) {
            post.setPostContentData();
            count++;
            store.add(addPost(post));
            if(count>10){
                break;
            }
        }
        //RdfRepository store = new RdfRepository("MyDatabases/Dataset");
        
        store.commit();
    }

    public Model addBlog(BlogHeader blogHeader) {
        Property property;
        Literal literal;
        Statement statement;
        Resource resource;

        Model model = ModelFactory.createDefaultModel();
        
        //Blog é do tipo SIOC.Forum = RDF.type = SIOC.Forum
        //Adiciona o tipo do blog (conteiner)
        property = model.createProperty(Namespaces.NS_RDF, "type");
        resource = model.createResource(Namespaces.NS_SIOC+"Forum");
        statement = model.createStatement(resourceBlog, property, resource);
        model.add(statement);
        
        //Id do Blog = DC.identifier
        property = model.createProperty(Namespaces.NS_DC, "identifier");
        literal = model.createLiteral(blogHeader.getId() + "");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Título do Blog = DC.title
        property = model.createProperty(Namespaces.NS_DC, "title");
        literal = model.createLiteral(blogHeader.getName() + "");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Descrição do Blog = DC.description
        property = model.createProperty(Namespaces.NS_DC, "description");
        literal = model.createLiteral(blogHeader.getDescription() + "");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Última atualização do Blog = DC.modified 
        property = model.createProperty(Namespaces.NS_DCT, "modified");
        literal = model.createLiteral(blogHeader.getLastUpdated() + "");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Data de criação do Blog = DC.issued
        property = model.createProperty(Namespaces.NS_DCT, "issued");
        literal = model.createLiteral(blogHeader.getPublished() + "");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Linguagem do Blog = DC.language
        property = model.createProperty(Namespaces.NS_DC, "language");
        literal = model.createLiteral("pt-BR");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);

        //Url do Blog = DC.source
        property = model.createProperty(Namespaces.NS_DC, "source");
        literal = model.createLiteral("http://www.drrisadinha.org.br/");
        statement = model.createStatement(resourceBlog, property, literal);
        model.add(statement);
        
        return model; 
        
    }

    public Model addPost(DadosPost post) {
        Property property;
        Literal literal;
        Statement statement;
        Resource resourcePost;
        Resource resource;
        Resource resourceType;

        Model model = ModelFactory.createDefaultModel();
        
        resourcePost = model.createResource(Namespaces.NS_RISADINHA + post.getId() + "");
        
        //Post é do tipo SIOC.Post = RDF.type = SIOC.Post
        //Adiciona o tipo do Post (item)
        resource = model.createResource(Namespaces.NS_SIOC+"Post");
        property = model.createProperty(Namespaces.NS_RDF, "type");
        statement = model.createStatement(resourcePost, property, resource);
        model.add(statement);
        
        //has_container - The Container to which this Item belongs.
        //Adiciona o Post (item) ao Blog (conteiner)
        property = model.createProperty(Namespaces.NS_SIOC, "has_container");
        statement = model.createStatement(resourcePost, property, resourceBlog);
        model.add(statement);

        //Id do Post = DC.identifier
        literal = model.createLiteral(post.getId() + "");
        property = model.createProperty(Namespaces.NS_DC, "identifier");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //Título do Post = DC.title
        literal = model.createLiteral(post.getTitle() + "");
        property = model.createProperty(Namespaces.NS_DC, "title");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //Data da postagem (Date of formal issuance (e.g., publication) of the resource.) = DC.Issued
        literal = model.createLiteral(post.getPublishedDate() + "");
        property = model.createProperty(Namespaces.NS_DCT, "issued");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //Data da atualização = DC.Updated
        literal = model.createLiteral(post.getUpdatedDate() + "");
        property = model.createProperty(Namespaces.NS_DCT, "modified");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //Resumo do conteúdo = DC.Abstract
        literal = model.createLiteral(post.getMensagemCurta() + "");
        property = model.createProperty(Namespaces.NS_DCT, "abstract");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //url = DC.source
        literal = model.createLiteral(post.getUrl() + "");
        property = model.createProperty(Namespaces.NS_DC, "source");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);

        //imagem = DC.relation (related resource)
        literal = model.createLiteral(post.getImageUrl() + "");
        property = model.createProperty(Namespaces.NS_FOAF, "depiction");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);
        
        //language = DC.language
        literal = model.createLiteral("pt-BR");
        property = model.createProperty(Namespaces.NS_DC, "language");
        statement = model.createStatement(resourcePost, property, literal);
        model.add(statement);
        
        //Pessoa que publicou o Post
        
        PostPublisher publisher = post.getPublisher();
        
        if(publisher != null){

            //Post -> publisher -> recurso publisher
            property = model.createProperty(Namespaces.NS_DC, "publisher");
            resource = model.createResource(Namespaces.NS_RISADINHA + publisher.getPublisherName() + "");
            statement = model.createStatement(resourcePost, property, resource);
            model.add(statement);
            
            //resource -> rdf:type -> foaf:Person 
            property = model.createProperty(Namespaces.NS_RDF, "type");
            resourceType = model.createResource(Namespaces.NS_FOAF+"Person");
            statement = model.createStatement(resource, property, resourceType);
            model.add(statement);
            
            //resource -> DC:identifier -> id
            property = model.createProperty(Namespaces.NS_DC, "identifier");
            literal = model.createLiteral (publisher.getId());
            statement = model.createStatement(resource, property, literal);
            model.add(statement);
            
            //resource -> FOAF:name -> name
            property = model.createProperty(Namespaces.NS_FOAF, "name");
            literal = model.createLiteral (publisher.getPublisherName());
            statement = model.createStatement(resource, property, literal);
            model.add(statement);
      
            //resource -> FOAF:homepage -> site
            property = model.createProperty(Namespaces.NS_FOAF, "homepage");
            literal = model.createLiteral (publisher.getUrl());
            statement = model.createStatement(resource, property, literal);
            model.add(statement);
            
        }
        
        // autores
        for (String autor : post.getAutor()) {
            //Post -> autor -> recurso autor
            property = model.createProperty(Namespaces.NS_SIOC, "has_Creator");
            resource = model.createResource(Namespaces.NS_RISADINHA + autor + "");
            statement = model.createStatement(resourcePost, property, resource);
            model.add(statement);
            
            //id -> rdf:type -> foaf:Person 
            property = model.createProperty(Namespaces.NS_RDFS, "type");
            resourceType = model.createResource(Namespaces.NS_FOAF+"Person");
            statement = model.createStatement(resource, property, resourceType);
            model.add(statement);
            
            //id -> FOAF:name -> name
            property = model.createProperty(Namespaces.NS_FOAF, "name");
            literal = model.createLiteral (autor);
            statement = model.createStatement(resource, property, literal);
            model.add(statement);
        }

        // revisores
        for (String revisor : post.getRevisores()) {
            //System.out.println("no rdf: "+ revisor);
            //Post -> revisor -> recurso revisor
            property = model.createProperty(Namespaces.NS_DC, "contributor");
            resource = model.createResource(Namespaces.NS_RISADINHA + revisor + "");
            statement = model.createStatement(resourcePost, property, resource);
            model.add(statement);
            
            //id -> rdf:type -> foaf:Person 
            property = model.createProperty(Namespaces.NS_RDFS, "type");
            resourceType = model.createResource(Namespaces.NS_FOAF+"Person");
            statement = model.createStatement(resource, property, resourceType);
            model.add(statement);
            
            //id -> FOAF:name -> name
            property = model.createProperty(Namespaces.NS_FOAF, "name");
            literal = model.createLiteral (revisor);
            statement = model.createStatement(resource, property, literal);
            model.add(statement);
        }

        model.add(addSubject(post, resourcePost));
        
        return model;
    }
    
    public Model addSubject(DadosPost post, Resource resourcePost){
        Property property;
        Literal literal;
        Statement statement;
        Model model = ModelFactory.createDefaultModel();
        
        //Adiciona o post ao blog
        property = model.createProperty(Namespaces.NS_DC, "subject");
        
        RdfService rdfService = new RdfService(); 
               
        String[] labels = post.getLabels();
        if (labels != null) {
            for (String label : labels) {
                //System.out.println("---------------------------------------------------------");
                //System.out.println("label: " +removerEspaco(label));
                List<Resource> recursoDBPedia = rdfService.queryDBPedia(removerEspaco(label));
                if(!recursoDBPedia.isEmpty()){
                    statement = model.createStatement(resourcePost, property, recursoDBPedia.get(0));
                    model.add(statement);
                    //System.out.println("adicionou rec: " +recursoDBPedia.get(0).toString());
                }else{
                    literal = model.createLiteral (label);
                    //System.out.println("adicionou literal: " +label);
                    statement = model.createStatement(resourcePost, property, literal);
                    model.add(statement);
                }
            }
        }
        
        
        return model;
    }
    
    public boolean blogExists(String id, RdfRepository tdb){
        List<String> resultados = tdb.queryList("SELECT ?identifier " +
                        "WHERE " +
                        "{ " +
                        "  <http://drrisadinha.org/BlogDrRisadinha> <http://purl.org/dc/elements/1.1/identifier> ?identifier ." +
                        "}  ","identifier");
           
            //System.out.println(resultados);
        if(resultados.contains(id)){
            //System.out.println("Existe: "+resultados.toString)+ header.getId());
            return true;
        }else{
            //System.out.println("Não existe"+resultados.toString()+header.getId());
            return false;
        }
    }
    
    public List<String> getIdPosts(RdfRepository tdb){
        List<String> resultados = tdb.queryList("SELECT ?identifier " +
                        "WHERE " +
                        "{ " +
                        "  ?o <http://purl.org/dc/elements/1.1/Identifier> ?identifier ." +
                        "}  ","identifier");
           
        //System.out.println(resultados);
        return resultados;
        
    }
    
    public List<String> getPerson(){
        RdfRepository tdb = new RdfRepository("MyDatabases/Dataset");
        tdb.end();
        tdb.load();
        List<String> resultados = tdb.queryList("SELECT ?identifier " +
                        "WHERE " +
                        "{ " +
                        "  ?o <http://xmlns.com/foaf/0.1/name> ?identifier ." +
                        "}  ","identifier");
           
        System.out.println(resultados);
        tdb.end();
        return null;
        
    }
    
    public String removerEspaco(String string) {
        //string = Normalizer.normalize(string, Normalizer.Form.NFD);
        //string = string.replaceAll("[^\\p{ASCII}]", "");
        string = string.replaceAll("\u200b", "");
        string = string.replaceAll(" ", "");
        return string;
    }
    
}
