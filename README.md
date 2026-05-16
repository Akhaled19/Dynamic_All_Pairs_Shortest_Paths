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

As the it a real world systems where costs change continuously

Airline Networks → Ticket prices change
GPS Systems Traffic → conditions change
Internet Routing → Network congestion changes
Delivery Systems → Road availability changes
Communication Networks → Link failures occur

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

The chosen algorithm is Dynamic All-Pairs Shortest Paths (Dynamic APSP):
It handles the dynamic graph updates including:
Edge insertion: Add a new connection between two vertices
Edge deletion: Remove an existing connection
Edge weight increase: Increasing the cost of an existing edge
Edge weight decrease: Decreasing the cost of an existing edge

The main objective of Dynamic APSP algorithm is to:

Ensure that the shortest distance between all node pairs is correct
Prevent complete recalculations of the solution each time there is a small alteration
Reduce the time taken by updates on the graph
Perform efficient shortest path queries anytime

Why Dynamic APSP Was Chosen
Some shortest path algorithms that were considered before choosing the final solution method are 
listed below. Each of the algorithms mentioned is efficient in its own right; however, the 
differences between them are significant in terms of their applicability within a dynamic all-pairs 
system.

Floyd-Warshall-Classic algorithm for all-pairs shortest paths in dense graphs
Dijkstra’s Algorithm-Efficient single-source shortest path algorithm
A*-Heuristic based algorithm for single source to destination path finding 
LPA*(Lifelong Planning A*)-Incremental algorithm of A*
D-Lite* –Real time replanning algorithm for robotics and navigation
Johnson’s Algorithm-Efficient APSP algorithm for sparse graphs

Restriction of Floyd–Warshall Algorithm

Floyd–Warshall finds shortest path between every pair of vertices, but:
Runs in O(V^3) time complexity
Needs to be recalculated from scratch after modification of a graph
Cannot efficiently handle any modification to a graph
It is not suitable for the graphs which are subject to changes.


