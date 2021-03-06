package pkg;

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

public class JarAnalysis implements RootAnalysis<DirAnalysisConfig> {

	@Override
	public void accept(AnalysisVisitor av) {
		throw new Error();
	}

	@Inject
	JarJarProcessor jpp;

	@Override
	public void run(DirAnalysisConfig config, Processors proc) throws IOException {

		jpp.run(null, proc, config.p, new Provider<InputStream>() {

			@Override
			public InputStream get() {
				try {
					return Files.newInputStream(config.p);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		});

		try (InputStream is = Files.newInputStream(config.p)) {
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

				for (JarProcessorInvoker x : proc.invokers) {
					x.run(proc, Paths.get(e.getName()), pp);
				}
			}
		}
	}

}
