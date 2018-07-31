package DrawPackageOne;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MouseTwo extends JFrame {
	
	public static boolean mDown = false;
	public static int mX = 0;
	public static int mY = 0;
	
	private JPanel panel;
	private JLabel statusbar;
	
	public MouseTwo() {
		super("title");
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.CENTER);
		
		statusbar = new JLabel ("default");
		add(statusbar, BorderLayout.NORTH);
		
		HandlerClass handler = new HandlerClass();
		panel.addMouseListener(handler);
		//panel.addMouseMotionListener(handler);
		
	}
	
	public static boolean msDown() {
		if (mDown) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static int X() {
		return mX;
	}
	
	public static int Y() {
		return mY;
	}
	
	public class HandlerClass implements MouseListener {
		
		public void mouseClicked (MouseEvent e) {
			statusbar.setText(String.format("Clicked at %d, %d",  e.getX(), e.getY()));
			
		}
		public void mousePressed(MouseEvent e) {
			mDown = true;
			mX = e.getX();
			mY = e.getX();
		}
		public void mouseReleased(MouseEvent e) {
			mDown = false;
			statusbar.setText("Released");
		}
		public void mouseEntered(MouseEvent e) {
			statusbar.setText("Entered");
		}
		public void mouseExited(MouseEvent e) {
			statusbar.setText("Exited");
		}
	}
}
