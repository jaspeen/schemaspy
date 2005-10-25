package net.sourceforge.schemaspy.view;

import java.util.*;
import net.sourceforge.schemaspy.model.*;
import net.sourceforge.schemaspy.util.*;

/**
 * Represents GraphVis dot's concept of an edge.  That is, a connector between two nodes.
 *
 * @author John Currier
 */
public class DotConnector implements Comparable {
    private TableColumn parentColumn;
    private Table parentTable;
    private TableColumn childColumn;
    private Table childTable;
    private boolean implied;
    private boolean bottomJustify;
    private String parentPort;
    private String childPort;

    /**
     * Create an edge that logically connects a child column to a parent column.
     *
     * @param parentColumn TableColumn
     * @param childColumn TableColumn
     * @param implied boolean
     */
    public DotConnector(TableColumn parentColumn, TableColumn childColumn, boolean implied) {
        this.parentColumn = parentColumn;
        this.childColumn = childColumn;
        this.implied = implied;
        this.parentPort = parentColumn.getName();
        this.parentTable = parentColumn.getTable();
        this.childPort = childColumn.getName();
        this.childTable = childColumn.getTable();
        this.bottomJustify = !Dot.getInstance().supportsCenteredEastWestEdges();
    }

    /**
     * Returns true if this edge logically "points to" the specified table
     *
     * @param possibleParentTable Table
     * @return boolean
     */
    public boolean pointsTo(Table possibleParentTable) {
        return possibleParentTable.equals(parentTable);
    }

    /**
     * By default a parent edge connects to the column name...this lets you
     * connect it the parent's type column instead (e.g. for detailed parents)
     *
     * Yes, I need to find a more appropriate name/metaphor for this method....
     */
    public void connectToParentDetails() {
        parentPort = parentColumn.getName() + ".type";
    }

    public void connectToParentTitle() {
        parentPort = parentColumn.getTable().getName() + ".heading";
    }

    public void connectToChildTitle() {
        childPort = childColumn.getTable().getName() + ".heading";
    }

    public String toString() {
        StringBuffer edge = new StringBuffer();
        edge.append("  \"");
        if (childTable != null) {
            edge.append(childTable.getName());
            edge.append("\":\"");
            edge.append(childPort);
        }
        edge.append("\":");
        if (bottomJustify)
            edge.append("s");
        edge.append("w -> \"");
        if (parentTable != null) {
            edge.append(parentTable.getName());
            edge.append("\":\"");
            edge.append(parentPort);
        }
        edge.append("\":");
        if (bottomJustify)
            edge.append("s");
        edge.append("e ");

        edge.append("[arrowtail=");
        if (childPort.endsWith(".heading")) {
            edge.append("none");
        } else {
            if (!childColumn.isUnique())
                edge.append("crow");
            if (childColumn.isNullable())
                edge.append("odot");
            else
                edge.append("tee");
        }
        edge.append(" arrowhead=none");
        if (implied)
            edge.append(" style=dashed");
        edge.append("];");
        return edge.toString();
    }

    public int compareTo(Object o) {
        DotConnector other = (DotConnector)o;
        int rc = childTable == null ? -1 : (other.childTable == null ? 1 : 0);
        if (rc == 0)
            rc = childTable.getName().compareTo(other.childTable.getName());
        if (rc == 0)
            rc = childColumn.getName().compareTo(other.childColumn.getName());
        if (rc == 0)
            rc = parentTable == null ? -1 : (other.parentTable == null ? 1 : 0);
        if (rc == 0)
            rc = parentTable.getName().compareTo(other.parentTable.getName());
        if (rc == 0)
            rc = parentColumn.getName().compareTo(other.parentColumn.getName());
        if (rc == 0 && implied != other.implied)
            rc = implied ? 1 : -1;
//        if (rc == 0)
//            rc = parentPort.compareTo(other.parentPort);
//        if (rc == 0)
//            rc = childPort.compareTo(other.childPort);

        return rc;
    }

    public boolean equals(Object other) {
        if (!(other instanceof DotConnector))
            return false;
        return compareTo(other) == 0;
    }

    public int hashCode() {
        int p = parentTable == null ? 0 : parentTable.getName().hashCode();
        int c = childTable == null ? 0 : childTable.getName().hashCode();
        return (p << 16) & c;
    }

    /**
     * stubMissingTables
     *
     * @param tablesWritten Set
     */
    public void stubMissingTables(Set tables) {
        if (!tables.contains(parentTable))
            parentTable = null;
        if (!tables.contains(childTable))
            childTable = null;
    }

    public Table getParentTable() {
        return parentTable;
    }

    public Table getChildTable() {
        return childTable;
    }
}
