package DrawPackageOne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/* A neural network, defined here as a collection of layers.
 *
 * @Ian Goodwin
 * @4/19/2018
 */
public class Network {
	public Layer[] layers;
	// layers[0] is the input layer, and layers[layers.length - 1] is the output
	// layer
	/**
	 * Input layer biases will always be 0!
	 */
	public final int numLayers;
	public int output;
	public final int gradientSize;
	public final int summarySize;
	/*
	 * I didn't just make output a return type for feedForward because I wanted
	 * the output to be more generalizable. Maybe that's not important.
	 */
	public double[][][] weights;
	/*
	 * This is where my notation will probably get wonky. The first index is the
	 * index of the layer where the weights ends - i.e., weights[i][?][?] is one
	 * of the set of weights connecting a neuron from layer index i with a
	 * neuron from layer index i+1. The second index corresponds to the index of
	 * the neuron from layer i-1 that the weight connects to, and the third
	 * index of 'weights' is the index of the neuron in layer i that the weight
	 * feeds into. Incidentally, setting weights up this way will solve the
	 * "ghost weights" problem. I hope that creating a "Network" class won't
	 * slow the process down too much, as it only needs to be created once.
	 */

	// I'm not bounding the weights, but they will be initialized between -1 and
	// 1 by default.

	/**
	 * I need to make sure that when a Network is created, the weights are
	 * created properly with the right size arrays etc.
	 */

	public final double learningRate = .4;

	// I'll use activation (-1,1)

	/*
	 * If we assume that this one will only get called if we're not loading a
	 * network from save, Then we can safely autocreate the weights randomly and
	 * set the biases to 0
	 */
	public Network(int nL, int[] nNs) {
		layers = new Layer[nL];
		numLayers = nL;
		if (nNs.length != nL) {
			System.out
					.println("Dimension mismatch error between Network size and"
							+ "layer size input array");
		}
		for (int i = 0; i < numLayers; i++) {
			layers[i] = new Layer(nNs[i]);
		}
		weights = new double[nL - 1][][];
		for (int i = 0; i < weights.length; i++) {
			double[][] temp = new double[layers[i].activations.length][layers[i + 1].activations.length];
			weights[i] = temp;
		}

		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				for (int k = 0; k < weights[i][j].length; k++) {
					double temp = Math.random();
					temp *= 2;
					temp -= 1;
					weights[i][j][k] = temp;
				}
			}
		}
		for (int i = 0; i < layers.length; i++) {
			for (int j = 0; j < layers[i].biases.length; j++) {
				layers[i].biases[j] = 0;
			}
		}
		int gradientTicker = 0;
		for (int i = 1; i < layers.length; i++) {// Don't count biases of input
													// layer
			gradientTicker += layers[i].biases.length;
			// Number of biases
		}
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				// gradientSize += weights[i][j].length;
				for (int k = 0; k < weights[i][j].length; k++) {
					gradientTicker++;
				}
			}
		}
		gradientSize = gradientTicker;
		summarySize = gradientSize + layers[0].length();
	}

	public Network(double[][][] weight, double[][] bias) {// We can extrapolate
															// number of layers
															// and neurons in
															// layer from
															// properly sized
															// weight array
		// Make sure to include a bit in biases for the input layer even though
		// those
		// don't need it - find some way to fix?
		numLayers = weight.length + 1;
		// System.out.println("Number of layers is "+numLayers);
		if (numLayers != bias.length)
			System.out
					.println("ERROR! DIM MISMATCH IN NETWORK INITIALIZATION!");

		layers = new Layer[numLayers];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Layer(bias[i].length);
			if (i != layers.length - 1) {// weight array is size 1 less than
											// layers
				if (layers[i].activations.length != weight[i].length)
					System.out
							.println("ERROR! DIM MISTMATCH IN NETWORK INITIALIZATION!");
			}

		}
		weights = weight;
		for (int i = 0; i < bias.length; i++) {
			layers[i].biases = bias[i];
		}

		int gradientTicker = 0;
		for (int i = 1; i < layers.length; i++) {// Don't count biases of input
													// layer
			gradientTicker += layers[i].biases.length;
			// Number of biases
		}
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				// gradientSize += weights[i][j].length;
				for (int k = 0; k < weights[i][j].length; k++) {
					gradientTicker++;
				}
			}
		}
		gradientSize = gradientTicker;
		summarySize = gradientSize + layers[0].length();
	}

	public void save(String whichFile) throws IOException {
		FileWriter saveNet = new FileWriter(new File(whichFile));
		PrintWriter output = new PrintWriter(saveNet);
		for (int i = 0; i < layers.length; i++) {
			int b = 0;
			for (b = 0; b < layers[i].length() - 1; b++) {
				output.print(layers[i].biases[b] + " ");
			}
			output.println(layers[i].biases[b]);// This should print the last
												// one - double-check
		}
		output.println("*");
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				for (int k = 0; k < weights[i][j].length - 1; k++) {
					output.print(weights[i][j][k] + " ");
				}
				output.println(weights[i][j][weights[i][j].length - 1]);
			}
			if (i != weights.length - 1)
				output.println("*");
		}

		output.close();
		saveNet.close();
	}

	public static Network load(String whichFile) throws IOException {
		/*
		 * An explanation of the save format I'm using: The biases array is
		 * two-dimensional. The weights array is 3d. So, if I use some delimiter
		 * (in this case, *) between 2d arrays, I can save the settings as:
		 * biases
		 * 
		 * weights[0]
		 * 
		 * weights[1]
		 * 
		 * . . .
		 * 
		 * weights[n]. Within a 2d array, I can separate 1d arrays using line
		 * breaks, so: biases[0] biases[1] . . . biases[n]. And within 1d
		 * arrays, I separate individual elements with spaces, so biases[0][0]
		 * biases[0][1] ... biases[0][n].
		 */
		double[][][] stats;
		// will have length of weights.length + 1
		Scanner load3d = new Scanner(new File(whichFile));
		// load3d.useDelimiter("*");//Will this work?
		ArrayList<String> loadLines = new ArrayList<String>();
		int maxIndx0 = -1;
		while (load3d.hasNextLine()) {
			loadLines.add(load3d.nextLine());
			maxIndx0++;
		}
		// System.out.println("maxIndx0 = "+maxIndx0);
		StringBuffer sb = new StringBuffer();
		ArrayList<String> twoDArrays = new ArrayList<String>();
		int maxIndx = -1;// will equal number of 2d arrays minus 1, so is index
		for (int i = 0; i <= maxIndx0; i++) {
			// System.out.println("Working on line index "+i);
			String two = "x";
			String one = loadLines.get(i);
			if (i != maxIndx0)
				two = loadLines.get(i + 1);
			if (!one.equals("*")) {
				sb.append(one);
				if (two.equals("*") || (two.equals("x"))) {
					twoDArrays.add(sb.toString());
					sb = new StringBuffer();
					maxIndx++;
				} else {
					sb.append("\n");
				}
			}
		}
		// System.out.println("maxIndx = "+maxIndx);

		// int maxIndx = -1;
		// ArrayList<String> twoDArrays = new ArrayList<String>();
		/*
		 * while(load3d.hasNext()) { twoDArrays.add(load3d.next()); maxIndx++;
		 * System.out.println("twoDArrays index "+maxIndx+" is:");
		 * System.out.println(twoDArrays.get(maxIndx)); }
		 */
		load3d.close();

		/*
		 * for(int i = 0; i <= maxIndx; i++) {
		 * System.out.println("twoDArrays index "+i+":");
		 * System.out.println(twoDArrays.get(i)); }
		 */

		stats = new double[maxIndx + 1][][];
		String[] temp = new String[maxIndx + 1];
		for (int i = 0; i <= maxIndx; i++) {
			temp[i] = twoDArrays.get(i);
			Scanner load2d = new Scanner(temp[i]);// Error thrown
													// here!!!!/**Error thrown
													// here!*/
			int maxIndx2 = -1;
			ArrayList<String> oneDArrays = new ArrayList<String>();
			while (load2d.hasNextLine()) {
				oneDArrays.add(load2d.nextLine());
				maxIndx2++;
			}
			load2d.close();
			stats[i] = new double[maxIndx2 + 1][];
			String[] temp2 = new String[maxIndx2 + 1];
			for (int j = 0; j <= maxIndx2; j++) {
				temp2[j] = oneDArrays.get(j);
				Scanner load1d = new Scanner(temp2[j]);
				int maxIndx3 = -1;
				ArrayList<Double> elements = new ArrayList<Double>();
				while (load1d.hasNextDouble()) {
					elements.add((Double) load1d.nextDouble());
					maxIndx3++;
				}
				load1d.close();
				double[] tempStats = new double[maxIndx3 + 1];
				for (int k = 0; k <= maxIndx3; k++) {
					tempStats[k] = elements.get(k);
				}
				stats[i][j] = tempStats;
			}
		}

		double[][][] weightsX = new double[stats.length - 1][][];
		for (int i = 1; i < stats.length; i++) {
			weightsX[i - 1] = stats[i];
		}
		/*
		 * System.out.println("Stats:"); for(int i = 0; i < stats.length; i++) {
		 * System.out.println("i = "+i); for(int j = 0; j < stats[i].length;
		 * j++) { System.out.println("j = "+j); for(int k = 0; k <
		 * stats[i][j].length; k++) { System.out.println("k = "+k);
		 * System.out.println(stats[i][j][k]); } } }
		 */

		// Temporary return statement
		// double[][][] weights2 = {{{0}}};
		// double[][] biases2 = {{0}, {0}};
		// System.out.println("Reached everything but return statement successfully");
		return new Network(weightsX, stats[0]);
	}

	public void printFeedForward(double[] inputs) {
		/**
		 * System.out.println("inputs: "); for(int i = 0; i < inputs.length;
		 * i++) { System.out.println(inputs[i]); }
		 */
		double[] temp = feedForward(inputs);

		System.out.println("outputs: ");
		for (int i = 0; i < temp.length; i++) {
			System.out.println(temp[i]);
		}
	}

	/*
     * 
     */
	public double[] feedForward(double[] inputs) {
		layers[0].setActivations(inputs);
		// System.out.print("Activations for layer 0 are now:");
		// layers[0].print();
		double[] tempActs;
		for (int k = 1; k < layers.length; k++) {
			// System.out.println("\nFeedforward operating on layer "+k);
			tempActs = new double[layers[k].activations.length];
			for (int i = 0; i < tempActs.length; i++) {
				double sum = weightSum(k, i);
				double squished = squishy(sum);
				tempActs[i] = squished;
			}
			layers[k].setActivations(tempActs);
			// System.out.print("Activations for layer "+k+" are now:");
			// layers[k].print();
		}

		tempActs = new double[layers[layers.length - 1].activations.length];
		for (int i = 0; i < layers[layers.length - 1].length(); i++) {
			tempActs[i] = layers[layers.length - 1].activations[i];
		}
		return tempActs;
		// To find output in digit recognition; implement this in separate
		// method late
		/*
		 * double bestAct = 0; int bestFitNeuronIndx = 0; for(int i = 0; i <
		 * layers[layers.length - 1].activations.length; i++) {
		 * if(layers[layers.length - 1].activations[i] > bestAct) { bestAct =
		 * layers[layers.length - 1].activations[i]; bestFitNeuronIndx = i; } }
		 * output = bestFitNeuronIndx;
		 */
	}

	public int outputForward(double[] inputs) {
		double[] outs = feedForward(inputs);

		int bestGuess = 0;
		double maxGuess = 0;
		for (int j = 0; j < 10; j++) {
			if (outs[j] > maxGuess) {
				maxGuess = outs[j];
				bestGuess = j;
			}
		}

		return bestGuess;
	}

	public int outputForward(double[][] image) {
		int oneDSize = image.length * image[0].length;// Assumes image is
														// rectangular
		int ticker = 0;
		double[] oneDInputs = new double[oneDSize];
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				oneDInputs[ticker++] = image[i][j];
			}
		}

		return outputForward(oneDInputs);
	}

	/*
	 * This method takes and returns the whatever-dimensions gradient vector
	 * (with each weight and bias as a component) over all training examples in
	 * a batch times -k, where k is the learning rate. It will also apply said
	 * backpropagation.
	 * 
	 * Parameter trainingOuts: This is an array of expected values for
	 * trainings. First index is which training batch (for stochastic) we're
	 * doing, second index is which training example we're looking at, third is
	 * the index indicating which expected value we're looking at. trainingIns
	 * is similar, with the first index which training batch, second index being
	 * which training we observe, and the third index being which input neuron
	 * we're taking the value of.
	 */
	public double[] backPropagate(double[][][] trainingIns,
			double[][][] trainingOuts, boolean stochastic, boolean print,
			int startBatch) {// Option for stochastic or not?
		// Put in parameter String savePath?
		// I'm not sure the stochastic option is doing what I think it is.
		// System.out.println("Backpropagation called");
		double[] gradient = new double[gradientSize];
		/*
		 * The gradient will be all biases first - input, then first hidden,
		 * then second hidden, etc. Then all weights - first in order of layers,
		 * then in order of first neuron, then in order of second neuron -
		 * basically, in the order of the weights array.
		 */

		if (stochastic) {
			for (int batch = startBatch; batch < trainingIns.length; batch++) {
				for (int i = 0; i < trainingOuts.length; i++) {
					gradient[i] = 0;
				}
				for (int t = 0; t < trainingIns[batch].length; t++) {
					if (!print)
						feedForward(trainingIns[batch][t]);
					else {
						printFeedForward(trainingIns[batch][t]);// necessary so
																// that we're
																// measuring the
																// correct cost
						System.out.println("Cost for this training example is "
								+ calcCost(trainingIns[batch][t],
										trainingOuts[batch][t]));
					}
					int indx = 0;
					// System.out.println("Currently on gradient index 0");
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						// Don't count biases of input layer, start at 1
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							// System.out.println("b = "+b);
							gradient[indx] += dC0db(l, b,
									trainingOuts[batch][t]);
							// System.out.println("Added dC0db "+gradient[indx]+" to gradient at index "+indx);
							indx++;
							// System.out.println("Currently on gradient index "+indx);
						}
					}
					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i = "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j = "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k = "+k);
								gradient[indx] += dC0dw(i, j, k,
										trainingOuts[batch][t]);
								// System.out.println("Added dC0dw "+gradient[indx]+" to gradient at indx "+indx);
								indx++;
								// System.out.println("Currently on gradient index "+indx);
							}
						}
					}
				}
				for (int i = 0; i < gradient.length; i++) {
					gradient[i] /= trainingOuts.length;
					gradient[i] *= -1 * learningRate;
				}
				int indx = 0;
				for (int l = 1; l < layers.length; l++) {// which layer we are
															// observing, for
															// biases
					for (int b = 0; b < layers[l].activations.length; b++) {// which
																			// neuron
																			// -
																			// and
																			// hence,
																			// which
																			// bias
																			// -
																			// we
																			// calculate
						layers[l].biases[b] += gradient[indx++];
					}
				}
				for (int i = 0; i < weights.length; i++) {
					// System.out.println("i is "+i);
					for (int j = 0; j < weights[i].length; j++) {
						// System.out.println("j is "+j);
						for (int k = 0; k < weights[i][j].length; k++) {
							// System.out.println("k is "+k);
							weights[i][j][k] += gradient[indx];
							indx++;
						}
					}
				}
			}
		} else {
			for (int batch = 0; batch < trainingIns.length; batch++) {
				for (int t = 0; t < trainingIns[batch].length; t++) {
					for (int i = 0; i < trainingOuts.length; i++) {
						gradient[i] = 0;
					}
					if (!print)
						feedForward(trainingIns[batch][t]);
					else {
						printFeedForward(trainingIns[batch][t]);// necessary so
																// that we're
																// measuring the
																// correct cost
						System.out.println("Cost for this training example is "
								+ calcCost(trainingIns[batch][t],
										trainingOuts[batch][t]));
					}

					int indx = 0;
					// System.out.println("Currently on gradient index 0");
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						// Don't count biases of input layer, start at 1
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							// System.out.println("b = "+b);
							gradient[indx] += dC0db(l, b,
									trainingOuts[batch][t]);
							// System.out.println("Added dC0db "+gradient[indx]+" to gradient at index "+indx);
							indx++;
							// System.out.println("Currently on gradient index "+indx);
						}
					}

					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i = "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j = "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k = "+k);
								gradient[indx] += dC0dw(i, j, k,
										trainingOuts[batch][t]);
								// System.out.println("Added dC0dw "+gradient[indx]+" to gradient at indx "+indx);
								indx++;
								// System.out.println("Currently on gradient index "+indx);
							}
						}
					}

					// Apply changes
					for (int i = 0; i < gradient.length; i++) {
						gradient[i] *= -1 * learningRate;
					}
					int count = 0;
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							layers[l].biases[b] += gradient[count++];
						}
					}
					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i is "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j is "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k is "+k);
								weights[i][j][k] += gradient[count];
								count++;

							}
						}
					}
				}
			}
		}

		return gradient;
	}

	public double[] backPropagate(double[][][] trainingIns,
			double[][][] trainingOuts, boolean stochastic, boolean print,
			int startBatch, String savePath) throws IOException {
		// Put in parameter String savePath?
		// I'm not sure the stochastic option is doing what I think it is.
		// System.out.println("Backpropagation called");
		double[] gradient = new double[gradientSize];
		/*
		 * The gradient will be all biases first - input, then first hidden,
		 * then second hidden, etc. Then all weights - first in order of layers,
		 * then in order of first neuron, then in order of second neuron -
		 * basically, in the order of the weights array.
		 */

		if (stochastic) {
			for (int batch = 0; batch < trainingIns.length; batch++) {
				System.out.println("Working on batch " + batch);
				for (int i = 0; i < trainingOuts.length; i++) {
					gradient[i] = 0;
				}
				for (int t = 0; t < trainingIns[batch].length; t++) {
					if (!print)
						feedForward(trainingIns[batch][t]);
					else {
						printFeedForward(trainingIns[batch][t]);// necessary so
																// that we're
																// measuring the
																// correct cost
						System.out.println("Cost for this training example is "
								+ calcCost(trainingIns[batch][t],
										trainingOuts[batch][t]));
					}
					int indx = 0;
					// System.out.println("Currently on gradient index 0");
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						// Don't count biases of input layer, start at 1
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							// System.out.println("b = "+b);
							gradient[indx] += dC0db(l, b,
									trainingOuts[batch][t]);
							// System.out.println("Added dC0db "+gradient[indx]+" to gradient at index "+indx);
							indx++;
							// System.out.println("Currently on gradient index "+indx);
						}
					}
					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i = "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j = "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k = "+k);
								gradient[indx] += dC0dw(i, j, k,
										trainingOuts[batch][t]);
								// System.out.println("Added dC0dw "+gradient[indx]+" to gradient at indx "+indx);
								indx++;
								// System.out.println("Currently on gradient index "+indx);
							}
						}
					}
				}
				for (int i = 0; i < gradient.length; i++) {
					gradient[i] /= trainingOuts.length;
					gradient[i] *= -1 * learningRate;
				}
				int indx = 0;
				for (int l = 1; l < layers.length; l++) {// which layer we are
															// observing, for
															// biases
					for (int b = 0; b < layers[l].activations.length; b++) {// which
																			// neuron
																			// -
																			// and
																			// hence,
																			// which
																			// bias
																			// -
																			// we
																			// calculate
						layers[l].biases[b] += gradient[indx++];
					}
				}
				for (int i = 0; i < weights.length; i++) {
					// System.out.println("i is "+i);
					for (int j = 0; j < weights[i].length; j++) {
						// System.out.println("j is "+j);
						for (int k = 0; k < weights[i][j].length; k++) {
							// System.out.println("k is "+k);
							weights[i][j][k] += gradient[indx];
							indx++;
						}
					}
				}
				save(savePath);
			}
		} else {
			for (int batch = 0; batch < trainingIns.length; batch++) {
				System.out.println("Working on batch " + batch);
				for (int t = 0; t < trainingIns[batch].length; t++) {
					for (int i = 0; i < trainingOuts.length; i++) {
						gradient[i] = 0;
					}
					if (!print)
						feedForward(trainingIns[batch][t]);
					else {
						printFeedForward(trainingIns[batch][t]);// necessary so
																// that we're
																// measuring the
																// correct cost
						System.out.println("Cost for this training example is "
								+ calcCost(trainingIns[batch][t],
										trainingOuts[batch][t]));
					}

					int indx = 0;
					// System.out.println("Currently on gradient index 0");
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						// Don't count biases of input layer, start at 1
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							// System.out.println("b = "+b);
							gradient[indx] += dC0db(l, b,
									trainingOuts[batch][t]);
							// System.out.println("Added dC0db "+gradient[indx]+" to gradient at index "+indx);
							indx++;
							// System.out.println("Currently on gradient index "+indx);
						}
					}

					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i = "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j = "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k = "+k);
								gradient[indx] += dC0dw(i, j, k,
										trainingOuts[batch][t]);
								// System.out.println("Added dC0dw "+gradient[indx]+" to gradient at indx "+indx);
								indx++;
								// System.out.println("Currently on gradient index "+indx);
							}
						}
					}

					// Apply changes
					for (int i = 0; i < gradient.length; i++) {
						gradient[i] *= -1 * learningRate;
					}
					int count = 0;
					for (int l = 1; l < layers.length; l++) {// which layer we
																// are
																// observing,
																// for biases
						for (int b = 0; b < layers[l].activations.length; b++) {// which
																				// neuron
																				// -
																				// and
																				// hence,
																				// which
																				// bias
																				// -
																				// we
																				// calculate
							layers[l].biases[b] += gradient[count++];
						}
					}
					for (int i = 0; i < weights.length; i++) {
						// System.out.println("i is "+i);
						for (int j = 0; j < weights[i].length; j++) {
							// System.out.println("j is "+j);
							for (int k = 0; k < weights[i][j].length; k++) {
								// System.out.println("k is "+k);
								weights[i][j][k] += gradient[count];
								count++;

							}
						}
					}
					save(savePath);
				}
			}
		}

		return gradient;
	}

	public void printBackPropagate(double[][][] trainingIns,
			double[][][] trainingOuts) {// Must be stochastic so we're looking
										// at the average changes
		double[] changes = backPropagate(trainingIns, trainingOuts, true, true,
				0);
		for (int i = 0; i < changes.length; i++) {
			System.out.println(changes[i]);
		}
	}

	/*
	 * returns the derivative of the cost of one training example with respect
	 * to one weight
	 * 
	 * Preconditions: 0 < layer < layers.length expected.length == # neurons in
	 * last layer
	 * 
	 * Parameters: Same notation as in layers array, in same order, for first 3.
	 * expected is the array of expected activations
	 */
	public double dC0dw(int layer, int start, int end, double[] expected) {
		/*
		 * System.out.println("dC0dw called connecting neuron "+start+" in layer "
		 * +layer+" and neuron "+end+" in layer "+(layer+1));
		 */
		// System.out.println(" with expected  value "+expected[0]);
		double dzdw = layers[layer].activations[start];// Used to be layer-1. Is
														// that right?
		// System.out.println("dzdw for weight = "+dzdw);
		final double e = Math.E;
		double temp = Math.pow(e, weightSum(layer + 1, end));
		double temp2 = temp + 1;
		temp2 *= temp2;
		temp /= temp2;
		double dadz = temp;
		// System.out.println("dadz for weight = "+dadz);

		// System.out.println("calling dcda from dC0dw;");
		double dCda = dC0da(layer + 1, end, expected);// used to be layer,
														// start, expected.
		// System.out.println("dCda for weight = "+dCda);
		// System.out.println("dC0dw = "+(dzdw * dadz * dCda));
		// System.out.println("dC0dw finished \n");
		return dzdw * dadz * dCda;
	}

	/*
	 * returns the derivative of the cost of one training example with respect
	 * to one bias
	 */
	public double dC0db(int layyer, int whichInLyr, double[] expected) {
		// System.out.println("dC0db called for layer "+layyer+" and neuron "+whichInLyr);
		final double e = Math.E;
		double temp = Math.pow(e, weightSum(layyer, whichInLyr));
		double temp2 = temp + 1;
		temp2 *= temp2;
		temp /= temp2;
		double dadz = temp;
		// System.out.println("dadz for bias = "+dadz);

		// System.out.println("calling dcda from dC0db;");
		double dCda = dC0da(layyer, whichInLyr, expected);
		// System.out.println("dCda for bias = "+dCda);
		// System.out.println("dC0db finished\n");
		return dadz * dCda;
	}

	/*
	 * Should I make the bounds (multiplier on weighted sum x) correspond to
	 * something about the layer? Speed up learning
	 * 
	 * Parameter weightedSum includes bias
	 */
	public static double squishy(double weightedSum) {
		// System.out.print("Squish "+weightedSum+" to make ");
		double temp = Math.pow(Math.E, weightedSum);
		temp += 1;
		temp = -1 / temp;
		temp += 1;
		// System.out.println(temp);
		return temp;
	}

	/*
	 * Parameters: neurIndx is the neuron in that layer layer is the index of
	 * the layer - ranges from 0 to layers.length-1
	 */
	private double weightSum(int layer, int neurIndx) {// All the [layer]s here
														// used to be [layer-1].
		// System.out.println("Method weightSum called with 'layer' = "+layer+" and 'neurIndx' = "+neurIndx);
		double sum = 0;
		for (int j = 0; j < layers[layer - 1].activations.length; j++) {// the
																		// index
																		// of
																		// the
																		// neuron
																		// in
																		// the
																		// previous
																		// layer
																		// that
																		// we're
																		// feeding
																		// in
			double act = layers[layer - 1].activations[j];
			// System.out.println("activation of neuron "+j+" in layer "+layer+" is "+act);
			double weight = weights[layer - 1][j][neurIndx];
			sum += act * weight;
			// System.out.print("j = "+j+" and sum = ");//
			// System.out.println(sum);//
		}
		sum += layers[layer].biases[neurIndx];
		// System.out.println("Weighted sum is "+sum);
		return sum;
	}

	public double dC0da(int lyr, int neurInLyr, double[] xpected) {
		// System.out.println("dC0da called for layer "+lyr+" and neuron "+neurInLyr);
		double sum = 0;
		if (lyr == layers.length - 1) {
			// System.out.println("Looking at last layer for dC0da");
			sum = layers[lyr].activations[neurInLyr] - xpected[neurInLyr];
			sum *= 2;
			// System.out.println("effect of activation of neuron "+neurInLyr+" in layer "+lyr+" on Cost is "+sum);
			return sum;
		} else {
			// System.out.println("Looking at not last layer for dC0da");
			// q is the neuron in the "next" layer - the one after "layer"
			for (int q = 0; q < layers[lyr + 1].activations.length; q++) {
				double dzda_2 = weights[lyr][neurInLyr][q];// Correct?

				double temp = Math.pow(Math.E, weightSum(lyr + 1, q));// Used to
																		// be
																		// lyr
				double temp2 = temp + 1;
				temp2 *= temp2;
				temp /= temp2;
				double dadz_2 = temp;

				double dCda_2 = dC0da(lyr + 1, q, xpected);// Should that be q?
															// I think so

				sum += dzda_2 * dadz_2 * dCda_2;
			}
			// System.out.println("dC0da finished\n");
			return sum;
		}
	}

	public void printWeights() {
		for (int i = 0; i < weights.length; i++) {
			System.out.println("");
			System.out.println("Weights between layer " + i + " and " + (i + 1)
					+ ":");
			for (int j = 0; j < weights[i].length; j++) {
				for (int k = 0; k < weights[i][j].length; k++) {
					System.out.print("Weight from neuron " + j + " in layer "
							+ i + " to neuron " + k + " in layer " + (i + 1)
							+ ": ");
					System.out.println(weights[i][j][k]);
				}
			}
		}
	}

	public double calcCost(double[] trainingIns, double[] trainingOuts) {
		double[] outs = feedForward(trainingIns);
		/*
		 * System.out.println("outs:"); for(int i = 0; i < outs.length; i++) {
		 * System.out.println(outs[i]); }
		 */
		double sum = 0;
		for (int i = 0; i < outs.length; i++) {
			// System.out.println("i = "+i);
			double diff = outs[i] - trainingOuts[i];
			sum += Math.pow(diff, 2);
		}
		return sum;
	}

	public double calcAvgCost(double[][] trainingIns, double[][] trainingOuts) {
		double[] costs = new double[trainingOuts.length];
		for (int i = 0; i < trainingOuts.length; i++) {
			costs[i] = calcCost(trainingIns[i], trainingOuts[i]);
		}
		double sum = 0;
		for (int i = 0; i < costs.length; i++) {
			sum += costs[i];
		}
		sum /= costs.length;
		return sum;
	}

	public double[] summary() {
		// System.out.println(summarySize);
		double[] temp = new double[summarySize];
		int indx = 0;
		for (int i = 0; i < layers.length; i++) {
			for (int j = 0; j < layers[i].length(); j++) {
				// System.out.println(indx);
				temp[indx++] = layers[i].biases[j];
			}
		}
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				for (int k = 0; k < weights[i][j].length; k++) {
					// System.out.println(indx);
					temp[indx++] = weights[i][j][k];
				}
			}
		}

		return temp;
	}

	public void print() {
		for (int i = 0; i < layers.length; i++) {
			for (int j = 0; j < layers[i].length(); j++) {
				System.out.println("Bias for neuron " + j + " in layer " + i
						+ ": " + layers[i].biases[j]);
			}
		}
		printWeights();
	}
}