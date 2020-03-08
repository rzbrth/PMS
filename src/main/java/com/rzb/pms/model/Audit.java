package com.rzb.pms.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Audit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer auditId;

	private String auditType;

	private LocalDate createdDate;

	private String createdBy;

	private LocalDate updatedDate;

	private String updatedBy;

	private Integer customerId;

	private Integer dispenseId;

	private Integer stockId;

	private Integer poId;

	private Long userId;

	private String drugId;

	private Integer returnId;

}
