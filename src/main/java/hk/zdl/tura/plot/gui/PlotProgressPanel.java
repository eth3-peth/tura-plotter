package hk.zdl.tura.plot.gui;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlotProgressPanel extends JPanel implements PlotProgressListener {

	public PlotProgressPanel() {
		super(new GridBagLayout());
	}

	@Override
	public void onProgress(Type type, float progress, String rate, String ETA) {
		
	}

}
