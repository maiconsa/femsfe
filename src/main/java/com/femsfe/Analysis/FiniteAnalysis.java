package com.femsfe.Analysis;

import com.femsfe.enums.ProblemType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jblas.ComplexFloat;
import org.jblas.ComplexFloatMatrix;

import com.femsfe.BoundaryCondition.value.DirichletConditionValue;
import com.femsfe.BoundaryCondition.value.RobinConditionValue;
import com.femsfe.BoundaryCondition.value.SimpleBCValue;
import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.ComplexNumber.SolveComplexMatrix;
import com.femsfe.Geometries.Border2D;
import com.femsfe.Geometries.Geometry2D;
import com.femsfe.Geometries.Line2D;
import com.femsfe.Geometries.Material;
import com.femsfe.Geometries.Point2D;
import com.femsfe.Geometries.Triangle2D;
import com.femsfe.PML.PML;
import com.femsfe.PML.PMLRegions;
import com.femsfe.Triangulation.ConectivityList;
import com.femsfe.Project;

public class FiniteAnalysis {

    /*	MODEL */
    private ConectivityList mesh;

    /*	PROBLEM TYPE	*/
    private ProblemType problemType;

    /*	GLOBAL MATRIX [K]*/
    private ComplexNumber[][] K;

    /*	ARRAY B*/
    private ComplexNumber B[];

    /*	RESULT */
    private ComplexNumber result[];

    /*	MIN VALUE - REAL	*/
    public double minReal = Double.POSITIVE_INFINITY;

    /* MAX VALUE - REAL	*/
    public double maxReal = Double.NEGATIVE_INFINITY;

    /*	MIN VALUE - IMG	*/
    public double minImg = Double.POSITIVE_INFINITY;

    /* MAX VALUE - IMG	*/
    public double maxImg = Double.NEGATIVE_INFINITY;

    public FiniteAnalysis() {
    }

    public FiniteAnalysis(ConectivityList mesh, ProblemType problemType) {
        this.mesh = mesh;
        this.problemType = problemType;
    }

    public ConectivityList getMesh() {
        return mesh;
    }

    public void setMesh(ConectivityList mesh) {
        this.mesh = mesh;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    /*	INIT ALL VALUES OF [K] AND [B] WITH 0 VALUE */
    public void init() {
        for (int i = 0; i < mesh.nodeSize(); i++) {
            B[i] = new ComplexNumber(0, 0);
            for (int j = 0; j < mesh.nodeSize(); j++) {
                K[i][j] = new ComplexNumber(0, 0);;
            }
        }
    }

    /*	GENERATE THE ELEMENTAL MATRIX [KE]*/
    public ComplexNumber[][] elementalMatrix(Triangle2D element) {
        Material material = element.getMaterial();
        Point2D center = element.centroid();
        ComplexNumber alphax = null, alphay = null, beta = null, permittivity = null, permeability;
        ComplexNumber sx = null, sy = null, refIndex = null;
        float k0;
        switch (problemType) {
            case ELETROSTATIC:
                permittivity = element.getMaterial().getRelativePermittivity();
                alphax = permittivity;
                alphay = permittivity;
                beta = new ComplexNumber(0, 0);
                break;
            case MAGNETOSTATIC_VECTOR_POTENTIAL:
                permeability = element.getMaterial().getRelativePermeability();
                ComplexNumber one = new ComplexNumber(1, 0);
                alphax = one.divide(permeability);
                alphay = one.divide(permeability);
                beta = new ComplexNumber(0, 0);
                break;
            case MAGNETOSTATIC_SCALAR_POTENTIAL:
                permeability = element.getMaterial().getRelativePermeability();
                ComplexNumber aux = new ComplexNumber(1, 0);
                alphax = aux.times(permeability);
                alphay = aux.times(permeability);
                beta = new ComplexNumber(0, 0);
                break;
            case HELMHOLTZ_TE_MODE:
                refIndex = material.getRefractiveIndex();
                k0 = (float) Project.getWavelenght().getK0();
                if (material.isPML()) {
                    PML pml = element.getPML();
                    ComplexNumber[] s = getPMLParameters(pml, center.getX(), center.getY(), k0, refIndex.getReal());
                    sx = s[0];
                    sy = s[1];
                } else {
                    sx = new ComplexNumber(1, 0);
                    sy = new ComplexNumber(1, 0);
                }

                alphax = sx.divide(sy);
                alphay = sy.divide(sx);

                beta = sy.times(sx).times(k0 * k0).times(refIndex.times(refIndex)).times(-1);
                break;
            case HELMHOLTZ_TM_MODE:
                refIndex = material.getRefractiveIndex();
                k0 = (float) Project.getWavelenght().getK0();
                if (material.isPML()) {
                    PML pml = element.getPML();
                    ComplexNumber[] s = getPMLParameters(pml, center.getX(), center.getY(), k0, refIndex.getReal());
                    sx = s[0];
                    sy = s[1];

                } else {
                    sx = new ComplexNumber(1, 0);
                    sy = new ComplexNumber(1, 0);
                }

                alphax = sx.divide(sy);
                alphay = sy.divide(sx);

                alphax = alphax.divide(refIndex.times(refIndex));
                alphay = alphay.divide(refIndex.times(refIndex));

                beta = sy.times(sx).times(k0 * k0).times(-1);
                break;

            default:
                break;
        }
        element.setAlphax(alphax);
        element.setAlphay(alphay);
        element.setBeta(beta);

        Point2D p0 = element.p0;
        Point2D p1 = element.p1;
        Point2D p2 = element.p2;

        double factor = Geometry2D.getUnit().getFactor();
        double be[] = new double[3];
        be[0] = (p1.getY() - p2.getY()) * factor;
        be[1] = (p2.getY() - p0.getY()) * factor;
        be[2] = (p0.getY() - p1.getY()) * factor;

        double ce[] = new double[3];
        ce[0] = (p2.getX() - p1.getX()) * factor;
        ce[1] = (p0.getX() - p2.getX()) * factor;
        ce[2] = (p1.getX() - p0.getX()) * factor;

        double area = (0.5f * (be[0] * ce[1] - be[1] * ce[0]));

        ComplexNumber[][] KE = new ComplexNumber[3][3];
        int delta_ij;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                delta_ij = (i == j ? 1 : 0);
                KE[i][j] = element.getAlphax().times(be[i] * be[j]).plus(element.getAlphay().times(ce[i] * ce[j])).divide(4 * area);
                KE[i][j] = KE[i][j].plus((element.getBeta().times((1 + delta_ij) * (area / 12))));
                //KE[i][j] = (element.getAlphax()*be[i]*be[j] +  element.getAlphay()*ce[i]*ce[j])/(4*area);
                //KE[i][j]+= (element.getBeta()*(1+delta_ij)*area)/12;
            }
        }
        return KE;

    }


    /*	ADD ELEMENTAL MATRIX [KE] TO GLOBAL MATRIX [K] */
    public void addElemental2Global(Triangle2D element) {
        Point2D center = element.centroid();
        ComplexNumber source = new ComplexNumber((float) element.spaceDensityFunction.getValue(center.getX(), center.getY()), 0);

        switch (problemType) {
            case ELETROSTATIC:
                source = source.divide(Material.vacuumPermittivity);
                break;
            case MAGNETOSTATIC_VECTOR_POTENTIAL:
                source = source.times(Material.vacuumPermeability);
                break;
            case MAGNETOSTATIC_SCALAR_POTENTIAL:
                break;
            default:
                break;
        }
        double factor = Geometry2D.getUnit().getFactor();
        Point2D p0 = element.p0, p1 = element.p1, p2 = element.p2;
        double be[] = new double[3];
        be[0] = (p1.getY() - p2.getY()) * factor;
        be[1] = (p2.getY() - p0.getY()) * factor;
        be[2] = (p0.getY() - p1.getY()) * factor;

        double ce[] = new double[3];
        ce[0] = (p2.getX() - p1.getX()) * factor;
        ce[1] = (p0.getX() - p2.getX()) * factor;
        ce[2] = (p1.getX() - p0.getX()) * factor;

        double area = (0.5f * (be[0] * ce[1] - be[1] * ce[0]));
        ComplexNumber[][] KE = elementalMatrix(element);
        Point2D[] nodes = {element.p0, element.p1, element.p2};
        int[] index = {nodes[0].getIndex(), nodes[1].getIndex(), nodes[2].getIndex()};
        double F0x, F0y;
        Point2D centroid = element.centroid();
        double x = centroid.getX(), y = centroid.getY();
        ComplexNumber aux;
        for (int i = 0; i < 3; i++) {
            /* INSERT A VALUE BASED ON AREA,FUNCTION AND PHI VALUE */
            B[index[i]] = B[index[i]].plus(source.times(area / 3));
            if (element.isPermanentFieldRegion()) {
                F0x = element.getPermanentField().getXValue(x, y);
                F0y = element.getPermanentField().getYValue(x, y);
                switch (problemType) {
                    case ELETROSTATIC:
                        aux = new ComplexNumber((float) ((F0x * be[i] + F0y * ce[i]) / 2), 0);
                        B[index[i]] = B[index[i]].minus(aux.divide(Material.vacuumPermittivity));
                        break;
                    case MAGNETOSTATIC_VECTOR_POTENTIAL:
                        aux = new ComplexNumber((float) ((F0y * be[i] - F0x * ce[i]) / 2), 0);
                        B[index[i]] = B[index[i]].minus(aux.divide(element.getAlphax()));
                        break;
                    case MAGNETOSTATIC_SCALAR_POTENTIAL:
                        aux = new ComplexNumber((float) ((F0x * be[i] + F0y * ce[i]) / 2), 0);
                        B[index[i]] = B[index[i]].plus(aux.divide(Material.vacuumPermeability));
                        break;
                    default:
                        break;
                }

            }
            //B[index[i]]+= (element.area()/3)*(element.densityFunction*factor);
            /* LOOP FOR INSERT KE NODE IN GLOBAL MATRIX*/
            for (int j = 0; j < 3; j++) {
                K[index[i]][index[j]] = K[index[i]][index[j]].plus(KE[i][j]);
            }
        }
    }

    /*	IMPOSE DIRICHLET BOUNDARY CONDITION	*/
    public void imposeDirichletCondition(List<DirichletConditionValue> condition) {
        ComplexNumber value = null;
        int index = -1;
        for (DirichletConditionValue dirichlet : condition) {
            index = dirichlet.getPointIndex();
            value = dirichlet.getValue();
            B[index] = value;
            K[index][index] = new ComplexNumber(1, 0);
            for (int j = 0; j < mesh.nodeSize(); j++) {
                if (j == index) {
                    continue;
                }
                B[j] = B[j].minus(K[j][index].times(value));
                K[index][j] = new ComplexNumber(0, 0);
                K[j][index] = new ComplexNumber(0, 0);
            }
        }
    }

    /*	IMPOSE NEUMANN BOUNDARY CONDITION	*/
    public void imposeRobinCondition(List<RobinConditionValue> condition) {
        Point2D p0, p1;
        double lenght;
        ComplexNumber Ks[][] = new ComplexNumber[2][2];
        int[] index = new int[2];
        ConectivityList list = mesh;
        double factor = Geometry2D.getUnit().getFactor();
        for (RobinConditionValue robinConditionValue : condition) {
            index[0] = robinConditionValue.getPointIndex0();
            index[1] = robinConditionValue.getPointIndex1();

            p0 = list.getNode(index[0]);
            p1 = list.getNode(index[1]);
            lenght = p0.distanceTo(p1) * factor;
            Ks[0][0] = robinConditionValue.getyValue().times((float) lenght / 3);
            Ks[0][1] = robinConditionValue.getyValue().times((float) lenght / 6);
            Ks[1][0] = Ks[0][1];
            Ks[1][1] = Ks[0][0];

            // original -> plus
            if (Project.getProblemType() == ProblemType.ELETROSTATIC) {
                B[index[0]] = B[index[0]].plus(robinConditionValue.getqValue().times(lenght / 2).divide(Material.vacuumPermittivity));
                B[index[1]] = B[index[1]].plus(robinConditionValue.getqValue().times(lenght / 2).divide(Material.vacuumPermittivity));
            } else if (Project.getProblemType() == ProblemType.MAGNETOSTATIC_VECTOR_POTENTIAL) {
                B[index[0]] = B[index[0]].plus(robinConditionValue.getqValue().times(lenght / 2).times(Material.vacuumPermeability));
                B[index[1]] = B[index[1]].plus(robinConditionValue.getqValue().times(lenght / 2).times(Material.vacuumPermeability));
            } else {
                B[index[0]] = B[index[0]].plus(robinConditionValue.getqValue().times(lenght / 2));
                B[index[1]] = B[index[1]].plus(robinConditionValue.getqValue().times(lenght / 2));
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (Project.getProblemType() == ProblemType.ELETROSTATIC) {
                        K[index[i]][index[j]] = K[index[i]][index[j]].plus(Ks[i][j].divide(Material.vacuumPermittivity));
                    } else if (Project.getProblemType() == ProblemType.MAGNETOSTATIC_VECTOR_POTENTIAL) {
                        K[index[i]][index[j]] = K[index[i]][index[j]].plus(Ks[i][j].times(Material.vacuumPermeability));
                    } else {
                        K[index[i]][index[j]] = K[index[i]][index[j]].plus(Ks[i][j]);
                    }
                }
            }

        }
    }

    public void imposeSimpleBoundaryCondition(List<SimpleBCValue> condition) {
        Line2D segment = null;
        double C[][] = new double[2][2];
        int[] indexes = new int[2];
        ComplexNumber[] incidentValues = new ComplexNumber[2];
        float k0 = (float) Project.getWavelenght().getK0();
        double factor = Geometry2D.getUnit().getFactor();
        ComplexNumber gama = null;
        for (SimpleBCValue simpleBCValue : condition) {
            segment = new Line2D(mesh.getNode(simpleBCValue.index0), mesh.getNode(simpleBCValue.index1));
            C[0][0] = segment.getLength() * factor / 3;
            C[0][1] = segment.getLength() * factor / 6;
            C[1][0] = C[0][1];
            C[1][1] = C[0][0];
            for (Triangle2D triangle2d : getSharedElement(segment)) {
                //segment = triangle2d.getEdgeEqual(segment);
                simpleBCValue.swapByEdge(segment);
                indexes = simpleBCValue.getIndexes();
                incidentValues = simpleBCValue.getIncidentValues();
                triangle2d.getOpposedVertex(segment);
                if (segment.isVertical()) {
                    gama = new ComplexNumber(0, -k0);
                    gama = gama.times(triangle2d.getAlphax());
                } else {
                    gama = new ComplexNumber(0, -k0);
                    gama = gama.times(triangle2d.getAlphay());
                }

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        K[indexes[i]][indexes[j]] = K[indexes[i]][indexes[j]].minus(gama.times((float) C[i][j]));
                        B[indexes[i]] = B[indexes[i]].plus(gama.times((float) C[i][j]).times(incidentValues[j]));
                    }
                }
            }
        }

    }

    private List<Triangle2D> getSharedElement(Line2D line2d) {
        List<Triangle2D> element0 = line2d.getP0().getLinkedTriangles();
        List<Triangle2D> list = new ArrayList<>(2);
        for (Triangle2D triangle2d : element0) {
            if (triangle2d.hasEdge(line2d)) {
                list.add(triangle2d);
            }
        }
        return list;
    }

    /*	COMPUTE RESULT*/
    public void compute() {
        ConectivityList list = mesh;
        K = new ComplexNumber[list.nodeSize()][list.nodeSize()];
        B = new ComplexNumber[list.nodeSize()];
        init();
        for (Triangle2D element : list.getElements()) {
            addElemental2Global(element);
        }

        imposeRobinCondition(Project.getRobinCondition());
        if (problemType == ProblemType.HELMHOLTZ_TE_MODE || problemType == ProblemType.HELMHOLTZ_TM_MODE) {
            imposeSimpleBoundaryCondition(Project.getSimpleBoundaryCondition());
        }
        imposeDirichletCondition(Project.getDirichletCondition());

        System.out.println("ANALISE INICIADA !!!!!");
        System.nanoTime();

        ComplexFloatMatrix resultJBLAS = SolveComplexMatrix.solverJBLAS(K, B);

        System.nanoTime();
        System.out.println("ANALISE  CONCLUIDA!!!!!");

        ComplexFloat value;

        for (int i = 0; i < B.length; i++) {
            value = resultJBLAS.get(i);

            minReal = Math.min(minReal, value.real());
            maxReal = Math.max(maxReal, value.real());

            minImg = Math.min(minImg, value.imag());
            maxImg = Math.max(maxImg, value.imag());
            list.getNodes().get(i).setValue(new ComplexNumber(value.real(), value.imag()));

        }
        System.out.println("Max:" + maxReal + "," + "Min:" + minReal);
        System.out.println("Max:" + maxImg + "," + "Min:" + minImg);

        ComplexNumber Pinc = new ComplexNumber(0, 0), Pr = new ComplexNumber(0, 0), Psegment;
        List<Point2D> chewPoints;
        Point2D p0, p1;
        float k0 = (float) Project.getWavelenght().getK0();
        float factor = (float) Geometry2D.getUnit().getFactor();
        for (Border2D border2d : Project.getBordersFromFaces()) {
            if (border2d.isInputPort()) {
                chewPoints = border2d.getChewPoints();
                ComplexNumber inc0 = new ComplexNumber(0, 0), inc1 = new ComplexNumber(0, 0);
                float A = 1;
                for (int i = 1; i < chewPoints.size(); i++) {
                    p0 = chewPoints.get(i - 1);
                    p1 = chewPoints.get(i);
                    //A = (float) Math.exp(-Math.pow(p0.getY()/0.7, 2));
                    inc0.setReal((float) (A * Math.cos(k0 * p0.getX() * factor)));
                    inc0.setImg((float) (A * Math.sin(k0 * p0.getX() * factor)));

                    //A = (float) Math.exp(-Math.pow(p1.getY()/0.7, 2));
                    inc1.setReal((float) (A * Math.cos(k0 * p1.getX() * factor)));
                    inc1.setImg((float) (A * Math.sin(k0 * p1.getX() * factor)));

                    Psegment = inc0.times(inc0).plus(inc1.times(inc1)).plus(inc0.times(inc1)).times((float) p0.distanceTo(p1) * factor / 3);

                    Pinc = Pinc.plus(Psegment);

                    ComplexNumber v0 = p0.getValue();
                    ComplexNumber v1 = p1.getValue();

                    Psegment = v0.times(v0).plus(v1.times(v1)).plus(v0.times(v1)).times((float) p0.distanceTo(p1) * factor / 3);

                    Pr = Pr.plus(Psegment);

                }
            }
        }
        System.out.println("Pinc= " + Pinc.getReal());
        System.out.println("Pr = " + Pr.getReal());
        System.out.println("Reflection: " + Pr.getReal() / Pinc.getReal());

    }

    /*	PRIN THE GLOBAL MATRIX*/
    public void printGlobalMatrix() {
        String str = "";
        for (int i = 0; i < K.length; i++) {
            str += Arrays.toString(K[i]) + "\n";
        }
        System.out.println("GLOBAL MATRIX [K]");
        System.out.println(str);
    }

    /*	PRINT THE ARRAY RESULT	*/
    public void printResult() {
        System.out.println("RESULT");
        String str = "";
        for (int i = 0; i < result.length; i++) {
            str += "[" + result[i] + "]";

        }
        System.out.println(str);
    }

    /*	GET RESULT*/
    public ComplexNumber[] getResult() {
        return this.result;
    }

    /*	RESET	*/
    public void clear() {
        result = null;
        minReal = Double.POSITIVE_INFINITY;
        maxReal = Double.NEGATIVE_INFINITY;
        minImg = Double.POSITIVE_INFINITY;
        maxImg = Double.NEGATIVE_INFINITY;
    }

    private ComplexNumber[] getPMLParameters(PML pml, double x, double y, double k0, double refractiveIndex) {
        ComplexNumber sx = null, sy = null;
        PMLRegions region = pml.getRegion();
        double d, img, dBegin, fac;
        switch (region) {
            case TOP_CENTER:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, (float) img);
                sx = new ComplexNumber(1, 0);
                break;
            case BOTTOM_CENTER:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = (dBegin * dBegin) / (d * d);
                img = -fac;
                sy = new ComplexNumber(1, (float) img);
                sx = new ComplexNumber(1, 0);
                break;
            case LEFT_CENTER:
                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, 0);
                sx = new ComplexNumber(1, (float) img);
                break;
            case RIGHT_CENTER:
                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, 0);
                sx = new ComplexNumber(1, (float) img);
                break;
            case LEFT_TOP:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, (float) img);

                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sx = new ComplexNumber(1, (float) img);
                break;
            case RIGHT_TOP:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, (float) img);

                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sx = new ComplexNumber(1, (float) img);
                break;
            case LEFT_BOTTOM:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, (float) img);

                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sx = new ComplexNumber(1, (float) img);

                break;
            case RIGHT_BOTTOM:
                d = pml.getHeight();
                dBegin = y - pml.getYBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sy = new ComplexNumber(1, (float) img);

                d = pml.getWidth();
                dBegin = x - pml.getXBegin();
                fac = Math.pow(dBegin / d, 2);
                img = -(3 * Math.log(1 / PML.getR()) * fac) / (2 * k0 * d * refractiveIndex);
                sx = new ComplexNumber(1, (float) img);

                break;
            default:
                sx = new ComplexNumber(1, 0);
                sy = new ComplexNumber(1, 0);
                break;
        }

        ComplexNumber[] s = {sx, sy};
        return s;

    }
}
