package com.github.da;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.da.t.All;
import com.github.da.t.Analysis;
import com.github.da.t.ResourceProcessor;
import com.google.common.io.ByteStreams;

import utils.text.Describable;
import utils.text.Description;

@Analysis
public class JarResourceProcessor implements ResourceProcessor, Describable {

	@Inject
	@All
	List<ResourceProcessor> procs;

	@Override
	public void run(Path p, Provider<InputStream> is) throws IOException {
		if (!p.getFileName().toString().endsWith(".jar") //
				&& !p.getFileName().toString().endsWith(".war") //
				&& !p.getFileName().toString().endsWith(".ear") //
				&& !p.getFileName().toString().endsWith(".zip") //
		)
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

			for (ResourceProcessor x : procs) {
				x.run(Paths.get(e.getName()), pp);
			}
		}
	}

	@Override
	public void describe(Description d) {
		d.withList("resourceProcessors", procs);
	}
}
