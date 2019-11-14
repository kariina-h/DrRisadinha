/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */
package bloggerdata;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Provê acesso às informações do
 * blog via API de blogger.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class BlogJsonAccess {
    private final String base = "https://www.googleapis.com/blogger/v3/blogs/";
    private final String blogUrl;
    private final String key;
    private final Gson gson;
    private final BlogHeader header;

    public BlogJsonAccess(String url, String key) throws MalformedURLException, IOException {
        blogUrl = url;
        this.key = key;
        gson = new Gson();
        header = gson.fromJson(new InputStreamReader(
                new URL(base + "byurl?url=" + blogUrl + "&key=" + key).openStream()), BlogHeader.class);
    }

    public BlogHeader getBlogHeader() {
        return header;
    }
    
    public List<DadosPost> getPosts(BlogHeader header) throws MalformedURLException, IOException {
        AllPosts list = gson.fromJson(new InputStreamReader(
                new URL(base + header.getId() + 
                        "/posts?key=" + key + 
                        "&maxResults=" + header.getNumberOfPosts()).openStream()), 
                AllPosts.class);
        System.out.println(base + header.getId() + 
                        "/posts?key=" + key + 
                        "&maxResults=" + header.getNumberOfPosts());
        header.setAllPosts(list);
        return list.getPosts();
    }

    public List<DadosPost> getPostsByTitle(String search) throws IOException {
        List<DadosPost> result = new ArrayList<>();
        List<DadosPost> items = getPosts(header);
        for (DadosPost post : items) {
            if (post.getTitle().toLowerCase().contains(search.toLowerCase())) {
                result.add(post);
            }
        }
        return result;
    }
}
