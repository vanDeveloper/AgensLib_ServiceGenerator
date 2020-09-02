/************************************************************************************************************************************
 * LICENSE
 ************************************************************************************************************************************
 * 	This file is part of AgensLib.
 * 
 *  AgensLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  AgensLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with AgensLib.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.navi.agenslib.gui;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.navi.agenslib.gui.callbacks.AgensLibCallback;
import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.settings.Configuration;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JProgressBar;

/**
 * Created by van on 10/06/20.
 */
public class AgensLib {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AgensLib PACKAGE NAME
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final String PACKAGE_NAME = "com/navi/agenslib/";
	//private static final String AAR_PATH_DEBUG = "AgensLib/agenslib/build/outputs/aar/agenslib-debug.aar";
	//private static final String AAR_PATH_RELASE = "AgensLib/agenslib/build/outputs/aar/agenslib-release.aar";
	private static final String CONTENT_MSG = "<html><div style='text-align:center;'>Using Multi-Agent Systems to improve the server " + 
									  "communication in a mobile environment. Mapping the services provided in the Postman collection."+ 
									  " Parallelizing the code generation tasks.</div><html>";
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AgensLib GLOBAL COMPONENTS
	//--------------------------------------------------------------------------------------------------------------------------------
	private JFrame frame;
	private JTextArea txtArea;
	private JTextField txtFile;
	private JTextField txtOutput;
	private JButton btnGenerateCode;
	private JLabel lblLog;
	private JProgressBar progress;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private File file;
	private File outputDir;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		// Initializing JADE
		AgentUtils.setupJade();
	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AgensLib window = new AgensLib();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AgensLib() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 439);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// INITIALIZING THE TOP IMAGE BANNER 
		Image imgLogo = new ImageIcon(this.getClass().getResource("/branding_logo.jpg")).getImage();
		JLabel lblLogo = new JLabel("");
		lblLogo.setIcon(new ImageIcon(imgLogo));
		lblLogo.setBounds(0, 0, 600, 84);
		frame.getContentPane().add(lblLogo);
		
		// INITIALIZING THE TITLE LABEL
		JLabel lblTitle = new JLabel("BETA PLATFORM FOR CODE GENERATION");
		lblTitle.setFont(new Font("Malayalam MN", Font.PLAIN, 18));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(131, 96, 357, 29);
		frame.getContentPane().add(lblTitle);
		
		// INITIALIZING THE MAIN CONTENT MESSAGE LABEL
		JLabel lblMsg = new JLabel(CONTENT_MSG);
		lblMsg.setForeground(Color.WHITE);
		lblMsg.setBounds(58, 124, 476, 74);
		frame.getContentPane().add(lblMsg);
		
		// INITIALIZING A LABEL REFERENCE
		JLabel lblResponseMsg = new JLabel("<html><div style='text-align:left;'>Generic service response<br>error message: *</div></html>");
		lblResponseMsg.setForeground(Color.WHITE);
		lblResponseMsg.setBounds(68, 205, 170, 40);
		frame.getContentPane().add(lblResponseMsg);
		
		// INITIALIZING THE LABEL THAT WILL ACT AS A BUTTON TO DISPLAY A INFORMATIVE MESSAGE
		JLabel lblKnowMore = new JLabel("Know more...");
		lblKnowMore.setForeground(Color.WHITE);
		lblKnowMore.setBounds(470, 245, 64, 16);
		lblKnowMore.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		Font font = lblKnowMore.getFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		lblKnowMore.setFont(font.deriveFont(attributes));
		frame.getContentPane().add(lblKnowMore);
		lblKnowMore.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(frame, 
					"This message is used to set a generic error in the server-side,\n" +
						"this error will be responded by the server.\n\n" + 
						"You should take the example and change it according to your needs.", 
					"Information", 
					JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		// INITIALIZING THE TEXT AREA FIELD TO SET THE GENERIC ERROR MESSAGE
		txtArea = new JTextArea();
		txtArea.setText("Ej. No se puede puede conectar con el servidor.");
		txtArea.setBounds(245, 205, 289, 40);
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		frame.getContentPane().add(txtArea);
		txtArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) { }

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					validForm();
					
					if(!validString(txtArea.getText())) {
						JOptionPane.showMessageDialog(frame, 
							"Generic service response error message has invalid characters.\n" + 
								"(Only alphanumeric and some special characteres are allowed)", 
							"String has invalid characters", 
							JOptionPane.ERROR_MESSAGE);
					} 
				}
			}

			@Override
			public void keyReleased(KeyEvent e) { }
			
		});
		
		// INITIALIZING A LABEL REFERENCE
		JLabel lblFormFile = new JLabel("<html><div style='text-align:left;'>Choose Postman<br>collection:*</div></html>");
		lblFormFile.setForeground(Color.WHITE);
		lblFormFile.setBounds(68, 262, 160, 29);
		frame.getContentPane().add(lblFormFile);
		
		// INITIALIZING THE TEXTFIELD THAT WILL STORE THE INPUT PATH
		txtFile = new JTextField();
		txtFile.setBounds(242, 262, 195, 30);
		frame.getContentPane().add(txtFile);
		txtFile.setColumns(10);
		txtFile.setEditable(false);
		txtFile.setBackground(Color.LIGHT_GRAY);
		
		// INITIALIZING THE BUTTON TO UPLOAD THE INPUT FILE
		JButton btnUpload = new JButton("Upload");
		btnUpload.setBounds(436, 262, 105, 30);
		frame.getContentPane().add(btnUpload);
		btnUpload.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				fileChooser(e);
				validForm();
		    }
		});
		
		// INITIALIZING A LABEL REFERENCE
		JLabel lblselectTheOutputfolder = new JLabel("<html><div style='text-align:left;'>Select the output<br>folder:*</div></html>");
		lblselectTheOutputfolder.setForeground(Color.WHITE);
		lblselectTheOutputfolder.setBounds(68, 304, 160, 29);
		frame.getContentPane().add(lblselectTheOutputfolder);
		
		// INITIALIZING THE TEXTFIELD THAT WILL STORE THE OUTPUT PATH
		txtOutput = new JTextField();
		txtOutput.setEditable(false);
		txtOutput.setColumns(10);
		txtOutput.setBackground(Color.LIGHT_GRAY);
		txtOutput.setBounds(242, 304, 195, 30);
		frame.getContentPane().add(txtOutput);
		
		// INITIALIZING THE BUTTON TO SELECT THE OUTPUT CLASSES
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDir(e);
				validForm();
			}
		});
		btnBrowse.setBounds(436, 304, 105, 30);
		frame.getContentPane().add(btnBrowse);
		
		// INITIALIZING THE BUTTON TO GENERATE THE CLASSES 
		btnGenerateCode = new JButton("Generate classes");
		btnGenerateCode.setBounds(238, 346, 149, 30);
		frame.getContentPane().add(btnGenerateCode);
		btnGenerateCode.setVisible(false);
		btnGenerateCode.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				generateCode(e);		
		    }
		});
		
		// INITIALIZING THE PROGRESSBAR LOG LABEL 
		lblLog = new JLabel("");
		lblLog.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblLog.setForeground(Color.WHITE);
		lblLog.setBounds(0, 377, 600, 16);
		frame.getContentPane().add(lblLog);
		
		// INITIALIZING THE PROGRESSBAR
		progress = new JProgressBar();
		progress.setBounds(0, 391, 600, 20);
		progress.setMaximum(60);
		frame.getContentPane().add(progress);
		
		// INITIALIZING THE BACKGROUNG IMAGE
		JLabel lblBackground = new JLabel("");
		lblBackground.setBounds(0, 84, 600, 335);
		frame.getContentPane().add(lblBackground);
		Image tmpImg = new ImageIcon(this.getClass().getResource("/background.jpg")).getImage();
		Image imgBackground = tmpImg.getScaledInstance(lblBackground.getWidth(), lblBackground.getHeight(), Image.SCALE_SMOOTH);
		lblBackground.setIcon(new ImageIcon(imgBackground));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- FORM VALIDATION METHOD
	//--------------------------------------------------------------------------------------------------------------------------------
	private void validForm() {
		if( (this.txtArea.getText() != null && !this.txtArea.getText().equals("")) && 
			(this.txtFile.getText() != null && !this.txtFile.getText().equals("")) &&
			(this.txtOutput.getText() != null && !this.txtOutput.getText().equals("")) ) {
			this.btnGenerateCode.setVisible(true);
		} else {
			this.btnGenerateCode.setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- UPLOAD VALID POSTMAN COLLECTION (JSON FORMAT)
	//--------------------------------------------------------------------------------------------------------------------------------
	private void fileChooser(ActionEvent event) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setCurrentDirectory(new java.io.File("~/Documents"));
	    fileChooser.setDialogTitle("Select your Postman collection");
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    
	    int returnValue = fileChooser.showOpenDialog(null);
	    
	    if (returnValue == JFileChooser.APPROVE_OPTION) {
	        
	        if(!validInputFile(fileChooser.getSelectedFile()) ) {
	        	JOptionPane.showMessageDialog(frame, "File is not a JSON format.", "File can't be read", JOptionPane.ERROR_MESSAGE);
	        } else {
	        	this.file = fileChooser.getSelectedFile();
	        	this.txtFile.setText(this.file.getName());
	        	this.validForm();
		    }
	    }
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- UPLOAD VALID POSTMAN COLLECTION (JSON FORMAT)
	//--------------------------------------------------------------------------------------------------------------------------------
	private void chooseDir(ActionEvent event) {
	    JFileChooser dirChooser = new JFileChooser();
	    dirChooser.setCurrentDirectory(new java.io.File("~/Docuemnts"));
	    dirChooser.setDialogTitle("Select the output folder");
	    dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    dirChooser.setAcceptAllFileFilterUsed(false);
	    
		int returnValue = dirChooser.showOpenDialog(null);
		    
		if (returnValue == JFileChooser.APPROVE_OPTION) {
	        this.outputDir = dirChooser.getSelectedFile();
	        this.txtOutput.setText(this.outputDir.getAbsolutePath());
	        this.validForm();
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- VALIDATING POSTMAN COLLECTION
	//--------------------------------------------------------------------------------------------------------------------------------
	private boolean validInputFile(File inputFile) {
		JSONParser parser = new JSONParser();
        
        try {
        	Object obj = parser.parse(new FileReader(inputFile));
	        JSONObject postmanCollection = (JSONObject) obj;
	        
	        if(postmanCollection.containsKey("info") && postmanCollection.containsKey("item")){
	        	return true;
	        } else {
	        	return false;
	        }
        	
        } catch (ParseException | IOException e) {
			return false;
        }
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- METHOD THAT RUN THE MAS PROCESS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void generateCode(ActionEvent event) {
		JSONParser parser = new JSONParser();
        
		if(!validString(this.txtArea.getText())) {
			JOptionPane.showMessageDialog(frame, 
					"Generic service response error message has invalid characters.\n" + 
						"(Only alphanumeric and some special characteres are allowed)", 
					"String has invalid characters", 
					JOptionPane.ERROR_MESSAGE);
		} else {
	        try {
	        	Object obj = parser.parse(new FileReader(this.file.getAbsolutePath()));
		        JSONObject postmanCollection = (JSONObject) obj;
		        
		        if(postmanCollection.containsKey("info") && postmanCollection.containsKey("item")){
		        	JSONObject info = (JSONObject)postmanCollection.get("info");
		        	JSONArray webservices = (JSONArray)postmanCollection.get("item");
		        	
		        	// CONFIGURE THE FINAL LIB OUTPUT PATH
		        	Configuration simulation = new Configuration((String)info.get("name"), 
		        			this.txtArea.getText(), 
		        			this.txtOutput.getText() + "/agenslib/" + PACKAGE_NAME);
		        			//PACKAGE_NAME);
		        	
		        	SwingUtilities.invokeLater( new Runnable() {
		        		public void run() {
		        			try {
		        				frame.setEnabled(false);
		        				setupProgressCallback();
		        				MasSetup.sharedInstance().run(simulation, info, webservices);
		        			} catch(Exception e) {
		        				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		        			}
						}
					});
		        } else {
		        	this.showMessageDialog("File can't be read", "File is not a JSON format.", JOptionPane.ERROR_MESSAGE);
		        }
	        } catch (ParseException | IOException e) {
				this.showMessageDialog("File can't be read", "File is not a JSON format.", JOptionPane.ERROR_MESSAGE);
			}
	     }
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- INITIALIZING THE CALLBACK TO CONTROL THE PROGRESSBAR AND THE LOG LABEL
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setupProgressCallback() {
		new ProgressBarTimer(lblLog, progress, new AgensLibCallback() {
			@Override
			public void clean() {
				if(!MasSetup.finished) {
					showMessageDialog("Process blocked", 
									  "Process was suspended due to internal over time,\nthe application should be restarted", 
									  JOptionPane.WARNING_MESSAGE);
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				} else {
					// TO BUILD THE AAR FILE
					//executeCommand("AgensLib/gradle assembleDebug");
					showMessageDialog("Process finished", 
							"Classes successfully generated,\nthe application is going to close", 
							JOptionPane.INFORMATION_MESSAGE);
				    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));	
				    
				}
			}
		});
	}

	/*
	private void executeCommand(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	lblLog.setText(line);
			}
		    reader.close();
		    
		    showMessageDialog("Process finished", 
					"Classes successfully generated,\nthe application is going to close", 
					JOptionPane.INFORMATION_MESSAGE);
		    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));	
		} catch (Exception e) {
		    showMessageDialog("Error ocurred", e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
		    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}	
	}
	*/
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- REGEX FOR THE TEXT AREA FIELD
	//--------------------------------------------------------------------------------------------------------------------------------
	private boolean validString(String s){
        return s.matches("[a-zA-Z ñÑ.,áÁéÉíÍóÓúÚ]{6,70}$");
    }
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- REGEX FOR THE TEXT AREA FIELD
	//--------------------------------------------------------------------------------------------------------------------------------
	private void showMessageDialog(String title, String msg, int optionPane) {
		JOptionPane.showMessageDialog(frame, msg, title, optionPane);
	}
}
