package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

import com.github.da.Configured;

public class TypeMappers {
	@Inject
	@Configured
	List<TypeMapper> mappers;
}
