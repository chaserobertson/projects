from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .perceptron_learner import PerceptronLearner
from .matrix import Matrix
import numpy as np
import copy


class MultiplePerceptronLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.perceptrons = []
        self.bias = 1.0
        pass

    def train(self, features, labels):
        """
        :type features: Matrix
        :type labels: Matrix
        """
        # extract set of labels
        self.labels = []
        for i in range(labels.rows):
            self.labels.append(labels.get(i, 0))
            features.row(i).append(self.bias)
        self.labels = list(set(self.labels))
        # print('multiple perceptron labels: '+str(self.labels))

        if len(self.labels) < 3:
            self.perceptrons.append(PerceptronLearner())
            return self.perceptrons[0].train(features, labels)
        else:
            # multiple perceptrons, one for each label
            for i in range(len(self.labels)):
                new_labels = copy.deepcopy(labels)
                col = np.array(labels.col(0))
                new_label_ind = np.nonzero(col == self.labels[i])

                # print(new_labels.col(0))
                for j in range(len(col)):
                    if j in new_label_ind[0]:
                        new_labels.set(j, 0, 1.0)
                    else:
                        new_labels.set(j, 0, 0.0)

                # print(new_labels.col(0))
                self.perceptrons.append(PerceptronLearner())
                self.perceptrons[i].train(features, new_labels)

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]
        # print(features)
        if len(self.perceptrons) == 1:
            return self.perceptrons[0].predict(features, labels)
        else:
            local_labels = []
            for i in range(len(self.perceptrons)):
                del labels[:]
                self.perceptrons[i].predict(features, labels)
                local_labels.append(copy.deepcopy(labels))
            # labels = local_labels
