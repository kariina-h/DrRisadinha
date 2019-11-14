/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */

package bloggerdata;

import com.google.gson.JsonElement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import risadinha.ResumoContentParser;

/**
 * Mapeia o objeto JSON referente 
 * a uma postagem do blog.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class DadosPost {
    private String kind;
    private long id;
    private JsonElement blog;
    private String published;
    private String updated;
    private String url;
    private String selfLink;
    private String title;
    private String content;
    private PostPublisher author;
    private JsonElement replies;
    private String[] labels;
    
    private String mensagemCurta;
    private String mensagem;
    private String imageUrl;
    private final Collection<String> referencia;
    private final Collection<String> autor;
    private final Collection<String> revisor;
    
    public DadosPost(){
        referencia = new ArrayList<>();
        autor = new ArrayList<>();
        revisor = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return title + " : " + published + " [ " + url + " ]";
    }
      
    public void setPostContentData(){
            
            ResumoContentParser parser = new ResumoContentParser(content);

            String msg = parser.getMensagem();
            if (!msg.equals("\n")) {
                this.setMensagemCurta(parser.getMensagemCurta());
                this.setMensagem(parser.getMensagem());
                this.setImageUrl(parser.getUrlImagem());
                Collection<String> referencias = parser.getReferencia();
                for (String ref : referencias) {
                    this.addReferencia(ref);
                }
                Collection<String> autores = parser.getAutor();
                for (String aut : autores) {
                    this.addAutor(aut);
                }                        
                Collection<String> revisores = parser.getRevisor();
                for (String rev : revisores) {
                    this.addRevisor(rev);
                }
        }
            
         
    } 
    
    public String getTitle() {
        return title;
    }
    
    public long getId() {
        return id;
    }
    
    public LocalDateTime getUpdatedDate() {
        return OffsetDateTime.parse(updated).toLocalDateTime();
    }
    
    public LocalDateTime getPublishedDate() {
        return OffsetDateTime.parse(published).toLocalDateTime();
    }
    
    public String getContent() {
        return content;
    }
    
    public PostPublisher getPublisher() {
        return author;
    }
    
    public String[] getLabels() {
        return labels;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getMensagemCurta() {
        return mensagemCurta;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public Collection<String> getReferencia() {
        return Collections.unmodifiableCollection(referencia);
    }

    public Collection<String> getAutor() {
        return Collections.unmodifiableCollection(autor);
    }

    public Collection<String> getRevisores() {
        return Collections.unmodifiableCollection(revisor);
    }
    
    public void setMensagemCurta(String mensagemCurta) {
        this.mensagemCurta = mensagemCurta;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
        
        public void addReferencia(String referencia) {
        this.referencia.add(referencia);
    }

    public void addAutor(String valorAutor) {
        autor.add(valorAutor);
    }

    public void addRevisor(String valorRevisor) {
        revisor.add(valorRevisor);
    }
    
}
