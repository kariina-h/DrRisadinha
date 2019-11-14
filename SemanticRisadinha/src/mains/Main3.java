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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import risadinha.Log;
import risadinha.ResumoContentParser;

/**
 *
 * @author ricarte at ft.unicamp.br
 */
public class Main3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int count = 1;
        try {
//            FileHandler logFile = new FileHandler("risadinha.log");
//            logFile.setFormatter(new SimpleFormatter());
//            Log.LOG_RIS.addHandler(logFile);
            Log.LOG_RIS.setLevel(Level.INFO);
            BlogJsonAccess blog = new BlogJsonAccess("http://www.drrisadinha.org.br/",
                    "AIzaSyBrWRP49pj7dSMObLwW3WM4s5agG5yO9ds");
            BlogHeader header = blog.getBlogHeader();
            
            System.out.println("Blog " + header.getName()
                    + " tem " + header.getNumberOfPosts() + " artigos.");
            LocalDateTime updated = header.getLastUpdated();
            System.out.println("Última atualização em: "
                    + updated.format(DateTimeFormatter.ofPattern("ccc dd MMM yyyy HH:mm")));
            List<DadosPost> postList = blog.getPosts(header);
            DecimalFormat form = new DecimalFormat("000");
            for (DadosPost post : postList) {
                String content = post.getContent();
                Log.LOG_RIS.log(Level.INFO, "--- Resumo {0} ---", new Object[]{count});
                ResumoContentParser parser = new ResumoContentParser(content);
                StringBuilder clean = new StringBuilder("[Resumo " + count + "]\n");
                clean.append("[Mensagem curta:]\n").append(parser.getMensagemCurta()).append("\n");
                clean.append("[Mensagem longa:]\n").append(parser.getMensagem()).append("\n");
                clean.append("[Autor:]\n");
                Collection<String> autores = parser.getAutor();
                for (String autor : autores) {
                    clean.append(autor).append("\n");
                }
                clean.append("[Referência:]\n").append(parser.getReferencia()).append("\n");
                clean.append("[Revisor:]\n");
                Collection<String> revisores = parser.getRevisor();
                if (revisores.size() > 0) {
                    for (String revisor : revisores) {
                        clean.append(revisor).append("\n");
                    }
                }
                Path resPath = Paths.get("resumos", "Resumo" + form.format(count++) + ".txt");
                Files.deleteIfExists(resPath);
                Files.write(Files.createFile(resPath),
                        (post.getTitle() + "\n\n-----------------------------\n\n"
                                + content + "\n\n-----------------------------\n\n"
                                + parser.cleanTags(content) + "\n\n-----------------------------\n\n"
                                + clean.toString()).getBytes());
            }
        } catch (MalformedURLException ex) {
            Log.LOG_RIS.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Log.LOG_RIS.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Log.LOG_RIS.log(Level.WARNING, "{0} durante resumo {1}", new Object[]{ex.toString(), count});
        }
    }
    
}
