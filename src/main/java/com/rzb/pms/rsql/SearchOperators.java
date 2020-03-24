package com.rzb.pms.rsql;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum SearchOperators {

	EQUALITY("=="), GREATER_THAN_EQUAL(">="), LESS_THAN("<"), LIKE("=lk="), NOT_NULL("=NN="), IS_NULL("=NL=");

	String name;

	private SearchOperators(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SearchOperators getOperationFromOperator(String operator) {
		if (SearchOperators.EQUALITY.getName().equalsIgnoreCase(operator)) {
			return EQUALITY;
		} else if (SearchOperators.GREATER_THAN_EQUAL.getName().equalsIgnoreCase(operator)) {
			return GREATER_THAN_EQUAL;

		} else if (SearchOperators.LESS_THAN.getName().equalsIgnoreCase(operator)) {
			return LESS_THAN;

		} else if (SearchOperators.LIKE.getName().equalsIgnoreCase(operator)) {
			return LIKE;

		} else if (SearchOperators.NOT_NULL.getName().equalsIgnoreCase(operator)) {
			return NOT_NULL;

		} else if (SearchOperators.IS_NULL.getName().equalsIgnoreCase(operator)) {
			return IS_NULL;

		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Please Provide right search Operator. Like >=, =lk=, !null, ==, < ");
		}

	}
}
