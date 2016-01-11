package Examples;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.*;
import java.util.*;

public class SimpleGUI {

	public JPanel Inframe, Outframe, Buttonframe;
	public JTextField Input;
	public JEditorPane Output,Info;
	public JLabel Inputlabel;
	public JScrollPane Scroll, ScrollInfo;
	public String dialogout = "";
	public Vector<String> dialoghistory = new Vector<String>();
	public String displaytext = "";
	String question = "";
	String answer = "";
	Reasoner myReasoner;

	public SimpleGUI() {               // Constructor for an Instance of SimpleGUI

		myReasoner = new Reasoner(this);         // Instantiate a "brain", reference this GUI to it
										                      		
		myReasoner.initknowledge();              // fill "the brain" with knowledge

		Input = new JTextField(80);	
		Inputlabel = new JLabel("Question");
		
		
		Output = new JEditorPane("text/html","<b>Initial text</b>");
		Output.setEditable(false);                 // no one should be able to write in the display	
		Output.setToolTipText("<html>Your dialog with the machine.</html>");
		
		Scroll = new JScrollPane(Output);          									                
		Scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		Scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Scroll.setBorder(BorderFactory.createTitledBorder("Conversation:"));
		Scroll.getViewport().setPreferredSize(new Dimension(500,600));
		
		Info = new JEditorPane("text/html","");	    
		Info.setEditable(false);  			
		Info.setText("Background information about the conversations topic will be displayed in this window.");
				      
	    ScrollInfo = new JScrollPane(Info);   									                
		ScrollInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ScrollInfo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ScrollInfo.setBorder(BorderFactory.createTitledBorder("Background Info:"));
	    ScrollInfo.getViewport().setPreferredSize(new Dimension(500,600));
				
		Inframe = new JPanel();                             // Frame for the Inputelements
		Outframe = new JPanel();                            // Frame for the Outputelements
					
		Inframe.add(Inputlabel);                            // adding the elements to the JPanels
		Inframe.add(Input);
		Outframe.add(Scroll);
		Outframe.add(ScrollInfo);		

		JFrame Main = new JFrame("carHire help centre");   // our main frame
		
		try {
    		Main.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("test.JPG")))));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		
		Main.getContentPane().setLayout(new FlowLayout());
		Main.getContentPane().add(Outframe);
		Main.getContentPane().add(Inframe);
				
		Main.addWindowListener(new WindowAdapter() {        // implementing listeners we need
			public void windowClosing(WindowEvent e) {      // What to do if Window is closed
				System.exit(0);                             // Kill program
			}
		});
				
		Input.addActionListener(new ActionListener() {      // Action if "Enter" is pressed
			public void actionPerformed(ActionEvent e) {    // while "Input" has the Focus
				questionasked();
				Input.setText("");
			}
		});

		Main.pack();
		Main.setSize(1200, 800);
		Main.setVisible(true);                               // Don't forget
		
		dialoghistory.add("<H2>Great choice! Now, as you are here you can ask me anything regarding carHire.</H2> " +
				          "<H3>Including our worldwide vehicles, specific vehicles also information about you " +
				          "our beloved customer! Don't hesitate to ask me anything! If you need any help, you always can ask for some. You can begin by signing in using username first name and last name command</H3><br>");
		
		Output.setText(dialoghistory.firstElement());
		Input.requestFocusInWindow();
	}                                                        // Constructor done

	public void checkbrain() {

		String yo = myReasoner.testit();                     // Testing the reasoner's knowledge
		System.out.println(yo);                              // Testing the reasoner's knowledge
	}

	public void questionasked() {                            // log questions in a String vector

		question = Input.getText();
		generateanswer();
	}

	public void generateanswer() {                           // generate an answer

		Vector<String> answers = new Vector<String>();
				
		answers = myReasoner.generateAnswer(question);
	
		displaytext = "<font color=\"red\" face=\"Georgia, Arial\">You: " + answers.get(0)+
				       "</font>"+"<br><font COLOR=\"green\" face=\"Georgia, Arial\">" + 
				       answers.get(1) + "</font><br>";	
		
		dialoghistory.add(displaytext);                      // write the string to show in the
										                     // dialoghistory
	
		String dialogdisplay = "";
		
		for(int i=0; i<dialoghistory.size(); i++){
			dialogdisplay=dialogdisplay+dialoghistory.get(i);
		}
		
		Output.setText(dialogdisplay);                       // write the dialoghistory to output
		displaytext = "";                                    // clear the "long string" for the next answer
		
	}

	public void setmytooltip(String inputstring) {

		Info.setToolTipText(inputstring);
		System.out.println("Done setting the Tooltiptext");

	}
	
	public void setmyinfobox(String inputstring) {

		 try {
		       Info.setPage(inputstring);
		     }
		    catch (IOException e) {
		       Info.setContentType("text/html");
		       Info.setText("<html>Could not load"+inputstring);
		     } 
		System.out.println("Done setting the Tooltiptext");

	}

	public static void main(String[] args) {                 // main Method (starts when
												             // class/instance is called)
		SimpleGUI mygui = new SimpleGUI();
		mygui.checkbrain();                                  // check if brain is there and knowledge loaded

	}
}
