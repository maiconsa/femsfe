package com.femsfe.Geometrias;

public enum Units {
	METER(1.0E0,"m"),CENT(1.0E-2,"cm"),MILI(1.0E-3,"mm"),MICRO(1.0E-6,"\u00B5m"),NANO(1.0E-9,"nm");
	double factor;
	String notation;
	private Units(double factor,String notation) {
		this.factor = factor;
		this.notation = notation;
	}
	public double getFactor(){
		return factor;
	} 
	public String getNotation(){
		return notation;
	}
}
