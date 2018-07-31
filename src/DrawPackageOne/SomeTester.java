package DrawPackageOne;

import java.awt.Graphics;

import javax.swing.JFrame;

public class SomeTester {

	public static void main(String[] args) {
		final int WIDTH = 600;
		final int HEIGHT = 600;
		
		MouseTwo wind = new MouseTwo();
		wind.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		wind.setSize(WIDTH, HEIGHT);
		wind.setVisible(true);
		
		while (true) {
			if (MouseTwo.msDown()) {
				System.out.println("horp");
			}
			else {
				System.out.println(MouseTwo.msDown());
			}
		}
		
		
		
	}

}
