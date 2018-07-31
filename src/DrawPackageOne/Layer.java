package DrawPackageOne;

/* The layer of a neural network
 *
 * @Ian Goodwin
 * @4/19/2018
 */
public class Layer {
	public double[] activations;
	public double[] biases;
	public final int numNeurons;

	public Layer(int nN) {
		// initialise instance variables
		activations = new double[nN];
		biases = new double[nN];
		numNeurons = nN;
	}

	public Layer(double[] bias) {
		numNeurons = bias.length;
		biases = bias;
		activations = new double[numNeurons];
	}

	public int length() {
		return activations.length;
	}

	public void print() {
		for (int i = 0; i < activations.length; i++) {
			System.out.println(activations[i]);

		}
		System.out.println("");
	}

	/**
	 * Sets the activations of a layer to certain values
	 *
	 * parameter fresh is the array of new ("fresh") values for the activations
	 */
	public void setActivations(double[] fresh) {
		for (int i = 0; i < fresh.length; i++) {
			activations[i] = fresh[i];
		}
	}
}