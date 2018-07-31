package DrawPackageOne;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageExp extends JPanel implements ActionListener {
	private static final boolean DEBUG_GRAPHICS_LOADED = false;
	BufferedImage image;
	Dimension size = new Dimension();

	static final int XDIM = 280;
	static final int YDIM = 280;

	public ImageExp(BufferedImage image) {
		this.image = image;
		size.setSize(image.getWidth(), image.getHeight());
	}

	/**
	 * Drawing an image can allow for more flexibility in processing/editing.
	 */
	protected void paintComponent(Graphics g) {
		// Center image in this component.
		int x = (getWidth() - size.width) / 2;
		int y = (getHeight() - size.height) / 2;
		g.drawImage(image, x, y, this);
	}

	public Dimension getPreferredSize() {
		return size;
	}

	public static boolean mDown = false;

	public static void main(String[] args) throws IOException {

		String path = System.getProperty("user.dir") + "/legoWorker.jpg";
		BufferedImage image = new BufferedImage(XDIM, YDIM, 1); // ImageIO.read(new
																// File(path));
		ImageExp test = new ImageExp(image);
		MouseTwo f = new MouseTwo();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new JScrollPane(test));

		HandlerClassOne handler = new HandlerClassOne();
		test.addMouseListener(handler);

		f.setSize(XDIM + 30, YDIM + 100);
		f.setLocation(100, 100);
		f.setVisible(true);

		JButton button = new JButton("Submit");
		JPanel panel = new JPanel();
		f.add(panel, BorderLayout.SOUTH);
		panel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage resizedImage = new BufferedImage(28, 28,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(image, 0, 0, 28, 28, null);
				g.dispose();
				ImageExp a = new ImageExp(resizedImage);
				MouseTwo b = new MouseTwo();
				b.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				b.add(new JScrollPane(a));
				HandlerClassOne h = new HandlerClassOne();
				a.addMouseListener(h);

				// f.dispatchEvent(new WindowEvent(f,
				// WindowEvent.WINDOW_CLOSING));

				// b.setSize(50, 100);
				// b.setLocation(400, 400);
				// b.setVisible(true);

				
				 double[][] jabo; try { jabo =
				 ImageReader.imageToArray(resizedImage);
				 
				 for (int i = 0; i < 28; i++) { for (int k = 0; k < 28; k++) {
				 System.out.print(jabo[i][k]); } System.out.println(""); } }
				 catch (IOException e2) { // TODO Auto-generated catch block
				 e2.printStackTrace(); }
				 

				try {
					Network net = Network.load("C:/Users/micky/Desktop/APCS/DrawTestOne/NetSets01");
					int answer = net.outputForward(ImageReader
							.imageToArray(resizedImage));

					System.out.println("Answer: " + answer);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		int mouseX = 0;
		int mouseY = 0;

		while (true) {
			mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
			mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();

			int panX = (int) f.getX() + 9;
			int panY = (int) f.getY() + 50;

			// System.out.println(HandlerClassOne.msDown());

			int RADIUS = 10;

			// System.out.println("x: " + mouseX);
			// System.out.println("y: " + mouseY);
			int xx = 0;
			final int WHITE = 16777215;

			if ((mouseX > panX + RADIUS) && (mouseY > panY + RADIUS)
					&& (mouseY < panY + YDIM - RADIUS)
					&& (mouseX < panX + YDIM - RADIUS)
					&& HandlerClassOne.msDown()) {
				for (int bi = 0; bi < 1; bi++) {
					for (double cr = 0; cr < RADIUS; cr += .5) {
						for (int li2 = 0; li2 < 2 * cr; li2++) {
							// image.setRGB(mouseX - panX + li, mouseY - panY +
							// li2, 16777215);

							xx = (int) (-cr + (double) li2);
							image.setRGB(mouseX - panX + xx + bi, mouseY - panY
									+ (int) (Math.sqrt(cr * cr - xx * xx)),
									WHITE);
							image.setRGB(mouseX - panX + xx + bi, mouseY - panY
									- (int) (Math.sqrt(cr * cr - xx * xx)),
									WHITE);
							image.setRGB(
									mouseX
											- panX
											+ (int) (Math.sqrt(cr * cr - xx
													* xx)), mouseY - panY + xx
											+ bi, WHITE);
							image.setRGB(
									mouseX
											- panX
											- (int) (Math.sqrt(cr * cr - xx
													* xx)), mouseY - panY + xx
											+ bi, WHITE);
						}
					}
				}
				// test.getRootPane().revalidate();
				f.repaint();
			}
		}

		// showIcon(image);
	}

	/**
	 * Easy way to show an image: load it into a JLabel and add the label to a
	 * container in your gui.
	 */
	private static void showIcon(BufferedImage image) {
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon, JLabel.CENTER);
		JOptionPane.showMessageDialog(null, label, "icon", -1);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}