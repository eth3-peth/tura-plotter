package hk.zdl.crypto.peth.plot.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;

public class Util {
	public static final ExecutorService es = Executors.newCachedThreadPool(r -> {
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		return t;
	});

	public static final Process plot(Path plot_bin, Path target, boolean benchmark, BigInteger id, long start_nonce, long nonces, PlotProgressListener listener) throws IOException {
		if (!Files.exists(plot_bin.toAbsolutePath())) {
			plot_bin = findPath(plot_bin);
		}
		if (!plot_bin.toFile().exists()) {
			throw new FileNotFoundException(plot_bin.toString());
		} else if (!plot_bin.toFile().isFile()) {
			throw new FileNotFoundException("not a file: " + plot_bin.toString());
		} else if (!plot_bin.toFile().canRead()) {
			throw new IOException("cannot read: " + plot_bin.toString());
		} else if (!plot_bin.toFile().canExecute()) {
			throw new IOException("not executable: " + plot_bin.toString());
		}
		if (!target.toFile().exists()) {
			throw new FileNotFoundException(target.toString());
		} else if (!target.toFile().isDirectory()) {
			throw new IOException("not dir: " + target.toString());
		}
		List<String> l = new LinkedList<>();
		l.add(plot_bin.toAbsolutePath().toString());
		if (benchmark) {
			l.add("-b");
		}
		l.addAll(Arrays.asList("--id", id.toString(), "--sn", Long.toString(start_nonce), "--n", Long.toString(nonces), "-p", target.toAbsolutePath().toString()));
		Process proc = new ProcessBuilder(l).start();
		BufferedReader reader = proc.inputReader();
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		String line = null;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			} else {
				line = line.trim();
			}
			if (line.isEmpty() || line.equals("[2A")) {
				continue;
			} else if (line.startsWith("Error: ")) {
				reader.close();
				throw new IOException(line.substring("Error: ".length()));
			} else if (line.equals("Starting plotting...")) {
				es.submit(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						String line = null;
						while (true) {
							line = reader.readLine();
							if (line == null) {
								queue.offer(null);
								break;
							} else if (line.isEmpty() || line.equals("[2A")) {
								continue;
							} else {
								if (line.contains("鈹傗")) {
									byte[] bArr = line.getBytes("GBK");
									line = new String(bArr, "UTF-8");
									line = line.replace("�?", "│");
								}
								queue.offer(line);
							}
						}
						return null;
					}
				});
				break;
			}
		}
		es.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				String line = null;
				while (true) {
					line = queue.take();
					if (line == null) {
						break;
					}
					if (line.startsWith("Hashing:") || line.startsWith("Writing:")) {
						PlotProgressListener.Type type = line.startsWith("H") ? PlotProgressListener.Type.HASH : PlotProgressListener.Type.WRIT;
						line = line.substring(line.lastIndexOf('│') + 1);
						float progress = Float.parseFloat(line.substring(0, line.indexOf('%')).trim());
						line = line.substring(line.indexOf('%') + 1).trim();
						String rate, eta = "";
						if (line.endsWith("B/s")) {
							rate = line;
						} else {
							rate = line.substring(0, line.lastIndexOf(" ")).trim().replace(" ", "");
							eta = line.substring(line.lastIndexOf(" ")).trim();
						}
						listener.onProgress(type, progress, rate, eta);
					}
				}
				return null;
			}
		});
		return proc;
	}

	private static final Path findPath(Path p) throws IOException {
		return IOUtils.readLines(new ProcessBuilder().command("which", p.toString()).start().getInputStream(), "UTF-8").stream().map(Paths::get).findFirst().get();
	}

}
