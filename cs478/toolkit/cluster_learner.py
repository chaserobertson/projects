from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .matrix import Matrix
import numpy as np
from collections import Counter


class ClusterLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.k = 0
        self.centroids = []
        self.clusters = []
        self.assignments = []
        self.data = None
        self.sse = [0 for k in range(self.k)]
        pass

    def init(self):
        self.centroids = []
        self.clusters = []
        self.assignments = []
        self.data = None
        self.sse = [0 for k in range(self.k)]

    def train(self, features, labels):
        self.init()
        """
        :type features: Matrix
        :type labels: Matrix
        """
        features.shuffle()
        self.data = features

        for i in range(self.data.rows):
            self.assignments.append(-1)

        i = 0
        sse = 9999999999
        last_sse = float('infinity')
        while (last_sse - sse) != 0:
            self.sse = [0 for k in range(self.k)]
            last_sse = sse
            sse = 0
            i += 1
            # print('***************\nIteration '+str(i)+'\n***************')
            self.updateCentroids()
            for j in range(self.data.rows):
                instance = self.data.row(j)
                self.assignments[j], sse = self.assign(instance, sse)
            # self.printCentroids()
            # self.printAssigns()
            # print('SSE %.3f' % np.sum(self.sse))
        print('Number of clusters: '+str(self.k), '\n')
        self.printCentroids()
        self.printDetails()
        print('\nTotal SSE: %.3f\n' % sse)
        print('Number of iterations: %d\n' % i)
        # print('Silhouette: %.3f' % self.calcSilhouette())

    def my_train(self, data, k):
        self.k = k
        self.train(data, Matrix())
        return self.calcSilhouette()

    def calcSilhouette(self):
        cluster_sils = []
        for cluster in self.clusters:
            inst_sils = []
            for inst in cluster:
                sib_sils = []
                for i in cluster:
                    sib_sils.append(self.instDist(inst, i))
                a = np.average(sib_sils)
                cous_sils = []
                for cous in self.clusters:
                    if cluster != cous:
                        sils = []
                        for i in cous:
                            sils.append(self.instDist(inst, i))
                        cous_sils.append(np.average(sils))
                b = np.min(cous_sils)
                inst_sils.append((b - a) / np.max([a, b]))
            cluster_sils.append(np.average(inst_sils))
        return np.average(cluster_sils)

    def instDist(self, a, b):
        dist = 0
        for i in range(len(a)):
            x = a[i]
            y = b[i]
            tipo = self.data.attr_type(i)
            if tipo == 'real' or tipo == 'continuous':
                if np.isposinf(x) or np.isposinf(y):
                    dist += 1
                else:
                    dist += (x - y) * (x - y)
            elif tipo == 'nominal':
                if np.isposinf(x) or np.isposinf(y):
                    dist += 1
                elif x == y:
                    dist += 0
                else:
                    dist += 1
            else:
                raise Exception('attribute neither real nor nominal: %s' % tipo)
        return dist

    def assign(self, instance, sse):
        dists = []
        for c in range(len(self.centroids)):
            centroid = self.centroids[c]
            dists.append(self.instDist(instance, centroid))
        sse += np.min(dists)
        self.sse[np.argmin(dists)] += np.min(dists)
        return np.argmin(dists), sse

    def updateCentroids(self):
        # initialize centroids as first items in data set
        if len(self.centroids) == 0:
            for i in range(self.k):
                self.centroids.append(self.data.row(i))
        else:
            self.clusters = [[] for i in range(self.k)]
            for i in range(len(self.assignments)):
                a = self.assignments[i]
                self.clusters[a].append(self.data.row(i))

            for i in range(len(self.clusters)):
                cluster = np.array(self.clusters[i])
                if len(cluster) == 0:
                    continue
                centroid = []
                for j in range(self.data.cols):
                    tipo = self.data.attr_type(j)
                    if tipo == 'real' or tipo == 'continuous':
                        centroid.append(self.mean(cluster[:, j]))
                    elif tipo == 'nominal':
                        centroid.append(self.mode(cluster[:, j]))
                    else:
                        raise Exception('attribute neither real nor nominal: %s' % tipo)
                self.centroids[i] = centroid

    def mean(self, vals):
        sum = 0
        numVals = 0
        for val in vals:
            if not np.isposinf(val):
                sum += val
                numVals += 1
        if numVals == 0:
            return float("infinity")
        else:
            return sum / numVals

    def mode(self, vals):
        counter = Counter(vals)
        counter[float('infinity')] = -1
        mode = counter.most_common()[0][0]
        return mode

    def printAssigns(self):
        to_print = "Making Assignments:\n\t"
        for i in range(len(self.assignments)):
            cluster = self.assignments[i]
            if i == 0:
                pass
            elif i % 10 == 0:
                to_print = to_print + '\n\t'
            to_print = to_print + str(i) + '=' + str(cluster) + ' '
        print(to_print)

    def printCentroids(self):
        to_print = ''
        for i in range(len(self.centroids)):
            to_print = to_print + "Centroid %d = " % i
            centroid = self.centroids[i]
            for j in range(len(centroid)):
                if j != 0:
                    to_print = to_print + ', '
                if np.isposinf(centroid[j]):
                    to_print = to_print + '?'
                else:
                    if self.data.attr_type(j) == 'nominal':
                        to_print = to_print + self.data.enum_to_str[j][int(centroid[j])].lower()
                    else:
                        to_print = to_print + "%.3f" % centroid[j]
            to_print = to_print + '\n'
        print(to_print)

    def printDetails(self):
        for i in range(self.k):
            cluster = self.clusters[i]
            sse = self.sse[i]
            print("Cluster %d: %d instances, %.3f SSE" % (i, len(cluster), sse))

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]
        labels += [0]

    def test_mse(self, features, labels):
        pass
