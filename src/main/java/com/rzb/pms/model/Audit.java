package com.rzb.pms.model;

import java.util.Date;

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
	
	private Date createdDate;

	private String createdBy;
	
	private String updatedDate;
	
	private String updatedBy;

	private Integer customerId;
	
	private Integer dispenseId;
	
	private Integer stockId;
	
	private Integer poId;
	
	

 

}
