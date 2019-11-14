/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */
package risadinha;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Extração dos elementos específicos do blog 
 * Dr Risadinha a partir do 'content' não-estruturado de
 * uma postagem. 
 * 
 * @author ricarte at ft.unicamp.br
 */
public class ResumoContentParser {

    private final String linhaImagem;
    private final String linhaMensagemCurta;
    private final String linhaMensagem;
    private final String linhaReferencia;
    private final String linhaAutor;
    private final String linhaRevisores;

    public ResumoContentParser(String html) {
        linhaImagem = extrairUrlImagem(html);
        linhaMensagemCurta = extrairMensagemCurta(html);
        linhaMensagem = extrairMensagem(html);
        linhaReferencia = extrairReferencia(html);
        linhaAutor = extrairAutor(html);
        linhaRevisores = extrairRevisor(html);
    }

    public String cleanTags(String source) {
        StringBuilder result = new StringBuilder();
        int pos = 0;
        while (pos < source.length()) {
            if (source.charAt(pos) == '<') {
                while (source.charAt(pos) != '>') {
                    ++pos;
                }
            }
            result.append(source.charAt(pos++));
        }
        return result.toString().replace(">", "").replace("&nbsp;", " ").
                replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").
                replace("\n\n", "\n");
    }

    public String getUrlImagem() {
        return linhaImagem;
    }

    public String getMensagemCurta() {
        return linhaMensagemCurta;
    }

    public String getMensagem() {
        return linhaMensagem;
    }

    public Collection<String> getReferencia() {
        return getMultipleNames(linhaReferencia, "\n");
    }

    public Collection<String> getAutor() {
        return getMultipleNames(linhaAutor, ",\n");
    }

    public Collection<String> getRevisor() {
        return getMultipleNames(linhaRevisores, ",\n");
    }

    private Collection<String> getMultipleNames(String source, String sep) {
        StringTokenizer linha = new StringTokenizer(source, sep);
        Collection<String> result = new ArrayList<>();
        while (linha.hasMoreTokens()) {
            result.add(linha.nextToken().trim());
        }
        return Collections.unmodifiableCollection(result);
    }

    private String extrairUrlImagem(String source) {
        int pos1 = source.indexOf("href") + 6;
        if (pos1 == -1) return "";
        int pos2 = source.indexOf('"', pos1);
        Log.LOG_RIS.log(Level.FINE, "Extr url {0},{1}", new Object[]{pos1, pos2});
        return source.substring(pos1, pos2);
    }

    private String extrairMensagemCurta(String source) {
        String cleanSource = cleanTags(source);
        int posFinal = cleanSource.indexOf("***");
        Log.LOG_RIS.log(Level.FINE, "Extr msg crt {0}", new Object[]{posFinal});
        if (posFinal > 0) {
            return cleanSource.substring(0, posFinal).replace("\n", " ").trim();
        } else {
            return "\n";
        }
    }

    private String extrairMensagem(String source) {
        String cleanSource = cleanTags(source);
        int posInicial = cleanSource.indexOf("***");
        Log.LOG_RIS.log(Level.FINE, "Extr msg inic {0}", new Object[]{posInicial});
        if (posInicial == -1) {
            return "\n";
        }
        posInicial += 3;
        int posFinal = cleanSource.indexOf("Refer", posInicial);
        Log.LOG_RIS.log(Level.FINE, "Extr msg fin {0}", new Object[]{posFinal});
        if (posFinal == -1) {
            posFinal = cleanSource.indexOf("Para ter mai", posInicial);
            Log.LOG_RIS.log(Level.FINE, "Extr msg fin* {0}", new Object[]{posFinal});
        }
        if (posFinal > 0) {
            return cleanSource.substring(posInicial, posFinal).trim();
        } else {
            return "\n";
        }
    }

    private String extrairReferencia(String source) {
        int posInicial = source.indexOf("<b>Refer");
        Log.LOG_RIS.log(Level.FINE, "Extr ref inic {0}", new Object[]{posInicial});
        if (posInicial == -1) {
            return "\n";
        }
        posInicial = source.indexOf("</b>", posInicial) + 4;
        int posFinal = source.indexOf("<b>", posInicial);
        Log.LOG_RIS.log(Level.FINE, "Extr ref fin {0}", new Object[]{posFinal});
        if (posFinal == -1) {
            posFinal = source.indexOf("<a ", posInicial);
        Log.LOG_RIS.log(Level.FINE, "Extr ref fin* {0}", new Object[]{posFinal});
        }
        return cleanTags(source.substring(posInicial, posFinal)).trim();
    }

    private String extrairAutor(String source) {
        int posInicial = source.indexOf("<b>Autor");
        Log.LOG_RIS.log(Level.FINE, "Extr aut inic {0}", new Object[]{posInicial});
        if (posInicial == -1) {
            return "\n";
        }
        posInicial = source.indexOf("</b>", posInicial) + 4;
        int posFinal = source.indexOf("\n", posInicial);
        Log.LOG_RIS.log(Level.FINE, "Extr aut fin {0}", new Object[]{posFinal});
        if (posFinal == -1) {
            posFinal = source.length();
            Log.LOG_RIS.log(Level.FINE, "Extr aut fin* {0}", new Object[]{posFinal});
        }
        return cleanTags(source.substring(posInicial, posFinal)).trim();
    }

    private String extrairRevisor(String source) {
        int posInicial = source.indexOf("<b>Revisor");
        Log.LOG_RIS.log(Level.FINE, "Extr rev inic {0}", new Object[]{posInicial});
        if (posInicial == -1) {
            return "\n";
        }
        posInicial = source.indexOf("</b>", posInicial) + 4;
        int posFinal = source.indexOf("\n", posInicial);
        Log.LOG_RIS.log(Level.FINE, "Extr rev fin {0}", new Object[]{posFinal});
        return cleanTags(source.substring(posInicial, posFinal)).trim();
    }
}
