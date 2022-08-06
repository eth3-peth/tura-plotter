package hk.zdl.tura.plot.gui;

public interface PlotProgressListener {

	enum Type {
		HASH, WRIT
	}
	
	public void onProgress(Type type,float progress,String rate,String ETA);
}
