
package org.leibnizcenter.cfg.earleyparser.event;


import org.leibnizcenter.cfg.category.Category;
import org.leibnizcenter.cfg.earleyparser.parse.Chart;
import org.leibnizcenter.cfg.earleyparser.parse.Edge;

/**
 * An event generated when an {@link Edge edge} is added to a
 * {@link Chart chart} in {@link EarleyParser#parse(Iterable, Category)
 * Earley parsing}.
 *
 * @see Chart
 * @see EarleyParser
 * @see ParserListener
 */
public class EdgeEvent extends ParserEvent {
    public final Edge edge;

    /**
     * Creates a new edge event generated by the specified parser for the
     * given edge at the specified index.
     *
     * @param edge  The edge whose addition generated this event.
     * @param index The string index where the event was generated.
     * @see ParseEvent(EarleyParser, int)
     */
    public EdgeEvent(int index, Edge edge) {
        super(index);
        this.edge = edge;
    }

    /**
     * Gets the edge whose addition generated this event.
     *
     * @return The edge specified when this event was created.
     */
    public Edge getEdge() {
        return edge;
    }
}