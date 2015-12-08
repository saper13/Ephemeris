package Ephemeris;
/* Copyright 2002-2015 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package fr.cs.examples.propagation;

import java.util.Locale;

import javax.swing.JTextArea;

import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.forces.SphericalSpacecraft;
import org.orekit.forces.drag.Atmosphere;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.HarrisPriester;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.radiation.SolarRadiationPressure;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;


import Ephemeris.Autoconfiguration;


public class mySlavePropagator {

	
	public static void propagate (double a, double e, double i,double omega,double raan,double lM,
			double stepS, double duration, boolean solarBL, boolean aeroBL, JTextArea TF)
	{
		try {

            // configure Orekit
            Autoconfiguration.configureOrekit();

            // Inertial frame
            Frame inertialFrame = FramesFactory.getEME2000();

            // Initial date in UTC time scale
            TimeScale utc = TimeScalesFactory.getUTC();
            AbsoluteDate initialDate = new AbsoluteDate(2016, 01, 01, 12, 00, 00.000, utc);

            // gravitation coefficient
            double mu =  3.986004415e+14;

            // Orbit construction as Keplerian
            Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, PositionAngle.MEAN,
                                                    inertialFrame, initialDate, mu);

            // Initial state definition
            SpacecraftState initialState = new SpacecraftState(initialOrbit);
            
            // Adaptive step integrator with a minimum step of 0.001 and a maximum step of 1000
            final double minStep = 0.001;
            final double maxstep = 1000.0;
            final double positionTolerance = 0.1;
            final double initStep = stepS;
            final OrbitType propagationType = OrbitType.KEPLERIAN;
            final double[][] tolerances =
                    NumericalPropagator.tolerances(positionTolerance, initialOrbit, propagationType);
            AdaptiveStepsizeIntegrator integrator =
                    new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);
            
            integrator.setInitialStepSize(initStep);
            
            NumericalPropagator propagator = new NumericalPropagator(integrator);
            propagator.setOrbitType(propagationType);
            
            // Force Model (reduced to perturbing gravity field)
            final NormalizedSphericalHarmonicsProvider provider =
                    GravityFieldFactory.getNormalizedProvider(10, 10);
            ForceModel holmesFeatherstone =
                    new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,true), provider);
            
            // Aero drag
            
            final Frame earthFrame = CelestialBodyFactory.getEarth().getBodyOrientedFrame();
            final double ae = provider.getAe();
            final SphericalSpacecraft ssc = new SphericalSpacecraft(1,0.47, 0., 1.2);
            final OneAxisEllipsoid earth = new OneAxisEllipsoid(ae, Constants.WGS84_EARTH_FLATTENING, earthFrame);
            Atmosphere atmosphere = new HarrisPriester (CelestialBodyFactory.getSun(), earth);
            
            ForceModel aerodrag  = new DragForce(atmosphere, ssc);
            
            //Solar radiation pressure
            ForceModel solarPressure = new SolarRadiationPressure(CelestialBodyFactory.getSun(), ae, ssc);
            
            
            // Add force model to the propagator
            propagator.addForceModel(holmesFeatherstone);
            
            if(aeroBL)
            	propagator.addForceModel(aerodrag);
            
            if(solarBL)
            	propagator.addForceModel(solarPressure);
            
            // Set up initial state in the propagator
            propagator.setInitialState(initialState);
            
            // Set the propagator to slave mode (could be omitted as it is the default mode)
            propagator.setSlaveMode();

            // Overall duration in seconds for extrapolation
            //double duration = 630.;

            // Stop date
            final AbsoluteDate finalDate = initialDate.shiftedBy(duration);

            // Step duration in seconds
            double stepT = stepS;
            // Extrapolation loop
            //int cpt = 1;
            
            
            
            for (AbsoluteDate extrapDate = initialDate;
                 extrapDate.compareTo(finalDate) <= 0;
                 extrapDate = extrapDate.shiftedBy(60*stepT))  {

                SpacecraftState currentState = propagator.propagate(extrapDate);
                //System.out.println(/*"step " + */cpt++);
                //System.out.println(/*" time : " + */currentState.getDate());
                //System.out.println(" " + currentState.getOrbit());
                KeplerianOrbit o = (KeplerianOrbit) currentState.getOrbit();
                String tempSTR;
                tempSTR = String.format(Locale.US, "%s %12.3f %10.8f %10.6f %10.6f %10.6f %10.6f%n",
                        currentState.getDate(),
                        o.getA(), o.getE(),
                        FastMath.toDegrees(o.getI()),
                        FastMath.toDegrees(o.getPerigeeArgument()),
                        FastMath.toDegrees(o.getRightAscensionOfAscendingNode()),
                        FastMath.toDegrees(o.getTrueAnomaly()));
               
                
                TF.append(tempSTR);
            }

        } catch (OrekitException oe) {
            System.err.println(oe.getMessage());
        }
	}
	
	
    public static void main(String[] args) 
    {
    	
    }

}
