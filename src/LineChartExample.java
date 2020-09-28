/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;

import javax.swing.JFrame;  
import javax.swing.SwingUtilities;  
import javax.swing.WindowConstants;
  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;  
  
public class LineChartExample extends JFrame {  
  
  private static final long serialVersionUID = 1L;  
  
  public LineChartExample(String apptitle,String title,String y,int a,int b,int c, int d , int e , int f) {  
    super(apptitle);  
    // Create dataset  
    DefaultCategoryDataset dataset = createDataset(a,b,c,d,e,f);  
    // Create chart  
    JFreeChart chart = ChartFactory.createLineChart(  
        title, // Chart title  
        "No. of Tasks", // X-Axis Label  
        y, // Y-Axis Label  
        dataset,
        PlotOrientation.VERTICAL,  
        true,true,false
        );  
  
    ChartPanel panel = new ChartPanel(chart);  
    setContentPane(panel);  
  }  
  
  private DefaultCategoryDataset createDataset(int a , int b ,int c, int d, int e, int f ) {  
  
    String series1 = "Visitor";  
    String series2 = "Unique Visitor";  
  
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
  
   
    // Population in 2005  
    dataset.addValue(a, "LOA", "25");  
    dataset.addValue(b, "PSO", "25");  
      
  
    // Population in 2010  
    dataset.addValue(c, "LOA", "50");  
    dataset.addValue(d, "PSO", "50");  
      
  
    // Population in 2015  
    dataset.addValue(e, "LOA", "75");  
    dataset.addValue(f, "PSO", "75");  
  
    return dataset;  
  }  
  /*
  public static void main(String[] args) {  
    SwingUtilities.invokeLater(() -> {  
      LineChartExample example = new LineChartExample("Line Chart Example","Completion Time Vs No. of Tasks","Completion Time",10,20,30,15,16,18);  
      example.setAlwaysOnTop(true);  
      example.pack();  
      example.setSize(600, 400);  
      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
      example.setVisible(true);  
    });  
  }  
*/
}  