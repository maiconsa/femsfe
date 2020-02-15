package com.femsfe;

import com.femsfe.Geometrias.BezierCurve2D;
import com.femsfe.Geometrias.GeometryType;
import com.femsfe.Geometrias.Polygon2D;
import com.femsfe.Geometrias.Polyline2D;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


public class PromptComannd  extends javafx.scene.control.TextArea{
	int caretPosition = -1;
	int textSize = -1;
	public PromptComannd() {
		this.setId("promptComannd");
		this.appendText(">> ");
		//setFocused(true);

		setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				caretPosition = getCaretPosition();
				textSize = getText().length();
				
				switch (event.getCode()) {
					case ENTER:
						if(caretPosition == textSize){
							create();
						}
						break;

				default:
					break;
				}
				
			}
		});
	
	}
	@Override
    public void replaceText(int start, int end, String text) {
        String current = getText();
        // only insert if no new lines after insert position:
        if (! current.substring(start).contains(">>")) {
            super.replaceText(start, end, text);
        }
    }
    @Override
    public void replaceSelection(String text) {
        String current = getText();
        int selectionStart = getSelection().getStart();
        if (! current.substring(selectionStart).contains(">>")) {
            super.replaceSelection(text);
        }
    }
    @Override
    public void deleteText(int start, int end) {
    	String text = getText(start, end);

    	if(!text.equals(">")){
    		super.deleteText(start, end);
    	}
    }
    
    private void create(){
    	String[] lines = getText().split(">>");
		String lastLine = lines[lines.length-1];
			
    	if(!CreateGeometry.started()) {
    		switch (lastLine.trim()) {
				case "CreatePoint":
					CreateGeometry.begin(GeometryType.POINT);
	    			appendText(">>");
					break;
				case "CreateLine":
					CreateGeometry.begin(GeometryType.LINE);
	    			appendText(">>");
					break;
				case "CreatePolylines":
					CreateGeometry.begin(GeometryType.POLYLINE);
	    			appendText(">>");
					break;	
				case "CreateRect":
					CreateGeometry.begin(GeometryType.RECTANGLE);
	    			appendText(">>");
	    			break;
				case "CreatePolygon":
					CreateGeometry.begin(GeometryType.POLYGON);
	    			appendText(">>");
	    			break;
				case "CreateCircle":
					CreateGeometry.begin(GeometryType.CIRCLE);
	    			appendText(">>");
				
					break;
				case "CreateArc":
					CreateGeometry.begin(GeometryType.ARC);
	    			appendText(">>");
					break;
				case "CreateBezierCurve":
					CreateGeometry.begin(GeometryType.BEZIER);
	    			appendText(">>");
					break;
				
				default:
					break;
			}
    		
    		return;
    	
    	}else{
    		if(lastLine.trim().equals("ESC")){
    			CreateGeometry.end();
    			appendText(">> ");
    			return;
    		}
    	}
  
		String[] coords = null;
		if(!lastLine.isEmpty()){ 
			coords = lastLine.split(",");
		}else{
			appendText("Please Enter Coords(x,y)\n");
			appendText(">>");
			return;
		}

		float x = Float.NaN,y = Float.NaN;
    	switch (CreateGeometry.getType()) {
			case POINT:	
					 if(coords.length ==2 ){
							x = Float.parseFloat(coords[0]);
							y = Float.parseFloat(coords[1]);
						
							CreateGeometry.addPoint(x, y);
							appendText(">> Point Created("+x+","+y+")\n");
							appendText(">>  ");
					}else{
							appendText(">> Syntax Error,Enter the coords as:x,y\n");
					      	appendText(">>");
						}
				break;
			case LINE:			
				if(coords.length == 2){
					 x = Float.parseFloat(coords[0]);
					 y = Float.parseFloat(coords[1]);
					
					CreateGeometry.addPoint(x, y);
					appendText(">> Point Created("+x+","+y+")\n");
					appendText(">>");
					
				}else{
					appendText(">> Syntax Error,Enter the coords as: x,y\n");
					appendText(">>");
				}
				break;
			case POLYLINE:
				if(lastLine.trim().equals("end\n")){
					Polyline2D polyline = (Polyline2D) CreateGeometry.getGeometry();
					 Project.addAllGeometry(polyline.edges());
					 CreateGeometry.reset();
					 appendText(">> Polyline Created\n");
					 appendText(">>");
				}else{
				 x = Float.parseFloat(coords[0]);
				 y = Float.parseFloat(coords[1]);
				
				 CreateGeometry.addPoint(x, y);
				 appendText(">>Point Created("+x+","+y+")\n");
				 appendText(">>");
				}
				break;
			case POLYGON:
				if(lastLine.trim().equals("end")){
					Polygon2D poly = (Polygon2D) CreateGeometry.getGeometry();
					 if(!poly.isClosed() && poly.getSize() >=3){ 
						 poly.addPoint(poly.getFirstPoint());
						 if(poly.isClosed()){
							 Project.addGeometry(poly);
							 CreateGeometry.reset(); 
							 appendText(">> Polygon Created\n");
							 appendText(">>");
						 }
					 }
					
				}else{
					if(coords.length ==2){
						x = Float.parseFloat(coords[0]);
						y = Float.parseFloat(coords[1]);
					
						CreateGeometry.addPoint(x, y);
						appendText(">>Point Created("+x+","+y+")\n");
						 appendText(">>");
					}else{
						appendText(">> Syntax Error,Enter the coords as: x,y\n");
						appendText(">>");
					}
				}
				break;
			case RECTANGLE:
				float x1,y1;
				if(coords.length ==4){
					x = Float.parseFloat(coords[0]);
					y = Float.parseFloat(coords[1]);
					x1 = Float.parseFloat(coords[2]);
					y1 = Float.parseFloat(coords[3]);
					CreateGeometry.addPoint(x, y);
					CreateGeometry.addPoint(x1, y1);
					appendText(">> Rectangle Created\n");
					appendText(">>");
				}else{
					appendText(">> Syntax Error,Enter the coords as: x1,y1,x2,y2\n");
					appendText(">>");
				}
				break;
			case CIRCLE:
				double radius = -1;
				if(coords.length ==3){
					x = Float.parseFloat(coords[0]);
					y = Float.parseFloat(coords[1]);
					radius = Double.parseDouble(coords[2]);
					CreateGeometry.addPoint(x, y);
					CreateGeometry.addPoint((float)(x+ radius), y);
					appendText(">> Circle Created\n");
					appendText(">>");
				}else{
					appendText(">> Syntax Error,Enter the coords as: x,y,radius\n");
					appendText(">>");
				}
				break;
			case ARC:
				double r = -1;
				double start,end;
				if(coords.length ==5){
					x = Float.parseFloat(coords[0]);
					y = Float.parseFloat(coords[1]);
					r = Float.parseFloat(coords[2]);
					start = Double.parseDouble(coords[3]);
					end = Double.parseDouble(coords[4]);
					CreateGeometry.addPoint(x, y);
					CreateGeometry.addPoint((float)(x + r*Math.cos(Math.toRadians(start))), (float)(y + r*Math.sin(Math.toRadians(start))));
					CreateGeometry.addPoint((float)(x + r*Math.cos(Math.toRadians(end))),(float)( y + r*Math.sin(Math.toRadians(end))));
					appendText(">> Arc Created\n");
					appendText(">>");
				}else{
					appendText(">> Syntax Error,Enter the coords as: x,y,radius,angleStart,angleEnd\n");
					appendText(">> ");
				}
				break;
			case BEZIER:
				if(lastLine.trim().equals("end")){
					BezierCurve2D curve = (BezierCurve2D) CreateGeometry.getGeometry();
					 Project.addGeometry(curve);
					 CreateGeometry.reset();
					 appendText(">> B�zier Curve Created\n");
					 appendText(">>");
				}else{
				 x = Float.parseFloat(coords[0]);
				 y = Float.parseFloat(coords[1]);
				
				 CreateGeometry.addPoint(x, y);
				 appendText(">>Point Created("+x+","+y+")\n");
				 appendText(">>");
				}
				break;
			default:

				break;
		}
    }
    
    
  

}