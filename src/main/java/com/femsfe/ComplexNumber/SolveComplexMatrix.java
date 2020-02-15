package com.femsfe.ComplexNumber;


import org.jblas.ComplexFloat;
import org.jblas.ComplexFloatMatrix;

import org.jblas.FloatMatrix;
import org.jblas.Solve;


public class SolveComplexMatrix {

	public SolveComplexMatrix() {
		// TODO Auto-generated constructor stub
	}
	
	public static ComplexFloatMatrix solverJBLAS(ComplexNumber A[][] ,ComplexNumber B[]) {	
		int length = B.length*2;
		FloatMatrix AA = new FloatMatrix(length, length);
		FloatMatrix BB = new FloatMatrix(length);
		
		for (int i = 0; i < A.length; i++) {
			BB.put(i, B[i].getReal());
			BB.put(i+ B.length, B[i].getImg());
			for (int j = 0; j < A.length; j++) {
				
				AA.put(i, j,A[i][j].getReal());
				AA.put(i+ A.length, j + A.length,A[i][j].getReal());
				
				AA.put(i, j+A.length,-A[i][j].getImg());
				AA.put(i+ A.length, j,A[i][j].getImg());
			}
		}
		FloatMatrix result = Solve.solve(AA, BB);
		ComplexFloatMatrix complexResult= new ComplexFloatMatrix(A.length);
		for (int i = 0; i < A.length; i++) {
			complexResult.put(i, new ComplexFloat(result.get(i),result.get(i+A.length)));
			}
		return complexResult ;
	}
	
	public static ComplexNumber[] gaussian(ComplexNumber A[][] ,ComplexNumber B[]){
		int n = A.length;
		for (int k = 0; k < n - 1; k++) {
			for (int i = k+1; i < n; i++) {
				ComplexNumber fator = A[i][k].divide(A[k][k]);
				for (int j = k+1; j < n; j++) {
					A[i][j] = A[i][j].minus(A[k][j].times(fator));
				}
				B[i] = B[i].minus(B[k].times(fator));
			}
		}
		
		ComplexNumber X[] = new ComplexNumber[n];
		for (int i = n-1; i >= 0; i--) {
			ComplexNumber  soma = new ComplexNumber(0, 0);
			for (int j = i+1; j < n; j++) {
				soma = soma.plus(A[i][j].times(X[j]));
			}
			X[i] = B[i].minus(soma).divide(A[i][i]);
		}
		return X;
		
	} 

}
