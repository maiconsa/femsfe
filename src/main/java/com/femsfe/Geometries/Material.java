package com.femsfe.Geometries;


import com.femsfe.ComplexNumber.ComplexNumber;

import com.femsfe.PML.PMLRegions;
import java.io.Serializable;

public class Material implements Serializable{
	/**	Vacuum Permittivity  F/m */
	public final static float vacuumPermittivity =  (float) 8.854187817620E-12;
	
	/**	Vacuum Permittivity N/A² */
	public final static float vacuumPermeability =   (float) ((4E-7)*Math.PI);
	
	
	
	private float color[];
	
	private PMLRegions pmlRegion = null;
	
	private String name;
	
	/* ELETRIC PROPERTIES	*/
	private ComplexNumber relativePermittivity;
	private ComplexNumber relativePermeability;
	
	public Material(String name,ComplexNumber relativePermittivity,ComplexNumber relativePermeability ) {
		this.name = name;
		this.relativePermittivity = relativePermittivity;
		this.relativePermeability = relativePermeability;
		
	}
	public void setColor(float color[]){
		this.color = color;
	}
	public float[] getColor(){
		return color;
	}
	public Material(String name) {
		this.name = name;
	}
	public Material(){}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public ComplexNumber getRelativePermittivity() {
		return relativePermittivity;
	}
	public void setRelativePermittivity(ComplexNumber relativePermittivity) {
		this.relativePermittivity = relativePermittivity;
	}
	public ComplexNumber getRelativePermeability() {
		return (ComplexNumber) relativePermeability;
	}
	public void setRelativePermeability(ComplexNumber relativePermeability) {
		this.relativePermeability = (ComplexNumber) relativePermeability;
	}
	public ComplexNumber getRefractiveIndex(){
		float real = (float) Math.sqrt((relativePermittivity.abs() + relativePermittivity.getReal())/2); 
		float img = (float) Math.sqrt((relativePermittivity.abs() - relativePermittivity.getReal())/2); 
		return new ComplexNumber(real, img);
	}
	
	public void setPMLRegion(PMLRegions pmlRegion){
		this.pmlRegion = pmlRegion;
	}
	public PMLRegions getPMLRegion(){
		return this.pmlRegion;
	}
	
	public boolean isPML(){
		return pmlRegion != null;
	}
	
	
	
	

}
