package com.femsfe;

public enum ButtonsID {
	BTN_CPOINT(OperationType.CREATE),
	BTN_CLINE(OperationType.CREATE),
	BTN_CPOLYLINE(OperationType.CREATE),
	BTN_CRECTANGLE(OperationType.CREATE),
	BTN_CPOLYGON(OperationType.CREATE),
	BTN_CCIRCLE(OperationType.CREATE),
	BTN_CARC(OperationType.CREATE),
	BTN_CBEZIER(OperationType.CREATE),
	BTN_CNFACE(OperationType.CREATE),
	BTN_CBFACE(OperationType.CREATE),
	BTN_CEFACE(OperationType.CREATE),
	
	BTN_LINE_DIVISION(OperationType.SELECT),
	BTN_CIRCLE_DIVISION(OperationType.SELECT),
	BTN_POINTS_DISTANCE(OperationType.SELECT),
	BTN_LINES_INTER(OperationType.SELECT),
	BTN_LINES_CIRCLE_INTER(OperationType.SELECT),
	BTN_LINES_ARC_INTER(OperationType.SELECT),
	BTN_CIRCLES_INTER(OperationType.SELECT),
	
	BTN_REMOVE_POINTS(OperationType.SELECT),
	BTN_REMOVE_LINES(OperationType.SELECT),
	BTN_REMOVE_ARCS(OperationType.SELECT),
	BTN_REMOVE_CIRCLES(OperationType.SELECT),
	BTN_REMOVE_FACES(OperationType.SELECT),
	BTN_REMOVE_BEZIER(OperationType.SELECT),
	
	BTN_BC_POTENTIAL(OperationType.SELECT),
	BTN_BC_DENSITY(OperationType.SELECT),
	BTN_BC_SPACE_DENSITY(OperationType.SELECT),
	BTN_BC_PERM_FIELD(OperationType.SELECT),
	BTN_ASSIGN_MATERIAL(OperationType.SELECT),
	
	BTN_EXPAND(OperationType.VIEW),

	TGLBTN_ZOOM_IN(OperationType.VIEW),
	TGLBTN_ZOOM_OUT(OperationType.VIEW),
	TGLBTN_PAN(OperationType.VIEW),
	
	TGLBTN_ISOLINE(OperationType.VIEW),
	TGLBTN_COLORMAP(OperationType.VIEW),
	TGLBTN_ARROW(OperationType.VIEW)
	
	;
	
	private OperationType operation;
	private ButtonsID(OperationType operation) {
		this.operation = operation;
	}
	public OperationType getOperationType(){
		return this.operation;
	}
	
}
