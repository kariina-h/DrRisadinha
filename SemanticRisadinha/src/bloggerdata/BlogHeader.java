/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */

package bloggerdata;

import com.google.gson.JsonElement;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Mapeia o objeto JSON com o cabeçalho
 * do blog.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class BlogHeader {
    private String kind;
    private long id;
    private String name;
    private String description;
    private String published;
    private String updated;
    private String url;
    private String selfLink;
    private Resources posts;
    private Resources pages;
    private JsonElement locale;
    private AllPosts allPosts;
    
    public long getId() {
        return id;
    }
    
    public int getNumberOfPosts() {
        return posts.getTotalItems();
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getSelfLink() {
        return selfLink;
    }
    
    public LocalDateTime getPublished() {
        return OffsetDateTime.parse(published).toLocalDateTime();
    }
    
    public LocalDateTime getLastUpdated() {
        return OffsetDateTime.parse(updated).toLocalDateTime();
    }
    
    public AllPosts getAllPosts() {
        return allPosts;
    }
    
    public void setAllPosts(AllPosts allPosts) {
        this.allPosts = allPosts;
    }
    
    @Override
    public String toString() {
        return name + "\n" +
                "id: " + id + "\n" +
                "updated: " + updated + "\n" +
                "posts: " + getNumberOfPosts();
    }
}
