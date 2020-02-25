package com.femsfe.Geometries;


import com.femsfe.enums.GeometryType;
import com.femsfe.enums.Units;
import com.femsfe.SelectGeometry;
import java.io.Serializable;


/**
 * The <code>Geometry2D</code> class describes a geometry in two dimensional space<p>
 * This class is only the abstract superclass for all subclass.
 *
 * @author     Maicon Alc�ntara 
 * @since 1.0
 */
public abstract class Geometry2D implements Serializable{
	/**
	 * Represent the Units for all geomatry
	 * */
	private static Units unit = Units.METER;
	/**
	 * ID  of this <code>Geometry2D<code>.
	 * */
	private int id = -1;
	/**
	 * Check status of this <code>Geometry2D<code>.
	 * */
	private boolean selected = false;
	/**
	 * Visibility status of this <code>Geometry2D<code>.
	 */
	private boolean visibility = true;
	/**
	 * Represent a error epsilon used for selection operation.
	 * */
	public static double EPSILON = 0.01;

	
	private boolean isChild = false; 
	
	/**
	 * The geometry type of subclass.
	 * */
	protected GeometryType type;
	
	/**
	 * Set the ID of <code>Geometry</code>.
	 * */
	public void setID(int id){
		this.id = id;
	}
	/**
	 * Return the ID  .
	 * @return 
	 * @return int number that represent the ID.
	 * */
	public int getID(){
		return id;
	}
	
	/**
	 * Return the geometry type.
	 * @return geometry type.
	 * */
	public GeometryType getType() {
		return type;
	}
	/**
	 * Return the select status of this <code>Geometry2D</code>.
	 * @return if this <code>Geometry2D</code>  is selected then return <code>true</code>. Otherwise, return <code>false</code>.
	 * */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * Set the selected status of this <code>Geometry2D</code>.
	 * @param status boolean status of selection.
	 * */
	public void setSelected(boolean status) {
		this.selected = status;
	}
	/**
	 * Return the visibility status of this <code>Geometry2D</code>.
	 * @return if this <code>Geometry2D</code>  is visible then return <code>true</code>. Otherwise, return <code>false</code>.
	 * */
	public boolean isVisible() {
		return visibility;
	}
	/**
	 * Set the visibility of this <code>Geometry2D</code>.
	 * @param status boolean status of visilibity.
	 * */
	public void setVisible(boolean status) {
		this.visibility = status;
	}
	
	/**
	 * Select this <code>Geometry2D</code>. if is not selected then is inserted into a list.
	 */
	public void selectThis() {
		if(!isSelected()){
			if(SelectGeometry.started()){
				SelectGeometry.selectGeometry(this);
			}else{
				SelectGeometry.begin(getType());
					SelectGeometry.selectGeometry(this);
				SelectGeometry.end();
			}	
			setSelected(true);
		}
		
	}
	/**
	 * Deselect this <code>Geometry2D</code>. Remove this from the selection list.
	 */
	public void deselectThis() {
		if(SelectGeometry.started()){
			SelectGeometry.deselectGeometry(this);
		}else{
			SelectGeometry.begin(getType());
				SelectGeometry.deselectGeometry(this);
			SelectGeometry.end();
		}	
		setSelected(false);
	}
	
	public static Units getUnit(){
		return unit;
	}
	public static void setUnit(Units unit){
		Geometry2D.unit = unit;
	}
	
	public void setIsChild(boolean isChild){
		this.isChild = isChild;
	}
	public boolean isChild(){
		return isChild;
	}
	
	
	/**
	 * An abstract method that all subclass must implement.This method return a copy the subclass.
	 * @return a copy o this <code>Geometry2D</code>
	 * */
	public abstract Geometry2D copy();	
	/**
	 * An abstract method that all subclass must implement.This method reset the geometry to default.
	 * */
	public abstract void reset();
	
	
	public abstract boolean isNear(double x,double y);
	public abstract boolean isNear(double x,double y,double epsilon);
	public abstract boolean isNear(Point2D point2d);
	public abstract boolean isNear(Point2D point2d,double epsilon);
	public abstract double getMaxCoordinate();

	
}
