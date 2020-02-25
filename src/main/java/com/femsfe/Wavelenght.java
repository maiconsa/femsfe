package com.femsfe;

import com.femsfe.enums.Units;

public class Wavelenght {
	private float value = Float.NaN;
	private Units unit = null;
	public Wavelenght(float value,Units unit) {
		this.value = value;
		this.unit = unit;
	}
	public void setValue(float value){
		this.value = value;
	}
	public float getValue(){
		return value;
	}
	
	public void setUnit(Units unit){
		this.unit = unit;
	}
	public Units getUnit(){
		return unit;
	}
	public double getWavelenght(){
		if(unit != null){
			return value*unit.getFactor();
		}else{
			return Float.NaN;
		}
	}
	public double getK0(){
		return 2*Math.PI/getWavelenght();
	}

}
