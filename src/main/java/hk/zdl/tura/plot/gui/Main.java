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
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
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
		Taskbar.getTaskbar().setIconImage(app_icon);
		var frame = new JFrame();
		frame.setIconImage(app_icon);
		var layout = new CardLayout();
		frame.setLayout(layout);
		var option_pane = new JPanel();
		frame.add(option_pane, "option_pane");
		var progress_pane = new JPanel();
		frame.add(progress_pane, "progress_pane");

		option_pane.setLayout(new GridBagLayout());
		var id_label = new JLabel("Wallet ID");
		option_pane.add(id_label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var id_field = new JTextField(100);
		option_pane.add(id_field, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var path_label = new JLabel("Path");
		option_pane.add(path_label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var path_field = new JTextField(100);
		path_field.setText(System.getProperty("user.home"));
		path_field.setEditable(false);
		option_pane.add(path_field, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var path_btn = new JButton("Select...");
		option_pane.add(path_btn, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		var fz_label = new JLabel("File Size");
		option_pane.add(fz_label, new GridBagConstraints(0, 2, 1, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var fz_slider = new JSlider(100, 10000000, 100) {

			@Override
			protected void fireStateChanged() {
				super.fireStateChanged();
			}

		};
		option_pane.add(fz_slider, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		var fz_label_1 = new JLabel();
		fz_label_1.setHorizontalAlignment(JLabel.RIGHT);
		option_pane.add(fz_label_1, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		var fz_label_2 = new JLabel("Bytes");
		option_pane.add(fz_label_2, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		var start_btn = new JButton("Start");
		start_btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
		option_pane.add(start_btn, new GridBagConstraints(0, 4, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		layout.show(frame.getContentPane(), "option_pane");
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
		fz_slider.fireStateChanged();

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
			File plotter_bin_path = null;
			try {
				plotter_bin_path = copy_plotter();
			} catch (IOException x) {
				JOptionPane.showMessageDialog(frame, x.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		});
	}

	private static File copy_plotter() throws IOException {
		String suffix = "";
		if (SystemInfo.isWindows) {
			suffix = ".exe";
		}
		File tmp_file = File.createTempFile("xxx", suffix);
		String in_filename = "";
		if (SystemInfo.isLinux) {
			in_filename = "signum-plotter";
		} else if (SystemInfo.isWebswing) {
			in_filename = "signum-plotter.exe";
		}
		IOUtils.copy(Main.class.getClassLoader().getResourceAsStream("lib/" + in_filename), new FileOutputStream(tmp_file));
		return tmp_file;
	}

}
