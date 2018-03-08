/* Meghana M Reddy

   March 2015
*/

import org.iiitb.es103_15.traffic.Car;
import org.iiitb.es103_15.traffic.Coords;
import org.iiitb.es103_15.traffic.Intersection;
import org.iiitb.es103_15.traffic.Road;
import org.iiitb.es103_15.traffic.RoadGrid;
import org.iiitb.es103_15.traffic.TrafficSignal;
import org.iiitb.es103_15.traffic.TrafficSignal.SignalListener;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class SmartCar25 extends Car implements SignalListener{
	private TrafficSignal signal;
	private Intersection next_intersection;
	private int signal_light;
	private int listener;
	private Car bad_car;
	
	public SmartCar25() {
		signal = null;
		signal_light = -1;
		listener = 1;
		bad_car = null;
	}
	
	public int getValidDir(Intersection i0) {
		/* generating a random valid direction to turn into at an intersection */
		Road[] roads = i0.getRoads();
		Random randomdir = new Random();
		int i = randomdir.nextInt() % 4;
		if (i<0)
			i= i*-1;
		while (roads[i] == null) {
			Random randomdir1 = new Random();
			i = randomdir1.nextInt() % 4;
			if (i<0)
				i= i*-1;
		}
		return i;
	}
	
	public void usualAccelerate() {
		/* the usual accelerating and decelerating of the car when there is no other car in front of this car */
		Road myroad = this.getRoad(); 
		int speed_limit = myroad.getSpeedLimit();
		double dist_intersection;
		dist_intersection = Math.sqrt(Coords.distSqrd(next_intersection.getCoords(), this.getPos()));
		float curr_speed = getSpeed();
		if (dist_intersection >= 100) {
			if (curr_speed < speed_limit - 1) {
				accelerate((float) (speed_limit - getSpeed()), 5);
			}
				
			else if (curr_speed > speed_limit + 1) {
				accelerate(speed_limit - curr_speed, 0);
			}
			
			else if (curr_speed > speed_limit && curr_speed <= speed_limit + 1 ) {
				accelerate(0, 0);
			}
		}
		
		
		else if (dist_intersection < 100) {
			float decelerate = (float) (-1*(getSpeed()*getSpeed())/(2*dist_intersection));
			if (speed_limit > 10) {
				if (decelerate + 4.5 < 0)
					accelerate((float) (decelerate + 4.5f), 0);
				else
					accelerate(-5, 0);
			}
			else if (speed_limit <= 12){
				accelerate(-3, 0);
			}
			if (signal != null) {
				if (signal.getType() == 0) {
					if (dist_intersection > 12 && signal_light != 1 && speed_limit > 10) {
						if (decelerate + 4.5 < 0)
							accelerate((float) (decelerate + 4.5f), 0);
						else
							accelerate(-5, 0);
					}
					else if (dist_intersection > 12 && signal_light != 1 && speed_limit <= 10) {
						accelerate(-3, 0);
					}
					else if (dist_intersection <= 12 && signal_light == 1 && getSpeed() <= 10) {
						int nextdir;
						nextdir = getValidDir(next_intersection);
						int old_dir = RoadGrid.getOppDir(this.getDir());
						crossIntersection(next_intersection, nextdir);
						if (signal == null)
							System.out.println("YO");
						else {
							synchronized(signal) {
								signal.removeListener(this, old_dir);
							}
						}
						signal = null;
						listener = 1;
						signal_light = -1;
						accelerate(5, 0);
					}
					else if (dist_intersection <= 12 && signal_light == 1 && getSpeed() > 10) {
						accelerate(-20000, 0);
						if (dist_intersection <= 3) {
							int nextdir;
							nextdir = getValidDir(next_intersection);
							int old_dir = RoadGrid.getOppDir(this.getDir());
							crossIntersection(next_intersection, nextdir);
							if (signal == null)
								System.out.println("YO");
							else {
								synchronized(signal) {
									signal.removeListener(this, old_dir);
								}
							}
							signal = null;
							listener = 1;
							signal_light = -1;
							accelerate(5, 0);
							System.out.println("accelerate 10, turned");
						}
					}
					else if (dist_intersection <= 12 && signal_light != 1 && speed_limit > 10)
						accelerate(-10000, 0);
					else if (dist_intersection <= 12 && signal_light != 1 && speed_limit <= 10) 
						accelerate(-5000, 0);
					
				}
			}
			
			
			else { //if no signal is there
				signal_light = -1;
				if (dist_intersection <= 12 && next_intersection.isOccupied()  == false && getSpeed() <= 10) {
					int nextdir;
					nextdir = getValidDir(next_intersection);
					crossIntersection(next_intersection, nextdir);
					accelerate(5, 0);
				}
				else if (dist_intersection <= 12 && next_intersection.isOccupied() == true) {
					accelerate(-10000, 0);
				}
				else if (dist_intersection <= 12 && next_intersection.isOccupied()  == false && getSpeed() > 10) {
					accelerate(-20000, 0);
					if (dist_intersection <= 3) {
						int nextdir;
						nextdir = getValidDir(next_intersection);
						crossIntersection(next_intersection, nextdir);
						accelerate(5, 0);
					}
				}
				else if (dist_intersection > 12 && next_intersection.isOccupied()  == false && getSpeed() > 10) 
					accelerate(-5, 0);
			}
		}
		if (getSpeed() < 10) 
			accelerate(10, 0);
	}
	
	public void carefulAccelerate() { 
		/* This function is called when there is a car in front of my car. Hence, 'careful' accelerate. */
		Road myroad = this.getRoad(); 
		int speed_limit = myroad.getSpeedLimit();
		double dist_bad_car = 1000;
		dist_bad_car = Math.sqrt(Coords.distSqrd(bad_car.getPos(), this.getPos()));
		if (speed_limit > 10) {
			if (dist_bad_car < 50 && dist_bad_car > 30) 
				accelerate(-20.0f, 0);
			
			else if (dist_bad_car <= 30) 
				accelerate(-20000, 0);
			
			else if (dist_bad_car >= 50) {
				if (getSpeed() < speed_limit - 1) 
					accelerate(speed_limit - getSpeed(), 0);
				else
					accelerate(0, 0);
			}
		}
		else {
			if (dist_bad_car < 50 && dist_bad_car > 30) 
				accelerate(-5.0f, 0);
			else if (dist_bad_car <= 30) 
				accelerate(-10000, 0);
			else { //if (dist_bad_car >= 50) {
				if (getSpeed() < speed_limit - 1) 
					accelerate(5, 0);
				else if (getSpeed() > speed_limit + 1) 
					accelerate(-2, 0);
			}
		}
	}
	
	public void updatePos() {
		/* The car's position is updated depending on its current position and acceleration */
		super.updatePos();
		Road myroad = this.getRoad(); 
		int speed_limit = myroad.getSpeedLimit();
		int road_dir = myroad.getDir();
		if (this.getDir() != road_dir) {
			next_intersection = myroad.getStartIntersection();
		}
		else {
			next_intersection = myroad.getEndIntersection();
		}
		if (listener == 1) {
			signal = (TrafficSignal) next_intersection.getTrafficControl();
			if (signal != null) {
				synchronized(signal) {
					signal.addListener(this, RoadGrid.getOppDir(this.getDir()));
				}
				listener = 0;
			}
		}
		if (getSpeed() > speed_limit && speed_limit <= 10) 
			accelerate(-2, 0);
		
		if (bad_car == null ) 
			usualAccelerate();
		else 
			carefulAccelerate();
		
	}
	
	public void accelerate(float d, int duration) {
		super.accelerate(d, duration);
	}
	
	protected void crossIntersection(Intersection inter, int dir) {
		/* The car crosses an intersection and moves from one road to another */
		super.crossIntersection(inter, dir);
	}

	@Override
	public void onChanged(int arg0) {
		signal_light = arg0;
	}
	
	public String toString() {
		String str = "Ivashkinator 25 ";
		return str;
	}
	
	public void carInFront(Car arg0) {
		bad_car = arg0;
	} 
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.darkGray);
	}
}

