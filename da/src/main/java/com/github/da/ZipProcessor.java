package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

public class ZipProcessor implements FileProcessor {

	@Override
	public boolean canProcess(Path p) {
		return p.getFileName().toString().endsWith(".zip");
	}

	@Inject
	EarProcessor ep;

	@Override
	public void process(InputStream is) throws IOException {

		ZipInputStream zis = new ZipInputStream(is);

		for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
			if (e.isDirectory())
				continue;

			if (e.getName().endsWith(".class")) {
				throw new Error();
			} else if (e.getName().endsWith(".ear"))
				ep.process(zis);
			else
				System.err.println("zip process " + e);

		}

	}

}
