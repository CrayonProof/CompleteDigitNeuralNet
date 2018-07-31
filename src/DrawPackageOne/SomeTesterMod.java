package DrawPackageOne;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class SomeTesterMod {

	public static void main(String[] args) throws IOException {
		final int WIDTH = 600;
		final int HEIGHT = 600;
		
		MouseTwo wind = new MouseTwo();
		wind.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		wind.setSize(WIDTH, HEIGHT);
		wind.setVisible(true);
		
		String path = System.getProperty("user.dir") + "/legoWorker.jpg";
		BufferedImage image = ImageIO.read(new File(path));
		//wind.add(new JScrollPane(test));
		/*
		while (true) {
			if (MouseTwo.msDown()) {
				System.out.println("horp");
			}
			else {
				System.out.println(MouseTwo.msDown());
			}
		}
		*/
		int mouseX = 0;
		int mouseY = 0;

		while (true) {
			
			mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
			mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();

			int panX = (int) wind.getX() + 9;
			int panY = (int) wind.getY() + 31;

			//System.out.println(mDown);
			
			int RADIUS = 10;

			//System.out.println("x: " + mouseX);
			//System.out.println("y: " + mouseY);
			System.out.println(MouseTwo.msDown());

			if ((mouseX > panX) && (mouseY > panY) && (mouseY < panY + HEIGHT - RADIUS) && (mouseX < panX + HEIGHT - RADIUS) && MouseTwo.msDown()) {
				for (int li = 0; li < RADIUS; li++) {
					for (int li2 = 0; li2 < RADIUS; li2++) {
						image.setRGB(mouseX - panX + li, mouseY - panY + li2, 0);
					}
				}
				wind.getRootPane().revalidate();
				wind.repaint();
			}
		}
		
	}

}
