from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .matrix import Matrix
import numpy as np
import random


class BackpropLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.l_rate = 0.1
        self.momentum = 0.0
        self.features = []
        self.network = []
        self.one_hot = []
        self.feats_to_use = []
        pass

    # bias weight must be the last e.g. weights[-1]
    # output layer must be the last

    def initialize_network(self, n_inputs, n_hidden, n_outputs):
        hidden_layer = [{'weights': [random.uniform(-0.1, 0.1) for i in range(n_inputs)] + [1],
                         'pastDw': [0.0 for i in range(n_inputs)] + [0.0]}
                        for i in range(n_hidden)]
        self.network.append(hidden_layer)
        output_layer = [{'weights': [random.uniform(-0.1, 0.1) for i in range(n_hidden)] + [1],
                         'pastDw': [0.0 for i in range(n_hidden)] + [0.0]}
                        for i in range(n_outputs)]
        self.network.append(output_layer)

    # Calculate neuron activation for an input
    def activate(self, weights, inputs):
        activation = weights[-1]
        for i in range(len(weights) - 1):
            activation += weights[i] * inputs[i]
        return activation

    # Transfer neuron activation
    def transfer(self, activation):
        return 1.0 / (1.0 + np.exp(-activation))

    # Forward propagate input to a network output
    def forward_propagate(self, inputs):
        for layer in self.network:
            new_inputs = []
            for neuron in layer:
                activation = self.activate(neuron['weights'], inputs)
                neuron['output'] = self.transfer(activation)
                new_inputs.append(neuron['output'])
            inputs = new_inputs
        return inputs

    # Calculate the derivative of an neuron output
    def transfer_derivative(self, output):
        return output * (1.0 - output)

    # Backpropagate error and store in neurons
    def backward_propagate_error(self, expected):
        for i in reversed(range(len(self.network))):    # from output layer to input(backwards)
            layer = self.network[i]
            errors = list()
            if i != len(self.network) - 1:              # if not input layer
                for j in range(len(layer)):
                    error = 0.0
                    for neuron in self.network[i + 1]:
                        error += (neuron['weights'][j] * neuron['delta'])
                    errors.append(error)
            else:                                       # if input layer
                for j in range(len(layer)):
                    neuron = layer[j]
                    errors.append(expected[j] - neuron['output'])
            for j in range(len(layer)):
                neuron = layer[j]
                neuron['delta'] = errors[j] * self.transfer_derivative(neuron['output'])

    # Update network weights with error
    def update_weights(self, row):
        for i in range(len(self.network)):
            inputs = row[:]
            if i != 0:
                inputs = [neuron['output'] for neuron in self.network[i - 1]]
            for neuron in self.network[i]:
                for j in range(len(inputs)):
                    cur_dw = self.l_rate * neuron['delta'] * inputs[j]
                    past_dw = self.momentum * neuron['pastDw'][j]
                    neuron['weights'][j] += cur_dw + past_dw
                    neuron['pastDw'][j] = cur_dw + past_dw
                cur_dw = self.l_rate * neuron['delta']
                past_dw = self.momentum * neuron['pastDw'][-1]
                neuron['weights'][-1] += (cur_dw + past_dw)
                neuron['pastDw'][-1] = (cur_dw + past_dw)

    def train(self, features, labels):
        """
        :type features: Matrix
        :type labels: Matrix
        """
        # extract set of labels, add bias to input rows
        self.labels = []
        for i in range(labels.rows):
            self.labels.append(labels.get(i, 0))
        self.labels = list(set(self.labels))

        # create one-hot encoding of classifiers
        for l in range(len(self.labels)):
            code = [0 for i in range(len(self.labels))]
            code[l] = 1
            self.one_hot.append(code)

        # ignore unimportant features
        self.feats_to_use = range(features.cols)
        self.feats_to_use.remove(0)

        self.initialize_network(len(self.feats_to_use), len(self.feats_to_use) * 2, len(self.labels))

        n = features.rows
        m = int(n / 8)
        t = n - m

        bssf = 1
        since_bssf = 0
        bssf_net = self.network
        training = True
        epoch = 1
        while training:
            features.shuffle(labels)
            for r in range(t):
                row = [features.get(r, i) for i in self.feats_to_use]
                expected = self.one_hot[int(labels.row(r)[0])]
                self.forward_propagate(row)
                self.backward_propagate_error(expected)
                self.update_weights(row)

            vs_mse, vs_class = self.get_mse(features, labels, range(t, n))

            if vs_mse < bssf:
                bssf = vs_mse
                bssf_net = self.network
                since_bssf = 0
            else:
                since_bssf += 1

            if since_bssf >= 10:
                training = False
            epoch += 1

        # end training, reset weights to bssf
        self.network = bssf_net
        print('Learning rate: %f' % self.l_rate)
        print('Number of epochs: %d' % epoch)
        vs_mse = self.get_mse(features, labels, range(t, n))
        ts_mse = self.get_mse(features, labels, range(1, t))
        print('Training set MSE: %f' % ts_mse[0])
        print('Validation set MSE: %f' % vs_mse[0])

    def print_weights(self):
        print('Weights:')
        for layer in self.network:
            for neuron in layer:
                for w in neuron['weights']:
                    print('%.11f' % w)
            print()

    def get_mse(self, features, labels, indices):
        mse = 0
        matches = 0
        for r in indices:
            label = []
            row = [features.get(r, i) for i in self.feats_to_use]
            targ = labels.row(r)[0]
            err, match = self.my_predict(row, label, targ)
            mse += err
            matches += match
        mse = mse / len(indices)
        matches = matches / len(indices)
        return mse, matches

    def test_mse(self, features, labels):
        return self.get_mse(features, labels, range(features.rows))

    def my_predict(self, features, labels, target):
        del labels[:]
        outputs = self.forward_propagate(features)
        actual_output = outputs.index(max(outputs))
        labels.append(actual_output)
        err = np.sum(np.power(np.subtract(self.one_hot[int(target)], outputs), 2))
        return err, actual_output == target

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]

        outputs = self.forward_propagate([features[i] for i in self.feats_to_use])
        actual_output = outputs.index(max(outputs))
        labels.append(actual_output)
