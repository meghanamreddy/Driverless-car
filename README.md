Implementation of a driverless car by simulating traffic scenarios where these cars navigate their way around 
collaboratively.
The driverless car follows the basic traffic rules which include -
* Car does not collide with some other car.
* Stay on the left of road; can briefly move to the right while overtaking slower/stalled vehicles.
* Traffic signals must be followed.
* Always maintain some safe distance from the car in front.

The basic framework of the road network along with helper classes are available as jar files.
The file TestTraffic8.java needs to be run for testing the code and starting a simulation of a road network with cars.

If a car violates any traffic rules, a penalty is awarded. If and when a car crashes, the car is removed from the simulation and the total score of the car is displayed.
