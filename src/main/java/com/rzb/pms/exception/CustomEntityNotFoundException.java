package com.rzb.pms.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomEntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1220025804090090927L;

	public CustomEntityNotFoundException(Class<?> clazz, String... searchParamsMap) {
		super(CustomEntityNotFoundException.createteMessage(clazz.getSimpleName(),
				toMap(String.class, String.class, searchParamsMap)));
		log.error(CustomEntityNotFoundException.createteMessage(clazz.getSimpleName(),
				toMap(String.class, String.class, searchParamsMap)), this);
	}

	private static String createteMessage(String entity, Map<String, String> searchParams) {
		return StringUtils.capitalize(entity) + " was not found for parameters " + searchParams;
	}

	private static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object... entries) {
		if (entries.length % 2 == 1)
			throw new IllegalArgumentException("Invalid entries");
		return IntStream.range(0, entries.length / 2).map(i -> i * 2).collect(HashMap::new,
				(m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])), Map::putAll);
	}

}
