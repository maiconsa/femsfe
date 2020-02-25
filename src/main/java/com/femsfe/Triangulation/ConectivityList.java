package com.femsfe.Triangulation;

import java.util.ArrayList;
import java.util.List;

import com.femsfe.Geometries.Point2D;
import com.femsfe.Geometries.Triangle2D;

/**
 * A concrete class of a conectivity list used for domain discretization of a
 * model 2d .
 *
 * @author Maicon Alc�ntara
 * @since 1.0
 *
 */
public class ConectivityList {

    /**
     * Nodes of the dicretization
	 *
     */
    List<Point2D> nodes;
    /**
     * Element (triangular) of domain discretization
	 *
     */
    List<Triangle2D> elements;

    private int contourNumber = 5;

    public int getContourNumber() {
        return contourNumber;
    }

    public void setContourNumber(int contourNumber) {
        this.contourNumber = contourNumber;
    }

    private boolean isMeshQualityVisible = false;
    private boolean isMeshVisible = true;
    private boolean isIsolinesVisible = false;
    private boolean isColorMapVisible = false;
    private boolean isArrowsVisible = false;

    private boolean simulationMade = false;

    public void setSimulationStatus(boolean status) {
        this.simulationMade = status;
    }

    public boolean simulationMade() {
        return this.simulationMade;
    }

    public boolean isIsolinesVisible() {
        return isIsolinesVisible;
    }

    public void setIsolinesVisible(boolean isIsolinesVisible) {
        this.isIsolinesVisible = isIsolinesVisible;
    }

    public boolean isColorMapVisible() {
        return isColorMapVisible;
    }

    public void setColorMapVisible(boolean isColorMapVisible) {
        this.isColorMapVisible = isColorMapVisible;
    }

    public boolean isArrowsVisible() {
        return isArrowsVisible;
    }

    public void setArrowsVisible(boolean isArrowsVisible) {
        this.isArrowsVisible = isArrowsVisible;
    }

    public boolean isMeshQualityVisible() {
        return isMeshQualityVisible;
    }

    public void setMeshQualityVisible(boolean isMeshQualityVisible) {
        this.isMeshQualityVisible = isMeshQualityVisible;
    }

    public boolean isMeshVisible() {
        return isMeshVisible;
    }

    public void setMeshVisible(boolean isMeshVisible) {
        this.isMeshVisible = isMeshVisible;
    }

    /**
     * Constructor and initializes the nodes and elements list
     *
     * @since 1.0
	 *
     */
    public ConectivityList() {
        nodes = new ArrayList<>();
        elements = new ArrayList<>();
    }

    /**
     * Add a node to the node list
     *
     * @param node to be inserted
     * @since 1.0
	 *
     */
    public void addNode(Point2D node) {
        if (node.getIndex() == -1) {
            node.setIndex(nodes.size());
            nodes.add(node);
        }
    }

    /**
     * Get the node on the index position
     *
     * @param index position of the node.
     * @return the node object in index indicated.
	 *
     */
    public Point2D getNode(int index) {
        return nodes.get(index);
    }

    /**
     * Get the all the nodes
     *
     * @return node list.
	 *
     */
    public List<Point2D> getNodes() {
        return nodes;
    }

    /**
     * Return the size of the node list return integer number.
	 *
     */
    public int nodeSize() {
        return nodes.size();
    }

    /**
     * Add an element to the element list
     *
     * @param element to be inserted
     * @since 1.0
	 *
     */
    public void addElement(Triangle2D element) {
        elements.add(element);
    }

    /**
     * Remove an element to the element list
     *
     * @param element to be removed
     * @since 1.0
	 *
     */
    public void removeElement(Triangle2D element) {
        elements.remove(element);
    }

    /**
     * Get all the elements.
     *
     * @return element list. 
	 *
     */
    public List<Triangle2D> getElements() {
        return elements;
    }

    /**
     * Return the size of the element list return integer number.
	 *
     */
    public int elementsSize() {
        return elements.size();
    }

    /**
     * Clear the node and element list.
	 *
     */
    public void clear() {
        nodes.clear();
        elements.clear();
    }

    @Override
    public String toString() {
        String str = "\ne\t|\tn(1,e)\t|\tn(2,e)\t|\tn(3,e)\t";
        int index = 0;
        for (Triangle2D triangle2d : elements) {
            str += "\n" + index + "\t|\t" + triangle2d.p0.getIndex() + "\t|\t" + triangle2d.p1.getIndex() + "\t|\t" + triangle2d.p2.getIndex() + " ";
            index++;
        }

        return str;
    }

}
