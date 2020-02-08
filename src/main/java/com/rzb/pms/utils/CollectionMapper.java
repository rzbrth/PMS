package com.rzb.pms.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rzb.pms.dto.DrugAutoCompleteDTO;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugType;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderLineItemsDTO;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.PurchaseOrderLineItems;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.StockRepository;

/*
 *Generic class to transform collections between different types
 *Entity to DTO conversion
 */
@Component
public abstract class CollectionMapper<FROM, TO> {

	@Autowired
	private static StockRepository stockRepository;

	abstract TO transformCollection(FROM from);

	public List<TO> transformCollection(List<FROM> list) {
		List<TO> to = new ArrayList<TO>();
		for (FROM from : list) {
			to.add(transformCollection(from));
		}
		return to;
	}

	public static List<DrugDTO> mapDrugDtoDrugDTO(List<Drug> list) {

		CollectionMapper<Drug, DrugDTO> transformer = new CollectionMapper<Drug, DrugDTO>() {

			@Override
			DrugDTO transformCollection(Drug e) {
				List<String> location = new ArrayList<String>();
				for (Drug data : list) {
					location.add(String.join(",", stockRepository.findLocationByDrugId(data.getDrugId())));
				}

				return DrugDTO.builder().brandName(e.getBrandName()).company(e.getCompany())
						.composition(e.getComposition()).drugForm(e.getDrugForm()).location(String.join(",", location))
						.mrp(e.getMrp()).unitPrice(e.getUnitPrice()).packing(e.getPacking())
						.expiryDate(e.getExpiryDate()).build();
			}
		};
		return transformer.transformCollection(list);
	}

	public static List<PurchaseOrderLineItemsDTO> mapPurchaseOrderToPurchaseOrderDTO(List<PurchaseOrderLineItems> list,
			List<PurchaseOrderDTO> po) {

		CollectionMapper<PurchaseOrderLineItems, PurchaseOrderLineItemsDTO> transformer = new CollectionMapper<PurchaseOrderLineItems, PurchaseOrderLineItemsDTO>() {

			@Override
			PurchaseOrderLineItemsDTO transformCollection(PurchaseOrderLineItems from) {
				List<PurchaseOrderDTO> parsedData = new ArrayList<PurchaseOrderDTO>();
				for (PurchaseOrderDTO s : po) {
					if (from.getPoLId() == s.getPoLId()) {
						parsedData.add(s);
					}
				}

				return PurchaseOrderLineItemsDTO.builder().createdBy(from.getCreatedBy()).createdDate(from.getCreatedDate())
						.poLId(from.getPoLId()).updatedBy(from.getUpdatedBy()).updatedDate(from.getUpdatedDate())
						.poReference(from.getPoReference()).poLineItem(parsedData).poStatus(from.getPoStatus()).build();
			}
		};
		return transformer.transformCollection(list);
	}

	public static List<DrugAutoCompleteDTO> mapDrugToDrugAutoCompleteDTO(List<Drug> from) {

		CollectionMapper<Drug, DrugAutoCompleteDTO> transformer = new CollectionMapper<Drug, DrugAutoCompleteDTO>() {

			@Override
			DrugAutoCompleteDTO transformCollection(Drug from) {

				List<String> result;

				String wholeAvlQntyInWords = null, location = null;
				List<Stock> stockInfo = stockRepository.findByDrugId(from.getDrugId());
				if (stockInfo.isEmpty()) {
					wholeAvlQntyInWords = "No Stock Available";
					location = null;

				} else {
					for (Stock x : stockInfo) {

						if (x.getAvlQntyWhole() % 1 != 0) {
							wholeAvlQntyInWords = BaseUtil.findQntyInWord(x.getAvlQntyWhole(), from.getDrugForm());
							

						} else {
							wholeAvlQntyInWords = BaseUtil.stripTrailingZero(String.valueOf(x.getAvlQntyWhole())) + " "
									+ DrugType.STRIP.toString() + " " + "of" + " " + from.getDrugForm();
						}

					}
				}

				return DrugAutoCompleteDTO.builder().brandName(from.getBrandName()).company(from.getCompany())
						.composition(from.getComposition()).drugForm(from.getDrugForm()).drugId(from.getDrugId())
						.genericId(from.getGenericId()).genericName(from.getGenericName()).mrp(from.getMrp())
						.packing(from.getPacking()).wholeAvlQntyInWords(wholeAvlQntyInWords).build();
			}

		};

		return transformer.transformCollection(from);

	}
}