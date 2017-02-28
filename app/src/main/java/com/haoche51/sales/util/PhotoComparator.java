package com.haoche51.sales.util;

import java.util.Comparator;

import com.haoche51.sales.entity.PhotoEntity;

public class PhotoComparator implements Comparator<PhotoEntity> {

	@Override
	public int compare(PhotoEntity lhs, PhotoEntity rhs) {
		int indexOne = lhs.getIndex();
		int indexTwo = rhs.getIndex();
		if (indexOne < indexTwo){
			return -1;
		}else if (indexOne == indexTwo){
			return 0;
		}else if (indexOne > indexTwo){
			return 1;
		}
		return 0;
	}

	

}
