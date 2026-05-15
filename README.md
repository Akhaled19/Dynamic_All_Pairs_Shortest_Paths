# Dynamic_All_Pairs_Shortest_Paths

QUESTION:

If you are given a graph of paths such as current airline flights, you can calculate optimal
cost using Floyd-Warshall which is O(V3). But since paths change in real time, what if
you can come up with an incremental algorithm to compute the new cheapest paths if the
data changes? For example, if only a single airfare changed, why should you have to
recompute everything?
This project will require you to setup a virtual set of costs. It would be lovely to pull in
ticket pricing for airplanes, but that's probably way too hard. If you want, you can try to
find a source where you can read in a text file of costs, but I assume you will not be able
to find one. This means you need to create your own fictitious situation (airfares,
trainfares, whatever). Then you solve the cheapest way to get from every point to every
other using Floyd-Warshall on your large, fake dataset. Then you devise an algorithm to
do it much faster incrementally.

There was a core question that:
If only one airfare changes in a transportation network, why should we recompute all shortest paths 
again using Floyd-Warshall?

Introduction:
All-Pairs Shortest Paths Is a graph algorithm which is used in the real world for the airline 
networks GPS navigations communication routes logistics and social network. The purpose is to 
compute the shortest path between every pair of vertices in the weighted graph. First the Floyd 
Warshall's algorithm was used however the real world system due to its dynamic progress it changes 
over the time.

Information known:
Floyd-Warshall solves All-Pairs Shortest Paths (APSP)
It uses three nested loops: one over all vertices, and two nested over pairs of vertices.
For each vertex, it checks all pairs to update shortest paths.
Its time complexity is: O(V^3) (This works well for static graphs.)

Problems encountered:
The transportation systems are usually dynamic which means flight prices change, traffic changes 
use of the path changes.

In such systems, recomputing shortest paths from scratch after every small update becomes 
computationally expensive.

This project focuses on solving this issue using a Dynamic All-Pairs Shortest Paths (Dynamic APSP) 
algorithm implemented in Java. Instead of recomputing the entire shortest path matrix after every 
edge update, the algorithm incrementally updates only the shortest paths affected by the modified 
edge.

The project combines:

Floyd-Warshall APSP
dynamic graph updates
historical shortest paths
locally shortest paths
priority queue optimization
path reconstruction

to create a research-inspired real-time shortest-path system.  

First observation:
A single edge update usually affects only a small subset of shortest paths. Boston → Chicago 
changes the price, then Tokyo → Mumbai probably would not change.

Leading to the initial Soltion:

Update edge weight
Run Floyd-Warshall again
Recompute all shortest paths

As the 








