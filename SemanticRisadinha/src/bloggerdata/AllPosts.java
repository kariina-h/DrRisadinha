/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */

package bloggerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapeia o objeto JSON com todas as postagens
 * do blog.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class AllPosts {
    private String kind;
    private String nextPageToken;
    private List<DadosPost> items;
    
    public AllPosts() {
        items = new ArrayList<>();
    }
    
    public void setPosts(List<DadosPost> listaPosts) {
        items = listaPosts;
    }
    
    public List<DadosPost> getPosts() {
        return Collections.unmodifiableList(items);
    }

    public String getKind() {
        return kind;
    }
    
}
