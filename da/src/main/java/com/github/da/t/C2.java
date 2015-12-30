package com.github.da.t;

import java.util.List;

import com.github.da.Configuration;

@Configuration
public class C2 {

	public List<A2Config> a2;

	@Override
	public String toString() {
		return super.toString() + "(a2=" + a2 + ")";
	}
}
