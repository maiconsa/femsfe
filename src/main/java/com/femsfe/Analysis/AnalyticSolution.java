package com.femsfe.Analysis;

public class AnalyticSolution {

    private static double PI = Math.PI;

    private AnalyticSolution() {
        // TODO Auto-generated constructor stub
    }

    public static double valueOnCoaxialCableMagPotential(double x, double y, double Re, double Ri, double J, double permi) {
        double r = Math.sqrt(x * x + y * y);
        if (r <= Ri) {
            return permi * J * (Ri * Ri - r * r) / 4 + permi * J * Ri * Ri * Math.log(Re / r) / 2;
        } else if (r >= Ri && r <= Re) {
            return permi * J * Ri * Ri * Math.log(Re / r) / 2;
        } else {
            return Double.NaN;
        }

    }

    public static double valueOnConductorEsphereOnEF(double x, double y, double E0, double a, double V0) {
        double r = Math.sqrt(x * x + y * y);
        return V0 - (E0 * x) + E0 * x * Math.pow(a / r, 3);

    }

    public static double valueOnCoaxialCable(double x, double y, double externalRadius, double internalRadius, double V0) {
        double r = Math.sqrt(x * x + y * y);

        double value = -V0 * Math.log(r) / Math.log(externalRadius / internalRadius);
        value += V0 * Math.log(externalRadius) / Math.log(externalRadius / internalRadius);
        return value;
    }

    public static double valueOnCapacitor(double x, double V1, double V2, double w) {
        return (V2 - V1) * x / w + V1;
    }

    public static double valueOnLaplaceSinFunction(double x, double y, double w, double h) {
        double result = 0;
        for (int n = 1; n < 100; n++) {
            double K = n * PI / h;
            double I = Math.sin(K * h) * Math.cos(h);
            I -= K * Math.sin(h) * Math.cos(K * h);
            I /= (K * K - 1);
            double Cn = (2 / (h * Math.sinh(K * w))) * I;
            result += Cn * Math.sinh(K * x) * Math.sin(K * y);
        }
        return result;
    }

    public static double valueOnLaplaceCosFunction(double x, double y, double w, double h) {
        double result = 0;
        for (int n = 1; n < 100; n++) {
            double K = n * PI / h;
            double I = Math.sin(K * h) * Math.sin(h);
            I += K * Math.cos(h) * Math.cos(K * h);
            I /= (1 - K * K);

            I = I - K / (1 - K * K);

            double Cn = (2 / (h * Math.sinh(K * w))) * I;
            result += Cn * Math.sinh(K * x) * Math.sin(K * y);
        }
        return result;
    }

    public static double valueOnLaplacePlate(double x, double y, double V0, double w, double h) {
        //Refrence :https://www.math.ubc.ca/~peirce/M257_316_2012_Lecture_24.pdf
        // Placa com potencial do lado direito
        double result = 0;
        for (int n = 1; n < 50; n += 2) {
            double K = n * PI / h;
            double I = (V0 / K) * (1 - Math.cos(K * h));
            double Cn = (2 / (h * Math.sinh(K * w))) * I;
            result += Cn * Math.sinh(K * x) * Math.sin(K * y);
        }

        return result;
    }

    public static double valueOnChargedDisk(double x, double y, double R, double p, double permi) {
        double r = Math.sqrt(x * x + y * y);
        return (p / (4 * permi)) * (R * R - r * r);
    }

    public static void main(String[] args) {

    }
}
