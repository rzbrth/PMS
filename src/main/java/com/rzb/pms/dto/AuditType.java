package com.rzb.pms.dto;

public enum AuditType {

	SELL, STOCK_IN_DIRECT, STOCK_IN_FROM_PO, STOCK_UPDATED, STOCK_OUT, STOCK_DELETED, PO_CREATED, PO_UPDATED,
	PO_DELETED, DRUG_CREATED, DRUG_UPDATED, DRUG_DELETED;

	@Override
	public String toString() {
		return super.toString();
	}
}
