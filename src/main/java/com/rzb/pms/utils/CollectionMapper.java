package com.rzb.pms.utils;

import java.util.ArrayList;
import java.util.List;

import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.model.Drug;

/*
 *Generic class to transform collections between different types

 */
public abstract class CollectionMapper<FROM, TO> {

	abstract TO transformCollection(FROM from);

	public List<TO> transformCollection(List<FROM> list) {
		List<TO> to = new ArrayList<TO>();
		for (FROM from : list) {
			to.add(transformCollection(from));
		}
		return to;
	}

	public static List<DrugDTO> mapDrugDtoReqResReqRes(List<Drug> list) {

		CollectionMapper transformer = new CollectionMapper<Drug, DrugDTO>() {

			@Override
			DrugDTO transformCollection(Drug e) {

				return DrugDTO.builder().brandName(e.getBrandName()).company(e.getCompany())
						.avlQntyInTrimmed(e.getAvlQntyInTrimmed()).avlQntyInWhole(e.getAvlQntyInWhole())
						.composition(e.getComposition()).drugForm(e.getDrugForm()).location(e.getLocation())
						.mrp(e.getMrp()).unitPrice(e.getUnitPrice()).packing(e.getPacking())
						.expiryDate(e.getExpiryDate()).build();
			}
		};
		return transformer.transformCollection(list);
	}
}