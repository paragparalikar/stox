package com.stox.core.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stox.core.intf.Identifiable;
import com.stox.core.intf.Nameable;
import com.stox.core.util.StringUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instrument implements Identifiable<String>, Nameable {

	@Wither
	private String symbol;

	private String isin;

	private Exchange exchange;

	private String name;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date expiry;

	private Double strike;

	private Integer lotSize;

	private Double tickSize;

	private InstrumentType type;

	@Override
	public String getId() {
		return isin;
	}

	@Override
	public void setId(String id) {
		this.isin = id;
	}

	@Override
	public String getName() {
		return StringUtil.hasText(name) ? name : symbol;
	}

	@Override
	public String toString() {
		return getName();
	}
}
