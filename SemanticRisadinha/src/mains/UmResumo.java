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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricarte at ft.unicamp.br
 */
public class UmResumo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BlogJsonAccess blog = new BlogJsonAccess("http://www.drrisadinha.org.br/",
                    "AIzaSyBrWRP49pj7dSMObLwW3WM4s5agG5yO9ds");
            BlogHeader header = blog.getBlogHeader();

            System.out.println("Blog " + header.getName()
                    + " tem " + header.getNumberOfPosts() + " artigos.");
            LocalDateTime updated = header.getLastUpdated();
            System.out.println("Última atualização em: "
                    + updated.format(DateTimeFormatter.ofPattern("ccc dd MMM yyyy HH:mm")));

            int resumo = 76;
            List<DadosPost> postList = blog.getPosts(header);
            DadosPost post = postList.get(resumo);
            String content = post.getContent();
            content = content + "\n-----------------\n";
//            content = content + new ResumoContentParser().cleanTags(content);
            Files.write(Files.createFile(Paths.get("Resumo" + resumo + ".txt")), content.getBytes());

        } catch (MalformedURLException ex) {
            Logger.getLogger(UmResumo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UmResumo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
