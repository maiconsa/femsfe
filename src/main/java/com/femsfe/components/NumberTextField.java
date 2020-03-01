package com.femsfe.components;


import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

	public NumberTextField() {
	
	}
	public NumberTextField(float value){
		setText(String.valueOf(value));
	}
	public NumberTextField(double value){
		setText(String.valueOf(value));
	}
	public float getFloatValue(){
		if(isEmpty()) return Float.NaN;
		return Float.parseFloat(this.getText());
	}
	public double getDoubleValue(){
		if(isEmpty()) return Double.NaN;
		return  Double.parseDouble(this.getText());
	}
	
	public boolean isEmpty(){
		return getText().isEmpty();
	}
	

	 @Override 
	 public void replaceText(int start, int end, String text) {
	        // If the replaced text would end up being invalid, then simply
	        // ignore this call!
	        if (!text.matches("[a-z^i]")) {
	            super.replaceText(start, end, text);
	        }
	    }
	    @Override 
	 public void replaceSelection(String text) {
	        if (!text.matches("[a-z^i]")) {
	            super.replaceSelection(text);
	        }
	    }

}
