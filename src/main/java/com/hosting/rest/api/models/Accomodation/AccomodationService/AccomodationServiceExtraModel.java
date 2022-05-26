package com.hosting.rest.api.models.Accomodation.AccomodationService;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Getter
@Table(name = "ACCOMODATION_SERVICE_EXTRA")
@PrimaryKeyJoinColumn(name = "ID")
public class AccomodationServiceExtraModel extends AccomodationServiceModel {

	private static final long serialVersionUID = 5407182388179643418L;
	
	@Column(name = "PRICE")
	private BigDecimal price;
}
