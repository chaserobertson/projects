from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .matrix import Matrix
import numpy as np
import csv


class PerceptronLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.weights = []
        self.bias = 1.0
        self.learning_rate = 0.1
        self.accurate_within = 0.01
        self.buffer_epochs = 5
        pass

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

        # initialize weight vector, +1 for bias
        for i in range(features.cols + 1):
            self.weights.append(0)

        with open('voting.csv', 'w') as csvfile:
            writer = csv.writer(csvfile)
            # epochs
            i = 0
            accuracy = self.accurate_within
            last_accuracies = np.zeros(self.buffer_epochs)
            while accuracy >= (np.mean(last_accuracies) + self.accurate_within):
                accuracies = []
                # iterate over each row, train weights
                for j in range(features.rows):
                    row = features.row(j)
                    net = np.dot(row, self.weights)
                    output = float(net > 0)
                    target = labels.row(j)[0]
                    accuracies.append(1 - abs(target - output))
                    if output != target:
                        self.calc_dw(target, output, row)
                accuracy = np.mean(accuracies)

                last_accuracies[i % self.buffer_epochs] = accuracy
                i += 1
                # writer.writerow(['', i, accuracy])
                features.shuffle(labels)
            # writer.writerow(['weights'])
            # writer.writerow(self.weights)

        # with open('test2.csv', 'w') as csvfile:
        #     writer = csv.writer(csvfile)
        #     writer.writerow(['weights'])
        #     writer.writerow(self.weights)
        #     writer.writerow('')
        #     writer.writerow(['features'])
        #     for row in range(features.rows):
        #         features.row(row).append(labels.get(row, 0))
        #         writer.writerow(features.row(row))

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]
        if len(features) != len(self.weights):
            features.append(self.bias)
        output = int(np.dot(features, self.weights) > 0)
        labels.append(output)

    def calc_dw(self, tar, net, x):
        y = np.zeros(len(x))
        for i in range(len(x)):
            y[i] = (self.learning_rate * (tar - net) * x[i])
        self.weights = np.add(self.weights, y)
