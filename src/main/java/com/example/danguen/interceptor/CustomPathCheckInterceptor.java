package com.example.danguen.interceptor;

import java.util.function.Function;

public interface CustomPathCheckInterceptor {
	default Long getIdFromPath(String path, Function<String, Long> function) {
		return function.apply(path);
	}
}
