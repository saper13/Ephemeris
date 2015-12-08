# Ephemeris
Simple numerical orbital propagator based on Orekit
First 6 input areas request orbital elements (Earth orbit):
  1 Semi Major Axis (m)
  2 Eccentricity (-)
  3 Inclination (deg)
  4 Perigee argument (deg)
  5 Right ascention of ascending node (deg)
  6 Mean anomaly (deg)
Next two areas are restpectively:
  7 initial timestep of adaptive timestep propagator (s)
  8 overall time of propagation (s)
  
Deafault force is only gravity attraction (Holmes Featherstone model)

Using checkbox you can enable:
- aerodynamic drag force (Harris Priester model)
- solar radiation pressure force (model takes into account eclipse and penumbra)

The output lines are returned every 60 initial timesteps (e.g. if initial timestep is 60 s the results will be written for every hour)
the result consists of: time (propagation starts 1.01.2016 at noon) and orbital elements, in such order and units as input data.

The program has been written as a student project to compare influence of aerodynamic drag and solar radiation pressure.

The project is based on Orekit 7.0 libraries, using also Common Maths3 3.5 .

