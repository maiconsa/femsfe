package com.femsfe;

import java.util.ArrayList;
import java.util.List;

import com.femsfe.Geometries.Material;

public final class RepositoryMaterials {
	private static List<Material> list = new ArrayList<Material>();
	
	private static float colorArray[][] = {
			{0.1f,0.9f,1f },
			{0.1f,1,0.1f},
			{0.8f,0,0 },
			{0.7f,0,0.9f },
			{1.0f,1.0f,0.0f},
			{1.0f,0.0f,1.0f},
			{0.8f,0.7f,0f }	
	};
	
	public static float[] getColor(int index){
		
		if(index == -1 || index >= colorArray.length){
			float[] color = {1f,1f,1f};
			return color;
		}else{
			return colorArray[index];
		}
	}
	
	public static void addMaterial(Material material){
		material.setColor(colorArray[list.size()]);
		list.add(material);
	}
	public static void addMaterial(int index,Material material){
		list.add(index, material);
	}
	
	public static void addAll(List<Material> materials){
		list.addAll(materials);
	}
	public static void removeMaterial(Material material){
		list.remove(material);
	}
	
	public static void removeMaterial(int index){
		list.remove(index);
	}
	
	public static Material getMaterial(int index){
		return list.get(index);
	}
	
	public static List<Material> getList(){
		return list;
	}
	
	public static int getSize(){
		return list.size();
	}
	
	public static void clear(){
		list.clear();
	}
	public static int getIndex(Material material){
		if(material == null) return -1;
		return list.indexOf(material);
	}

}
