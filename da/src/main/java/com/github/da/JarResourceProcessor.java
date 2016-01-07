package com.github.da;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.da.t.All;
import com.google.common.io.ByteStreams;

import utils.text.Describable;
import utils.text.Description;

@Analysis
public class JarResourceProcessor implements ResourceProcessor, Describable {

	@Inject
	@All
	List<ResourceProcessor> procs;

	@Override
	public void run(Archive parent, ResourceId id, Provider<InputStream> is) throws IOException {
		Archive cu;
		if (id.getPath().getFileName().toString().endsWith(".ear"))
			cu = new EnterpriseArchive(id);
		else if (id.getPath().getFileName().toString().endsWith(".war"))
			cu = new WebArchive(id);
		else if (id.getPath().getFileName().toString().endsWith(".jar"))
			cu = new JavaArchive(id);
		else if (id.getPath().getFileName().toString().endsWith(".zip"))
			cu = new ZipArchive(id);
		else
			return;

		ZipInputStream zis = new ZipInputStream(is.get());

		for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
			if (e.isDirectory())
				continue;

			final ZipEntry fe = e;

			Provider<InputStream> pp = new Provider<InputStream>() {

				byte[] data;

				@Override
				public InputStream get() {

					if (data == null) {
						long size = fe.getSize();

						try {
							data = ByteStreams.toByteArray(zis);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						if (size != -1 && data.length != size)
							throw new Error();
					}

					return new ByteArrayInputStream(data);
				}

			};

			ResourceId id2 = ResourceId.create(id, Paths.get(e.getName()));
			for (ResourceProcessor x : procs)
				x.run(cu, id2, pp);
		}

		parent.add(cu);
	}

	@Override
	public void describe(Description d) {
		d.withList("resourceProcessors", procs);
	}
}
