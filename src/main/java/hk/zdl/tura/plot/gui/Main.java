package hk.zdl.tura.plot.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Taskbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.jthemedetecor.OsThemeDetector;
import com.softsec.util.ChhUtil;

public class Main {

	private static final long byte_per_nounce = 262144;

	@SuppressWarnings("serial")
	public static void main(String[] args) throws Throwable {
		System.setProperty("apple.awt.application.appearance", "system");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		var otd = OsThemeDetector.getDetector();
		UIManager.setLookAndFeel(otd.isDark() ? new FlatDarkLaf() : new FlatLightLaf());
		var app_icon = ImageIO.read(Main.class.getClassLoader().getResource("app_icon.png"));
		try {
			Taskbar.getTaskbar().setIconImage(app_icon);
		} catch (Exception x) {
		}
		var frame = new JFrame();
		frame.setIconImage(app_icon);
		var layout = new CardLayout();
		frame.setLayout(layout);
		var option_pane = new JPanel();
		frame.add(option_pane, "option_pane");

		option_pane.setLayout(new GridBagLayout());
		var id_label = new JLabel("Wallet ID");
		option_pane.add(id_label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var id_field = new JTextField("1234567890", 100);
		option_pane.add(id_field, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		var path_label = new JLabel("Path");
		option_pane.add(path_label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var path_field = new JTextField(100);
		path_field.setText(System.getProperty("user.home"));
		path_field.setEditable(false);
		option_pane.add(path_field, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var path_btn = new JButton("Select...");

		option_pane.add(path_btn, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var fz_label = new JLabel("File Size");
		option_pane.add(fz_label, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var fz_slider = new JSlider(100, 10000000, 1000);
		option_pane.add(fz_slider, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		var fz_combo_box = new JComboBox<>(quick_size.values());
		fz_combo_box.addActionListener(e -> fz_slider.setValue(((quick_size) fz_combo_box.getSelectedItem()).size()));
		option_pane.add(fz_combo_box, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		var fz_label_1 = new JLabel();
		fz_label_1.setHorizontalAlignment(JLabel.RIGHT);
		option_pane.add(fz_label_1, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var fz_label_2 = new JLabel("Bytes");
		option_pane.add(fz_label_2, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		var start_btn = new JButton("Start");
		start_btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
		option_pane.add(start_btn, new GridBagConstraints(0, 4, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		layout.show(frame.getContentPane(), "option_pane");
		var progress_pane = new PlotProgressPanel() {

			@Override
			public void onDone() {
				start_btn.setEnabled(true);
				layout.show(frame.getContentPane(), "option_pane");
				setDone(false);
			}
		};
		frame.add(progress_pane, "progress_pane");
		var size = new Dimension(640, 480);
		frame.setPreferredSize(size);
		frame.setMinimumSize(size);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		otd.registerListener(isDark -> {
			Stream.of(new FlatLightLaf(), new FlatDarkLaf()).filter(o -> o.isDark() == isDark).forEach(FlatLaf::setup);
			SwingUtilities.invokeLater(() -> {
				SwingUtilities.updateComponentTreeUI(frame);
			});
		});

		fz_slider.addChangeListener(e -> fz_label_1.setText(ChhUtil.strAddComma(fz_slider.getValue() * byte_per_nounce + "")));
		fz_combo_box.setSelectedIndex(0);
		progress_pane.setDone(false);

		path_btn.addActionListener(e -> {
			var file_dialog = new JFileChooser();
			file_dialog.setDialogType(JFileChooser.SAVE_DIALOG);
			file_dialog.setMultiSelectionEnabled(false);
			file_dialog.setDragEnabled(false);
			file_dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int i = file_dialog.showOpenDialog(frame);
			if (i == JFileChooser.APPROVE_OPTION) {
				path_field.setText(file_dialog.getSelectedFile().getAbsolutePath());
			}
		});
		start_btn.addActionListener(e -> {
			var id = id_field.getText().trim().replace("+", "");
			try {
				Long.parseUnsignedLong(id);
			} catch (NumberFormatException x) {
				JOptionPane.showMessageDialog(frame, "Invalid Wallet ID!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			var dir = new File(path_field.getText());
			if (!dir.exists()) {
				JOptionPane.showMessageDialog(frame, "Path not exist!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (!dir.isDirectory()) {
				JOptionPane.showMessageDialog(frame, "Path is not Directory!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (!dir.canWrite()) {
				JOptionPane.showMessageDialog(frame, "Path is not Writable!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Util.es.submit(() -> {

				start_btn.setEnabled(false);
				try {
					layout.show(frame.getContentPane(), "progress_pane");
					Path plotter_bin_path = copy_plotter().toPath();
					Util.plot(plotter_bin_path, dir.toPath(), false, new BigInteger(id), Math.abs(new Random().nextInt()), fz_slider.getValue(), progress_pane);

				} catch (IOException x) {
					JOptionPane.showMessageDialog(frame, x.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					layout.show(frame.getContentPane(), "option_pane");
					start_btn.setEnabled(true);
					return;
				}
			});
		});
	}

	private static File copy_plotter() throws IOException {
		String suffix = "";
		if (SystemInfo.isWindows) {
			suffix = ".exe";
		}
		File tmp_file = File.createTempFile("plotter-", suffix);
		tmp_file.deleteOnExit();
		String in_filename = "";
		if (SystemInfo.isLinux) {
			in_filename = "signum-plotter";
		} else if (SystemInfo.isWindows) {
			in_filename = "signum-plotter.exe";
		} else if (SystemInfo.isMacOS) {
			in_filename = "signum-plotter-x86_64-apple-darwin.zip";
		}
		InputStream in = Main.class.getClassLoader().getResourceAsStream("lib/" + in_filename);
		FileOutputStream out = new FileOutputStream(tmp_file);
		IOUtils.copy(in, out);
		out.flush();
		out.close();
		in.close();
		if (SystemInfo.isMacOS) {
			ZipFile zipfile = new ZipFile(tmp_file);
			ZipEntry entry = zipfile.stream().findAny().get();
			in = zipfile.getInputStream(entry);
			tmp_file = File.createTempFile("plotter-", ".app");
			tmp_file.deleteOnExit();
			out = new FileOutputStream(tmp_file);
			IOUtils.copy(in, out);
			out.flush();
			out.close();
			in.close();
			zipfile.close();
		}
		tmp_file.setExecutable(true);
		return tmp_file;
	}

	private enum quick_size {
		MB_100, GB_1, GB_100, GB_500, GB_1000, GB_2000;

		int size() {
			switch (this) {
			case GB_1:
				return 4000;
			case GB_100:
				return 400000;
			case GB_1000:
				return 4000000;
			case GB_2000:
				return 8000000;
			case GB_500:
				return 2000000;
			case MB_100:
				return 400;
			default:
				break;

			}
			return 0;
		}

		public String toString() {
			switch (this) {
			case GB_1:
				return "1GB";
			case GB_100:
				return "100GB";
			case GB_1000:
				return "1000GB";
			case GB_2000:
				return "2000GB";
			case GB_500:
				return "500GB";
			case MB_100:
				return "100MB";
			default:
				return "";
			}
		}
	}

}
