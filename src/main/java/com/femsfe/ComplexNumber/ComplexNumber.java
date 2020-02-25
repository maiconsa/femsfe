package com.femsfe.ComplexNumber;

public class ComplexNumber {
	private float real = Float.NaN;
	
	private float img = Float.NaN;
	
	public ComplexNumber() {
		// TODO Auto-generated constructor stub
	}
	
	public ComplexNumber(float real,float img) {
		this.real = real;
		this.img = img;
	}

	public float getReal() {
		return real;
	}

	public void setReal(float real) {
		this.real = real;
	}

	public float getImg() {
		return img;
	}

	public void setImg(float img) {
		this.img = img;
	}
	
	public float abs()   { return (float) Math.hypot(getReal(), getImg()); } 
    public float phase() { return (float) Math.atan2(getImg(), getReal()); } 
	
	public ComplexNumber plus(ComplexNumber b){
		ComplexNumber a = this;
		return new ComplexNumber(a.getReal() + b.getReal(), a.getImg()+ b.getImg());
	}
	public ComplexNumber minus(ComplexNumber b){
		ComplexNumber a = this;
		return new ComplexNumber(a.getReal() - b.getReal(), a.getImg()- b.getImg());
	}
	
	public static String[] getComplexComponents(String string){
		String[] strs =  new String[2];
		if(string.contains("j")){
			if(string.startsWith("j")){
				strs[0] = "0.0";
				strs[1] = string.replace("j", "");
				return strs;
			}
			if(string.startsWith("-j")){
				strs[0] = "0.0";
				strs[1] = string.replace("j", "").trim();
				return strs;
			}
			if(string.contains("+")){
				int plusIndex = string.lastIndexOf("+");
				String real = string.substring(0, plusIndex);
				String img = string.substring(plusIndex+1, string.length() );
				strs = new String[2] ;
				strs[0] = real;
				strs[1] = img.replace("j", "");
				return strs;
				
			}else{
				int plusIndex = string.lastIndexOf("-");
				String real = string.substring(0, plusIndex);
				String img = string.substring(plusIndex, string.length() );
				strs = new String[2] ;
				strs[0] = real;
				strs[1] = img.replace("j", "").trim();
				return strs;
			}
		}else{
			strs = new String[2] ;
			strs[0] = string;
			strs[1] = "0.0";
			return strs;
		}
	}
	
	public ComplexNumber times(ComplexNumber b){
		float real = this.getReal() * b.getReal() - this.getImg() * b.getImg();
		float imag = this.getReal() * b.getImg() + this.getImg() * b.getReal();
        return new ComplexNumber(real, imag);
	}
	 public ComplexNumber times(double alpha) {
	        return new ComplexNumber((float)alpha * this.getReal(), (float)alpha * this.getImg());
	 }
	 public ComplexNumber conjugate(){
		 return new ComplexNumber(this.getReal(), (-1)*this.getImg());
	 }
	 
	 public ComplexNumber divide(double d){
		 return new ComplexNumber(this.getReal()/(float)d, this.getImg()/(float)d);
	 }
	 public ComplexNumber divide(ComplexNumber b){
		 float alpha = b.getReal()*b.getReal() + b.getImg()*b.getImg();
		 ComplexNumber numerador = this.times(b.conjugate());
		 return numerador.divide(alpha);
	 }
	
	@Override
	public String toString() {
		 if (img == 0 || Double.isNaN(img)) return real + "";
	     if (real == 0 || Double.isNaN(real) ) return img + "j";
	     if (img <  0) return real + " - " + (-img) + "j";
	      return real + " + " + img + "j";
	}



}
