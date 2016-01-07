package com.github.da;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class X {

	public static void main(String[] args) throws IOException {
		Path p = Paths.get("//sisters3/misc/incoming/torrent/complete2");

		DirectoryStream<Path> x = Files.newDirectoryStream(p);

		for (Path p1 : x) {
			System.err.println("p1 " + p1);

			for (char c : p1.toString().toCharArray()) {
				System.err.println(c + " " + (int) c + " " + Integer.toHexString((int) c));
			}
		}

	}
}
