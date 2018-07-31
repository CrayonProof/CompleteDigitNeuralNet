package DrawPackageOne;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HandlerClassOne implements MouseListener {

	public static boolean mDown = false;
	public static int mX = 0;
	public static int mY = 0;

	public void mouseClicked(MouseEvent e) {
		// System.out.println(String.format("Clicked at %d, %d", e.getX(),
		// e.getY()));

	}

	public void mousePressed(MouseEvent e) {
		mDown = true;
		mX = e.getX();
		mY = e.getX();
	}

	public void mouseReleased(MouseEvent e) {
		mDown = false;
		// System.out.println("Released");
	}

	public void mouseEntered(MouseEvent e) {
		// System.out.println("Entered");
	}

	public void mouseExited(MouseEvent e) {
		// System.out.println("Exited");
	}

	public static boolean msDown() {
		if (mDown) {
			return true;
		} else {
			return false;
		}
	}

	public static int X() {
		return mX;
	}

	public static int Y() {
		return mY;
	}
}