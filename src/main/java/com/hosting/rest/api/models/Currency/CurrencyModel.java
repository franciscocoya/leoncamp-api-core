package com.hosting.rest.api.models.Currency;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "APP_CURRENCY")
public class CurrencyModel implements Serializable{

	private static final long serialVersionUID = 7209376522058803717L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer idCurrency;

	@Column(name = "CURRENCY_ALPHANUMERIC_CODE")
	private String currencyAlphanumericCode;

	@Column(name = "CURRENCY_NAME")
	private String currencyName;

	@Column(name = "CURRENCY_CODE")
	private Integer currencyCode;
}
