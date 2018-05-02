from __future__ import (absolute_import, division, print_function, unicode_literals)

from .supervised_learner import SupervisedLearner
from .matrix import Matrix
from anytree import Node, RenderTree
import numpy as np
from collections import Counter
import copy
from scipy import stats


class DecisionTreeLearner(SupervisedLearner):

    labels = []

    def __init__(self):
        self.features = []
        self.labels = []
        self.root = None
        self.inductions = []
        self.ind_options = []
        self.split_options = []
        self.nodes_depth = [0, 0]
        pass

    def train(self, features, labels):
        self.__init__()
        """
        :type features: Matrix
        :type labels: Matrix
        """
        self.inductions = stats.mode(map(lambda x: features.col(x), range(features.cols)), axis=1)[0]
        self.ind_options = list(map(lambda x: len(Counter(features.col(x))), range(features.cols)))
        self.split_options = range(features.cols)
        self.features = np.zeros((features.rows, features.cols))
        for r in range(features.rows):
            self.features[r] = copy.deepcopy(features.row(r))
            self.induce(self.features[r])
        self.labels = np.array(labels.col(0))

        self.root = Node('root')
        self.nodes_depth[0] += 1
        self.splitOn(self.features, self.labels, self.root)
        # self.printTree()
        print()
        print('Training set accuracy: '+str(self.measure_accuracy(features, labels)))
        print('Nodes: '+str(len(self.root.descendants)), 'Depth: '+str(self.root.height))

    def induce(self, row):
        for i in range(len(row)):
            if row[i] not in range(self.ind_options[i]):
                row[i] = self.inductions[i]
        return row

    def printTree(self):
        print()
        for pre, fill, node in RenderTree(self.root):
            print("%s%s" % (pre, node.name))

    def splitOn(self, features, labels, parent):
        if parent.depth > self.nodes_depth[1]:
            self.nodes_depth[1] = parent.depth
        if len(self.split_options) < 1:
            parent.predict = stats.mode(labels)[0]
            return
        info = self.info(labels)
        gains = np.zeros(len(features[0]))
        for i in range(len(gains)):
            if i in self.split_options:
                attr_info = self.infoA(features[:, i], labels)
                gains[i] = info - attr_info
                # gains[i] = (info - attr_info) / attr_info
            else:
                gains[i] = 0
        if np.sum(gains) < 0.2:
            parent.predict = stats.mode(labels)[0]
            return
        split_on = np.argmax(gains)
        self.split_options.remove(split_on)
        col = features[:, split_on]
        opts = Counter(col).keys()
        for opt in opts:
            indices = np.where(col == opt)
            reduced_features = features[indices]
            reduced_labels = labels[indices]
            if len(reduced_labels) < 2 or len(Counter(reduced_labels)) < 2:
                counter = Counter(reduced_labels)
                predict = max(counter, key=counter.get)
                Node(self.name_node(split_on, opt, predict), parent=parent, split=split_on, opt=opt, predict=predict)
                self.nodes_depth[0] += 1
            else:
                new_node = Node(self.name_node(split_on, opt, None), parent=parent, split=split_on, opt=opt)
                self.nodes_depth[0] += 1
                self.splitOn(reduced_features, reduced_labels, new_node)

    def name_node(self, split_on, opt, predict):
        if predict is None:
            return 'col'+str(split_on)+',opt'+str(opt)
        else:
            return 'col'+str(split_on)+',opt'+str(opt)+',PREDICT'+str(predict)

    def info(self, labels):
        n = len(labels)
        counter = Counter(labels)
        keys = sorted(counter.keys())
        probs = [(counter[key] / n) for key in keys]
        infos = [(prob * np.log2(prob)) for prob in probs]
        return -np.sum(infos)

    def infoA(self, col, labels):
        # make ratios for features
        n = len(col)
        counter = Counter(col)
        keys = sorted(counter.keys())
        ratios = [(counter[key] / n) for key in keys]

        # for each feature, split on labels
        infos = np.zeros(len(keys))
        for f in range(len(keys)):
            indices = np.where(col == keys[f])
            new_labels = labels[indices]
            info = self.info(new_labels)
            infos[f] = info
        return np.sum(ratios * infos)

    def predict(self, features, labels):
        """
        :type features: [float]
        :type labels: [float]
        """
        del labels[:]
        node = self.root
        while not node.is_leaf:
            split_col = node.children[0].split
            test_opt = features[split_col]
            poss_opts = np.array([child.opt for child in node.children])
            index = np.argmax(poss_opts == test_opt)
            node = node.children[index]
        labels += [node.predict]

    def test_mse(self, features, labels):
        pass
