package pkg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Provider;

import com.google.common.io.ByteStreams;

public class JarJarProcessor implements JarContentProcessor<Void> {

	@Override
	public void accept(AnalysisVisitor av) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(Void config, Processors proc, Path p, Provider<InputStream> is) throws IOException {
		if (!p.getFileName().toString().endsWith(".jar") && !p.getFileName().toString().endsWith(".ear"))
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
			if (false)
				pp = new Provider<InputStream>() {

					@Override
					public InputStream get() {
						return zis;
					}

				};
			for (JarProcessorInvoker x : proc.invokers) {
				x.run(proc, Paths.get(e.getName()), pp);
			}
		}
	}
}
