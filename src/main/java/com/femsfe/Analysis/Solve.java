package com.femsfe.Analysis;

public class Solve {

	public Solve() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static double[] gaussian(double A[][] ,double B[]){
		int n = A.length;
		for (int k = 0; k < n - 1; k++) {
			for (int i = k+1; i < n; i++) {
				double fator = A[i][k]/A[k][k];
				for (int j = k+1; j < n; j++) {
					A[i][j] = A[i][j] - fator*A[k][j];
				}
				B[i] -= fator*B[k];
			}
		}
		
		
		double X[] = new double[n];
		for (int i = n-1; i >= 0; i--) {
			double  soma = 0;
			for (int j = i+1; j < n; j++) {
				soma+=A[i][j]*X[j];
			}
			X[i] = (B[i]- soma)/A[i][i];
		}
		return X;
		
	} 
	public static void main(String[] args) {
		double A[][] = {{3 ,-0.1, -0.2},
						{0.1 ,7, -0.3},
						{0.3,-0.2,10}};
		double B[] = {7.85,-19.3,71.4};
		
		double X[] = Solve.gaussian(A, B);
		for (int i = 0; i < X.length; i++) {
			System.out.println(X[i]);
		}
		
	}
	

}
