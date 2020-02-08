package com.rzb.pms.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class PurchaseOrderLineItems {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer poLId;

	private Date createdDate;

	private Date updatedDate;

	private String createdBy;

	private String updatedBy;

	private String poStatus;

	private String poReference;

//	@OneToMany(mappedBy = "orders")
//	private List<PurchaseOrder> podrug;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "poLId", insertable = false, updatable = false)
	private List<PurchaseOrder> podrug;

}
