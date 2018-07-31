package DrawPackageOne;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class MouseStuff {
	static JFrame frame = new JFrame();

	static class MainPanel extends JPanel {
		public MainPanel() {
			setBackground(Color.gray);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}
	}

	static class BorderPanel extends JPanel {

		JLabel stackLabel;
		int pX, pY;

		public BorderPanel() {
			ImageIcon icon = new ImageIcon(getClass().getResource(
					System.getProperty("user.dir") + "/legoWorker.jpg"));
			stackLabel = new JLabel();
			stackLabel.setIcon(icon);

			setBackground(Color.black);
			setLayout(new FlowLayout(FlowLayout.RIGHT));

			add(stackLabel);

			stackLabel.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					System.exit(0);
				}
			});
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					// Get x,y and store them
					pX = me.getX();
					pY = me.getY();
				}
			});
			addMouseMotionListener(new MouseAdapter() {
				public void mouseDragged(MouseEvent me) {
					frame.setLocation(frame.getLocation().x + me.getX() - pX,
							frame.getLocation().y + me.getY() - pY);
				}
			});
		}
	}

	static class OutsidePanel extends JPanel {

		public OutsidePanel() {
			setLayout(new BorderLayout());
			add(new MainPanel(), BorderLayout.CENTER);
			add(new BorderPanel(), BorderLayout.PAGE_START);
			setBorder(new LineBorder(Color.BLACK, 5));
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame.setUndecorated(true);
				frame.add(new OutsidePanel());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}