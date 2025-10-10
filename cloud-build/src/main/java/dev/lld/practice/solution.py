
# Your old code in java17 has been preserved below.
# /*
# # Build a cloud build sytem
# # Build an application with a large number of artifacts (source files, dependencies) faster
# # Use the concurrency of a large cluster of machines in the cloud build
# # Mock out the components that are not to be focused on: (compiler, repository, project spec parser)
# # Scheduler for the cloud cluster is the main focus
# */


from collections import defaultdict

class Build:

    def __init__(self):
        self.depGraph = DepGraph()

    def buildNow(self, target):
        self.depGraph.loadGraph(target)

        targets = self.depGraph.getTargets()

        print('Building targets {}', targets)


        targets = self.depGraph.updateGraph(targets)










class DepGraph:
    def __init__(self):
        self.v = []
        self.e = []
        self.edge_dict = defaultdict(list)
        self.target = None
        self.weights = defaultdict(int)


    def loadGraph(self, target):
        pass
        self.target = target


    def getTargets(self):
        for edge in self.e:
            fr, to = edge
            self.weights[to] += 1

        targets = []
        for key in self.weights:
            if self.weights[key] == 0:
                targets.append(key)
        return targets

    def updateGraph(self, targets):
        for t in targets:
            for edge in self.edge_dict[t]:
                fr, to = edge
                self.weight[to] -= 1

        targets = []
        for key in self.weights:
            if self.weights[key] == 0:
                targets.append(key)
        return targets










# import java.io.*;
# import java.util.*;
# import java.text.*;
# import java.math.*;
# import java.util.regex.*;

# public class Solution {



#     public static void main(String[] args) {

#     }
# }

