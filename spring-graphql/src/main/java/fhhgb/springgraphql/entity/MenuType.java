package fhhgb.springgraphql.entity;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MenuType {

	MEAT, VEGETARIAN, VEGAN;

	@JsonCreator
	public static MenuType fromName(String name) {
		return Arrays.stream(MenuType.values())
				.filter(type -> type.name().equals(name))
				.findFirst()
				.orElse(MenuType.MEAT);
	}

}
