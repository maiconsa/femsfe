package com.femsfe;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class PairValueCell extends TableCell<Pair<String, Object>, Object> {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (item instanceof String) {
                setText((String) item);
                setGraphic(null);
            } else if (item instanceof Integer) {
                setText(Integer.toString((Integer) item));
                setGraphic(null);
            }else if(item instanceof Double){
            	setText(String.valueOf(item));
                setGraphic(null);     
            }  else if(item instanceof Float){
            	setText(String.valueOf(item));
                setGraphic(null);
                
            }else if (item instanceof CheckBox) {
               CheckBox check = (CheckBox) item;
                setGraphic(check);
            } else if (item instanceof Image) {
                setText(null);
                ImageView imageView = new ImageView((Image) item);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                setGraphic(imageView);
            } else if(item instanceof ComboBox){
            	@SuppressWarnings("unchecked")
				ComboBox<String> cb = (ComboBox<String>) item;
            	setGraphic(cb);
            }else {
                setText("   ");
                setGraphic(null);
            }
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}