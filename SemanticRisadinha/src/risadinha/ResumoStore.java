/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */
package risadinha;

import bloggerdata.BlogHeader;
import bloggerdata.BlogJsonAccess;
import bloggerdata.DadosPost;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Organiza o armazenamento local, em arquivo serializado,
 * das postagens obtidas do blog Dr Risadinha.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class ResumoStore implements Serializable {
    
    private List<Resumo> store;
    private LocalDateTime lastVisited;
    private final String filename = "risadinha.dat";
    
    public ResumoStore(BlogJsonAccess blog) throws IOException, ClassNotFoundException {
        if (Files.exists(Paths.get(filename))) {
            restore();
        } else {
            lastVisited = LocalDateTime.MIN;
            store = new ArrayList<>();
        }
        BlogHeader header = blog.getBlogHeader();
        if (lastVisited.isBefore(header.getLastUpdated())) {
            List<DadosPost> postList = blog.getPosts(header);
            int resumoCount = 0;
            for (DadosPost post : postList) {
                ++resumoCount;
                Resumo r = new Resumo(post.getTitle(), post.getPublishedDate());
                r.setId(post.getId());
                r.setUpdated(post.getUpdatedDate());
                r.setUrl(post.getUrl());
                try {
                    ResumoContentParser parser = new ResumoContentParser(post.getContent());
                    String msg = parser.getMensagem();
                    if (!msg.equals("\n")) {
                        r.setMensagemCurta(parser.getMensagemCurta());
                        r.setMensagem(msg);
                        r.setImagemUrl(parser.getUrlImagem());
                        Collection<String> referencias = parser.getReferencia();
                        for (String referencia : referencias) {
                            r.addReferencia(referencia);
                        }
                        Collection<String> autores = parser.getAutor();
                        for (String autor : autores) {
                            r.addAutor(autor);
                        }                        
                        Collection<String> revisores = parser.getRevisor();
                        for (String revisor : revisores) {
                            r.addRevisor(revisor);
                        }
                        r.setLabels(post.getLabels());
                        addResumo(r);
                    }
                } catch (RuntimeException exc) {
                    System.err.printf("%s ao processar resumo %d\n", exc.getMessage(), resumoCount);
                    Files.write(Files.createFile(Paths.get("Resumo" + resumoCount)), post.getContent().getBytes());
                }
            }
        }
        save();
    }
    
    private void addResumo(Resumo r) {
        store.add(r);
    }
    
    public List<Resumo> getResumos() {
        return Collections.unmodifiableList(store);
    }
    
    public List<Resumo> getResumosByTitle(String search) throws IOException {
        if (search.isEmpty()) {
            return getResumos();
        }
        List<Resumo> result = new ArrayList<>();
        for (Resumo post : store) {
            if (post.getTitulo().toLowerCase().contains(search.toLowerCase())) {
                result.add(post);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    public List<Resumo> getResumosByLabel(String search) throws IOException {
        if (search.isEmpty()) {
            return getResumos();
        }
        List<Resumo> result = new ArrayList<>();
        for (Resumo post : store) {
            String[] labels = post.getLabels();
            for (String label : labels) {
                if (label.toLowerCase().contains(search.toLowerCase())) {
                    if (!result.contains(post)) {
                        result.add(post);
                    }
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    public LocalDateTime getLastVisited() {
        return lastVisited;
    }
    
    private void save() throws IOException {
        OutputStream out;
        Path pNew = Paths.get(filename);
        Path pOld = Paths.get("risadinha.bak");
        if (Files.exists(pNew)) {
            Files.copy(pNew, pOld, StandardCopyOption.REPLACE_EXISTING);
            out = Files.newOutputStream(pNew, StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            out = Files.newOutputStream(pNew, StandardOpenOption.CREATE_NEW);
        }
        lastVisited = LocalDateTime.now();
        new ObjectOutputStream(out).writeObject(this);
    }
    
    private void restore() throws IOException, ClassNotFoundException {
        ResumoStore obj = (ResumoStore) new ObjectInputStream(Files.newInputStream(Paths.get(filename))).readObject();
        store = obj.store;
        lastVisited = obj.lastVisited;
    }
}
