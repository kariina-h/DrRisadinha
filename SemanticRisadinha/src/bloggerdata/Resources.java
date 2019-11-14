/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */

package bloggerdata;

/**
 * Mapeia o elemento Resources de 
 * objetos JSON retornados pela API do blogger.
 * 
 * @author ricarte at ft.unicamp.br
 */
class Resources {
    private final int totalItems;
    private final String selfLink;

    public Resources(int totalItems, String selfLink) {
        this.totalItems = totalItems;
        this.selfLink = selfLink;
    }

    public int getTotalItems() {
        return totalItems;
    }
    
    @Override
    public String toString() {
        return totalItems + " items at " + selfLink;
    }
}
