package com.rzb.pms.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rzb.pms.dto.DrugAutoCompleteDTO;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugType;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.StockProjPost;
import com.rzb.pms.dto.StockProjPre;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.PurchaseOrder;
import com.rzb.pms.repository.StockRepository;

/*
 *Generic class to transform collections between different types
 *Entity to DTO conversion
 */
@Component
public abstract class CollectionMapper<FROM, TO> {

	abstract TO transformCollection(FROM from);

	public List<TO> transformCollection(List<FROM> list) {
		List<TO> to = new ArrayList<TO>();
		for (FROM from : list) {
			to.add(transformCollection(from));
		}
		return to;
	}

	public static List<DrugDTO> mapDrugDtoDrugDTO(List<Drug> list, StockRepository stockRepository) {

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

	public static List<PurchaseOrderDTO> mapPurchaseOrderToPurchaseOrderDTO(List<PurchaseOrder> list,
			List<PoDrugDTO> po) {

		CollectionMapper transformer = new CollectionMapper<PurchaseOrder, PurchaseOrderDTO>() {

			@Override
			PurchaseOrderDTO transformCollection(PurchaseOrder from) {
				List<PoDrugDTO> parsedData = new ArrayList<PoDrugDTO>();
				for (PoDrugDTO s : po) {
					if (from.getPoId() == s.getPoId()) {
						parsedData.add(s);
					}
				}

				return PurchaseOrderDTO.builder().createdBy(from.getCreatedBy()).createdDate(from.getCreatedDate())
						.poId(from.getPoId()).updatedBy(from.getUpdatedBy()).updatedDate(from.getUpdatedDate())
						.referenceNumber(from.getPoReference()).poLineItem(parsedData).poStatus(from.getPoStatus()).build();
			}
		};
		return transformer.transformCollection(list);
	}

	public static List<DrugAutoCompleteDTO> mapDrugToDrugAutoCompleteDTO(List<Drug> from,
			StockRepository stockRepository) {

		CollectionMapper<Drug, DrugAutoCompleteDTO> transformer = new CollectionMapper<Drug, DrugAutoCompleteDTO>() {

			@Override
			DrugAutoCompleteDTO transformCollection(Drug from) {

				List<StockProjPre> data = new ArrayList<StockProjPre>();
				List<StockProjPost> result = new ArrayList<StockProjPost>();

				List<Object[]> stockInfo = stockRepository.findByDrugId(from.getDrugId());
				if (!stockInfo.isEmpty()) {
					for (Object x[] : stockInfo) {

						data.add(StockProjPre.builder().stockId((Integer) x[0]).location((String) x[2])
								.avlQntyWhole((Double) x[1]).build());
					}

				}
				for (StockProjPre d : data) {

					result.add(StockProjPost.builder().stockId(d.getStockId())
							.avlQntyWhole(d.getAvlQntyWhole() % 1 != 0
									? BaseUtil.findQntyInWord(d.getAvlQntyWhole(), from.getDrugForm())

									: BaseUtil.stripTrailingZero(String.valueOf(d.getAvlQntyWhole())) + " "
											+ DrugType.STRIP.toString()

							).location(d.getLocation()).build());

				}

				return DrugAutoCompleteDTO.builder().brandName(from.getBrandName()).company(from.getCompany())
						.composition(from.getComposition()).drugForm(from.getDrugForm()).drugId(from.getDrugId())
						// .genericId(from.getGenericId())
						.genericName(from.getGenericName()).mrp(from.getMrp()).packing(from.getPacking())
						.stockInfo(result).build();
			}

		};

		return transformer.transformCollection(from);

	}
}