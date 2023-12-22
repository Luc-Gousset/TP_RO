package tp.vrp;

import tp.vrp.Data.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class TourPlotter {

    public static void plotTours(List<List<Integer>> tours, List<Node> nodes) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("VRP Tour Visualization")
                .xAxisTitle("X Coordinate")
                .yAxisTitle("Y Coordinate")
                .build();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setZoomEnabled(true);

        Random rand = new Random();

        for (int i = 0; i < 2; i++) {
            double[] xData = new double[tours.get(i).size()];
            double[] yData = new double[tours.get(i).size()];

            for (int j = 0; j < tours.get(i).size(); j++) {
                Node node = NodeUtil.findNodeById(tours.get(i).get(j), nodes);
                xData[j] = node.getLatitude();
                yData[j] = node.getLongitude();
            }

            XYSeries series = chart.addSeries("Tour " + (i + 1), xData, yData);
            series.setMarker(SeriesMarkers.CIRCLE);
            series.setMarkerColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())); // Random color
            series.setLineColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())); // Random color
        }

        new SwingWrapper(chart).displayChart();
    }

    public static void plotSequence(List<Integer> seq, List<Node> nodes) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("VRP Tour Visualization")
                .xAxisTitle("X Coordinate")
                .yAxisTitle("Y Coordinate")
                .build();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setZoomEnabled(true);
        double[] xData = new double[seq.size()];
        double[] yData = new double[seq.size()];

        Random rand = new Random();
        for (int j = 0; j < seq.size(); j++) {
            Node node = NodeUtil.findNodeById(seq.get(j), nodes);
            xData[j] = node.getLatitude();
            yData[j] = node.getLongitude();
        }
        XYSeries series = chart.addSeries("Tour " + (1), xData, yData);
        series.setMarker(SeriesMarkers.CIRCLE);
        series.setMarkerColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())); // Random color
        series.setLineColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())); // Random color

        new SwingWrapper(chart).displayChart();
    }

}
