package com.github.da;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DeploymentArtifacts implements Iterable<Archive> {
	public final List<Archive> da;

	public DeploymentArtifacts(List<Archive> archives) {
		da = Collections.unmodifiableList(new ArrayList<>(archives));
	}

	@Override
	public Iterator<Archive> iterator() {
		return da.iterator();
	}
}
