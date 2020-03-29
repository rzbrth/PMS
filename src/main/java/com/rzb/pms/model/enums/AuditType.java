package com.rzb.pms.model.enums;

public enum AuditType {

	STOCK_IN_DIRECT, // create direct stock
	STOCK_IN_FROM_PO, // create stock from purchase order
	STOCK_UPDATED, // update stock
	STOCK_OUT, // selling item
	STOCK_DELETED, // deleting item
	PO_CREATED, // purchase order created
	PO_UPDATED, // purchase order updated
	PO_DELETED, // purchase order deleted
	DRUG_CREATED, // drug created
	DRUG_UPDATED, // drug updated
	DRUG_DELETED, // drug deleted
	EXPIRED_STOCK_RETURN, // return expired stock
	USER_CREATED, // user created
	USER_DELETED, // user deleted
	USER_DISABLED, // user disabled
	USER_ENABLED, // user enabled
	USER_UPDATED, // user updated
	ITEM_RETURNED, // item returned
	RETURN_NEW, // return and create new stock if stock deleted for that id
	RETURN_UPDATE; // return and update existing stock

	@Override
	public String toString() {
		return super.toString();
	}
}
