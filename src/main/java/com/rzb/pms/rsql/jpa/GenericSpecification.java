package com.rzb.pms.rsql.jpa;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.rsql.SearchCriteria;
import com.rzb.pms.rsql.SearchOperators;

public class GenericSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 215567766380666135L;

	private SearchCriteria criteria;

	Logger logger = LoggerFactory.getLogger(GenericSpecification.class);

	public GenericSpecification(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	/*
	 * Based on the passed constraint this function will construct the actual query.
	 * Here We specify the conditions of the invoked database query. Only Allowed
	 * Condition here are GREATER_THAN_EQUAL, LESS_THAN, EQUALITY, LIKE and NOT_NULL
	 * Here the Key is the dataBase column and OPERATION(GREATER_THAN_EQUAL,
	 * LESS_THAN, EQUALITY, LIKE and NOT_NULL) based on those detail we will form
	 * query.
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		switch (SearchOperators.getOperationFromOperator(criteria.getOperation())) {

		case GREATER_THAN_EQUAL: {
			if (root.get(criteria.getKey()).getJavaType().equals(Date.class)) {
				return builder.greaterThanOrEqualTo(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
			} else {
				return builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()),
						criteria.getValue().toString());
			}
		}
		case LESS_THAN: {
			if (root.get(criteria.getKey()).getJavaType().equals(Date.class)) {
				return builder.lessThan(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
			} else {
				return builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
			}
		}
		case EQUALITY: {
			if (root.get(criteria.getKey()).getJavaType().equals(Integer.class)
					|| root.get(criteria.getKey()).getJavaType().equals(Long.class)
					|| root.get(criteria.getKey()).getJavaType().equals(BigDecimal.class)) {
				return builder.equal(root.get(criteria.getKey()), criteria.getValue());
			} else if (root.get(criteria.getKey()).getJavaType().equals(Date.class)) {
				return builder.equal(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
			} else if (root.get(criteria.getKey()).getJavaType().equals(Boolean.class)) {
				return builder.equal(root.<Boolean>get(criteria.getKey()),
						Boolean.valueOf((String) criteria.getValue()));
			} else {
				return builder.equal(builder.lower(root.get(criteria.getKey())),
						criteria.getValue().toString().toLowerCase());
			}
		}

		case LIKE: {
			if (root.get(criteria.getKey()).getJavaType().equals(Integer.class)
					|| root.get(criteria.getKey()).getJavaType().equals(Long.class)
					|| root.get(criteria.getKey()).getJavaType().equals(BigDecimal.class)
					|| root.get(criteria.getKey()).getJavaType().equals(Date.class)) {
				return builder.like(root.get(criteria.getKey()), '%' + criteria.getValue().toString() + '%');
			} else {
				return builder.like(builder.lower(root.get(criteria.getKey())),
						'%' + criteria.getValue().toString().toLowerCase() + '%');

			}
		}
		case NOT_NULL: {
			return builder.isNotNull(root.get(criteria.getKey()));

		}
		case IS_NULL: {
			return builder.isNull(root.get(criteria.getKey()));
		}
		default:
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Please Provide right search Operation. Like >=, =lk=, !null, ==, < ");
		}

	}

}
