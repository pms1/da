package com.github.da;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.io.ByteStreams;

@Include(JarJarProcessor.class)
public class JarScanner implements RootAnalysis<JarScannerConfig> {

	@Inject
	JarJarProcessor jpp;

	@Inject
	JarScannerConfig config;

	@Override
	public void run(Processors proc) throws IOException {

		jpp.run(proc, config.getPath(), new Provider<InputStream>() {

			@Override
			public InputStream get() {
				try {
					return Files.newInputStream(config.getPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		});

		if(false)
		try (InputStream is = Files.newInputStream(config.getPath())) {
			ZipInputStream zis = new ZipInputStream(is);

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

				for (JarContentProcessor<?> x : proc.invokers) {
					x.run(proc, Paths.get(e.getName()), pp);
				}
			}
		}
	}

}
