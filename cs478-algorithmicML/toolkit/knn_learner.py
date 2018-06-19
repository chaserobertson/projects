from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .matrix import Matrix
import numpy as np
import scipy.stats as stats


class InstanceBasedLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.k = 3
        self.classify = False
        self.distance_weighting = True
        self.features = None
        pass

    def train(self, features, labels):
        self.__init__()
        """
        :type features: Matrix
        :type labels: Matrix
        """
        # fill numpy arrays with all training features and labels
        self.features = np.zeros((features.rows, features.cols))
        self.labels = np.zeros((labels.rows, labels.cols))
        for i in range(features.rows):
            self.features[i] = features.row(i)
            self.labels[i] = labels.row(i)

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]
        dists = np.sum(np.abs(self.features - features), axis=1)
        sorted_inds = np.argsort(dists)
        nearest_vals = self.labels[sorted_inds[range(self.k)]]
        nearest_dists = dists[sorted_inds[range(self.k)]]
        predict = self.mode(nearest_vals, nearest_dists)

        labels += [predict]

    def test_mse(self, features, labels):
        pass

    def mode(self, vals, dists):
        if self.classify:
            if self.distance_weighting:
                votes = {}
                for i in range(self.k):
                    votes[vals.flatten()[i]] = 0
                for i in range(self.k):
                    votes[vals.flatten()[i]] += 1 / np.square(dists.flatten()[i])
                return votes.keys()[votes.values().index(max(votes.values()))]
            else:
                return stats.mode(vals)[0]
        else:
            if self.distance_weighting:
                return np.average(vals.flatten(), weights=dists)
            else:
                return np.mean(vals)
