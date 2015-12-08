package Ephemeris;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.math3.util.FastMath;

public class Ephemeris_GUI extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton runBt;
	private JLabel semiMajorAxis, eccentricity, inclination, perigeeArgument, rightAscention, meanAnomaly, stepSize, duration;
	private JLabel radiationForceLB, aeroForceLB;
	private JCheckBox aeroForceCB, radiationForceCB;

	private JTextField semiMajorAxisTF, eccentricityTF, inclinationTF, perigeeArgumentTF, rightAscentionTF, meanAnomalyTF, stepSizeTF, durationTF;
	private static JTextArea resultsTF;
	
	public Ephemeris_GUI()
	{
		//Main window
		setSize(1100,770);
		setTitle("Propagator beta 1.0");
		setLayout(null);
		
		runBt = new JButton("Run propagation");
		runBt.setBounds(130,500,150,30);
		add(runBt);
		runBt.addActionListener(this);
		
		//Labels of input data
		semiMajorAxis = new JLabel("Semi Major Axis (m) :");
		semiMajorAxis.setBounds(30,50,150,30);
		add(semiMajorAxis);
		
		eccentricity = new JLabel("Eccentricity (-) :");
		eccentricity.setBounds(30,90,150,30);
		add(eccentricity);
		
		inclination = new JLabel("Inclination (deg) :");
		inclination.setBounds(30,130,150,30);
		add(inclination);
		
		perigeeArgument = new JLabel("Perigee argument (deg) :");
		perigeeArgument.setBounds(30,170,150,30);
		add(perigeeArgument);
		
		rightAscention = new JLabel("RAAN (deg) :");
		rightAscention.setBounds(30,210,150,30);
		add(rightAscention);
		
		meanAnomaly = new JLabel("Mean anomaly (deg) :");
		meanAnomaly.setBounds(30,250,150,30);
		add(meanAnomaly);
		
		stepSize = new JLabel("Step size (s) :");
		stepSize.setBounds(30,290,150,30);
		add(stepSize);
		
		duration = new JLabel("Duration (s) :");
		duration.setBounds(30,330,150,30);
		add(duration);
		
		radiationForceLB = new JLabel("Solar radiation pressure:");
		radiationForceLB.setBounds(30,400,150,30);
		add(radiationForceLB);
		
		aeroForceLB = new JLabel("Aerodynamic drag:");
		aeroForceLB.setBounds(30,440,150,30);
		add(aeroForceLB);
		
		//Text fields of input data
		semiMajorAxisTF = new JTextField("30000000");
		semiMajorAxisTF.setBounds(200,50,150,30);
		add(semiMajorAxisTF);
		
		eccentricityTF = new JTextField("0.7");
		eccentricityTF.setBounds(200,90,150,30);
		add(		eccentricityTF);
		
		inclinationTF = new JTextField("8");
		inclinationTF.setBounds(200,130,150,30);
		add(inclinationTF);
		
		perigeeArgumentTF = new JTextField("180");
		perigeeArgumentTF.setBounds(200,170,150,30);
		add(perigeeArgumentTF);
		
		rightAscentionTF = new JTextField("260");
		rightAscentionTF.setBounds(200,210,150,30);
		add(rightAscentionTF);
		
		meanAnomalyTF = new JTextField("0");
		meanAnomalyTF.setBounds(200,250,150,30);
		add(meanAnomalyTF);
		
		stepSizeTF = new JTextField("60");
		stepSizeTF.setBounds(200,290,150,30);
		add(stepSizeTF);
		
		durationTF = new JTextField("2592000");
		durationTF.setBounds(200,330,150,30);
		add(durationTF);
		
		//Text field of output
		resultsTF = new JTextArea("");
		JScrollPane scrollP = new JScrollPane(resultsTF);
		scrollP.setBounds(400,50,650,650);
		add(scrollP);
		//resultsTF.setBounds(400,50,750,600);
		//add(resultsTF);
		
		//Check boxes
		radiationForceCB = new JCheckBox("");
		radiationForceCB.setBounds(200,400,30,30);
		add(radiationForceCB);
		//radiationForceCB.addActionListener(this);
		
		aeroForceCB = new JCheckBox("");
		aeroForceCB.setBounds(200,440,30,30);
		add(aeroForceCB);
		//aeroForceCB.addActionListener(this);

		//GUI look
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	//action
	public void actionPerformed(ActionEvent e)
	{
		Object inSource = e.getSource();
		if (inSource == runBt)
		{
			boolean solar, aero;
			if(aeroForceCB.isSelected())
				aero = true;
			else
				aero = false;
			if(radiationForceCB.isSelected())
				solar = true;
			else
				solar = false;
	
			mySlavePropagator.propagate(
					Double.parseDouble(semiMajorAxisTF.getText()),
					Double.parseDouble(eccentricityTF.getText()),
					FastMath.toRadians(Double.parseDouble(inclinationTF.getText())),
					FastMath.toRadians(Double.parseDouble(perigeeArgumentTF.getText())),
					FastMath.toRadians(Double.parseDouble(rightAscentionTF.getText())),
					Double.parseDouble(meanAnomalyTF.getText()),
					Double.parseDouble(stepSizeTF.getText()),
					Double.parseDouble(durationTF.getText()),
					solar,
					aero,
					resultsTF);
	    }

	}
	public static void main(String[] args) 
	{
		Ephemeris_GUI homePg = new Ephemeris_GUI();
		homePg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homePg.setVisible(true);
		
		
		

	}
}

