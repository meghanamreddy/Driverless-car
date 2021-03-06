

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.iiitb.es103_15.traffic.*;


public class TestTraffic8demo {

	 RoadGrid rg;
	 ArrayList<Car> cars; 
	 static int paintCounter = 0;
	
	private  void updateScores(JTextArea textArea) {
		String str = "";
		int i = 0;
		for(Car car: cars) {
			i++;
			int[] scores = car.getScore();
			str = str + "Car " + i + ":  -" + scores[0] + "  +" + scores[1] + "  d:" + scores[2];
			str = str + " " + car.getLastViolation();
			if(scores[3] > 0)
				str = str + " Crashed!!\n";
			else
				str = str + "\n";
		}
		textArea.setText(str);
	}

	@SuppressWarnings("serial")
	public void startGui() {
		
		rg = new RoadGrid();
		cars = new ArrayList<Car>();
		
		Intersection i1, i2, i3, i4, i5, i6, i7, i8;
		
		i1 = new Intersection(400, 100);
		i2 = new Intersection(700, 100);
		i3 = new Intersection(700, 400);
		i4 = new Intersection(700, 600);
		i5 = new Intersection(400, 600);
		i6 = new Intersection(100, 600);
		i7 = new Intersection(100, 400);
		i8 = new Intersection(400, 400);
		
		rg.add(i1);
		rg.add(i2);
		rg.add(i3);
		rg.add(i4);
		rg.add(i5);
		rg.add(i6);
		rg.add(i7);
		rg.add(i8);
		
		Road r1, r2, r3, r4, r5, r6, r7, r8, r9, r10;
		
		r1 = new SmartRoad(RoadGrid.EAST, i1, i2);
		r2 = new SmartRoad(RoadGrid.SOUTH, i2, i3);
		r2.setSpeedLimit(100);
		r3 = new SmartRoad(RoadGrid.SOUTH, i3, i4);
		r4 = new SmartRoad(RoadGrid.EAST, i5, i4);
		r5 = new SmartRoad(RoadGrid.EAST, i6, i5);
		r6 = new SmartRoad(RoadGrid.SOUTH, i7, i6);
		r6.setSpeedLimit(60);
		r7 = new SmartRoad(RoadGrid.EAST, i7, i8);
		r8 = new SmartRoad(RoadGrid.SOUTH, i1, i8);
		r8.setSpeedLimit(10);
		r9 = new SmartRoad(RoadGrid.EAST, i8, i3);
		r9.setSpeedLimit(40);
		r10 = new SmartRoad(RoadGrid.SOUTH, i8, i5);
		
		
		rg.add(r1);
		rg.add(r2);
		rg.add(r3);
		rg.add(r4);
		rg.add(r5);
		rg.add(r6);
		rg.add(r7);
		rg.add(r8);
		rg.add(r9);
		rg.add(r10);
		
		TrafficSignal ts1 = new TrafficSignal(i3, 5000);
		i3.setTrafficControl(ts1);
		
		TrafficSignal ts2 = new TrafficSignal(i8, 8000);
		i8.setTrafficControl(ts2);
		
		Car car1 = new SmartCar25();
		car1.setInitialPos(r1, i1.getCoords(), RoadGrid.EAST);
		
		Car car2 = new TestCar();
		car2.setInitialPos(r2, i2.getCoords(), RoadGrid.SOUTH);
		
		Car car3 = new TestCar();
		car3.setInitialPos(r2, i3.getCoords(), RoadGrid.NORTH);
		
		Car car4 = new TestCar();
		car4.setInitialPos(r4, i4.getCoords(), RoadGrid.WEST);
		
		Car car5 = new SmartCar25();
		car5.setInitialPos(r5, i5.getCoords(), RoadGrid.WEST);
		
		
		Car car6 = new SmartCar25();
		car6.setInitialPos(r6, i6.getCoords(), RoadGrid.NORTH);
		
		Car car7 = new TestCar(7);
		car7.setInitialPos(r7, i7.getCoords(), RoadGrid.EAST);
		
		Car car8 = new TestCar(8);
		car8.setInitialPos(r10, i8.getCoords(), RoadGrid.SOUTH);
	
		cars.add(car1);
		cars.add(car2);
		cars.add(car3);
		cars.add(car4);
		cars.add(car5);
		cars.add(car6);
		cars.add(car7);
		cars.add(car8);
		
		
		for(Car car:cars)
			rg.add(car);
		
		rg.startSimulation();
		
		// create panel to draw
		JFrame frame = new JFrame("Traffic Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// set up a canvas for drawing the position of the cars

		final JPanel canvas = new JPanel() {
			public void paintComponent(Graphics g) {
				g.clearRect(getX(), getY(), getWidth(), getHeight());
				rg.paint(g);
			}
		};

		canvas.setPreferredSize(new Dimension(1000, 1000));
		frame.add(canvas, BorderLayout.LINE_START);

		final JTextArea textArea = new JTextArea(6, 20);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);
		
		frame.add(scrollPane, BorderLayout.LINE_END);

		
		
		// update view periodically
		ActionListener taskPerformer = new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				// force repainting of canvas every time the timer fires.
				canvas.repaint();
				if((paintCounter++ % 5) == 0)
					updateScores(textArea);
			}
		};
		new Timer(400, taskPerformer).start();
		
		// Display the window.
		frame.pack();
		frame.setVisible(true); // starts EDT if not already started
		// EDT continues until explicitly terminated with exit (or exception)
	}
	
	public static void main(String[] args) {
		final TestTraffic8demo mainActivity = new TestTraffic8demo();
		// start the GUI in the EDT thread
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	mainActivity.startGui();
				    }
				});
	}
}
