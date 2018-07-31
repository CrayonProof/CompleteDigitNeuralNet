package DrawPackageOne;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageReader {
	public static double[][] imageToArray(BufferedImage image)
			throws IOException {
		double[][] colors = new double[28][28];

		for (int y = 0; y < 28; y++) {
			for (int x = 0; x < 28; x++) {
				double pixcolor = ((double) (image.getRGB(y, x) & 0xFF)) / 255;
				System.out.println(pixcolor);
				colors[x][y] = pixcolor;
			}
		}
		return colors;
	}
}
