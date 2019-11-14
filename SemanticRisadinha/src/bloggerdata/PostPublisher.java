/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */

package bloggerdata;

import com.google.gson.JsonElement;

/**
 * Mapeia o objeto JSON
 * com dados de autor da postagem.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class PostPublisher {
    private String id;
    private String displayName;
    private String url;
    private JsonElement image;
    
    public String getPublisherName() {
        return displayName;
    }
    
    public String getId() {
        return id;
    }
    
    public String getUrl() {
        return url;
    }
}
