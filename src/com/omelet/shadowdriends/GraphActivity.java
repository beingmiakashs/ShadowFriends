package com.omelet.shadowdriends;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GraphActivity extends Activity {

	private GraphicalView mChart;
	private String[] mMonth = new String[] {"May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);
		openChart();
	}

	private void openChart() {

		int z[] = { 0, 1, 2, 3, 4, 5, 6, 7 };
		int x[] = { 100, 150, 120, 140, 100, 90, 180, 100 };

		XYSeries xSeries = new XYSeries(
				"sexual harassment occurances in last six months");

		for (int i = 0; i < z.length; i++) {
			xSeries.add(z[i], x[i]);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(xSeries);

		XYSeriesRenderer Xrenderer = new XYSeriesRenderer();
		Xrenderer.setColor(Color.RED);
		// Include low and max value
		Xrenderer.setDisplayBoundingPoints(true);
		Xrenderer.setPointStyle(PointStyle.DIAMOND);
		Xrenderer.setDisplayChartValues(true);
		Xrenderer.setChartValuesTextSize(20);
		Xrenderer.setFillPoints(false);
		Xrenderer.setLineWidth(5);
		Xrenderer.setPointStrokeWidth(6);

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.setChartTitle("SEXUAL HARASSMENT DATA ANALYTICS");
		mRenderer.setXTitle("months");
		mRenderer.setYTitle("number of harassment");
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXLabels(0);
		mRenderer.setPanEnabled(true);
		mRenderer.setShowGrid(true);
		mRenderer.setClickEnabled(true);

		for (int i = 0; i < z.length; i++) {
			mRenderer.addXTextLabel(i, mMonth[i]);
		}

		mRenderer.addSeriesRenderer(Xrenderer);
		LinearLayout chart_container = (LinearLayout) findViewById(R.id.chart_layout);
		mChart = (GraphicalView) ChartFactory.getLineChartView(getBaseContext(),
				dataset, mRenderer);
		chart_container.addView(mChart);

	}
}
