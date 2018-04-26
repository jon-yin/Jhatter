package samplegui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class PreferencesDialog extends JDialog{
	
	
	private JCheckBox autoRefresh;
	private List<PreferencesVisitor> visitors;
	private Preferences storedPrefs;
	private ButtonGroup group;
	public static final Dimension MIN_DIMENSION;
	
	static
	{
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		MIN_DIMENSION = new Dimension(resolution.width/4, resolution.height/4);
	}
	
	public PreferencesDialog(JFrame parent, Preferences storedPrefs)
	{
		super(parent, "Preferences Dialog");
		this.storedPrefs = storedPrefs;
		setMinimumSize(MIN_DIMENSION);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JPanel lookandfeel = new JPanel();
		JPanel timeout = new JPanel();
		JPanel buttons = new JPanel();
		group = new ButtonGroup();
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo info : lafs)
		{
			JRadioButton look = new JRadioButton(info.getName());
			group.add(look);
			lookandfeel.add(look);
			look.addActionListener(event ->
			{
				try{
				UIManager.setLookAndFeel(info.getClassName());
				SwingUtilities.updateComponentTreeUI(getParent());
				SwingUtilities.updateComponentTreeUI(this);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			});
		}
		autoRefresh = new JCheckBox("Automatically refresh rooms and users when entering/leaving a room");
		JButton apply = new JButton("Apply");
		JButton cancel = new JButton("Cancel");
		JPanel panel = new JPanel();
		panel.add(autoRefresh);
		buttons.add(apply);
		buttons.add(cancel);
		visitors = new ArrayList<>();
		apply.addActionListener(event -> {saveSettings(); 
		for (PreferencesVisitor pv: visitors){
			pv.loadPreferences();
		}
		setVisible(false);});
		cancel.addActionListener(event -> {
			for (PreferencesVisitor pv: visitors){
				pv.loadPreferences();
			}
			setVisible(false);});
		
		SwingUtilities.getRootPane(this).setDefaultButton(apply);
		add(lookandfeel);
		add(panel);
		add(buttons);
		pack();
		setLocationRelativeTo(parent);
		
	}
	
	public void registerVisitor(PreferencesVisitor visitor)
	{
		visitors.add(visitor);
	}
	
	public void updateView()
	{
		LookAndFeel curFeel = UIManager.getLookAndFeel();
		Enumeration<AbstractButton> buttons = group.getElements();
		while(buttons.hasMoreElements())
		{
			AbstractButton radButton = buttons.nextElement();
			if (radButton.getText().equals(curFeel.getName()))
			{
				radButton.setSelected(true);
				break;
			}	
		}
		autoRefresh.setSelected(storedPrefs.getBoolean("arefresh", false));
		
	}
	
	public void saveSettings()
	{
		Enumeration <AbstractButton> buttons = group.getElements();
		while (buttons.hasMoreElements()){
			AbstractButton b = buttons.nextElement();
			if (b.isSelected())
			{
				//System.out.println(b.getText());
				storedPrefs.put("laf", b.getText());
				break;
			}
		}
		//Put autorefresh setting into preferences
		storedPrefs.putBoolean("arefresh", autoRefresh.isSelected());
	}

}
