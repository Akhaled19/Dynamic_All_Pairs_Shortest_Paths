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

INTRODUCTION:
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
This project, therefore, concentrates on implementing a dynamic shortest path algorithm. Initially, 
it calculates all the shortest paths using the Floyd-Warshall algorithm, then updates those paths 
which have been affected by an alteration in one flight's cost. This ensures that the shortest 
routes query is answered rapidly without doing unnecessary calculations.

DynamicAPSP is the Java code used for the project. It dynamically updates a weighted directed graph 
with respect to edge updates. It keeps track of edge weights, shortest path lengths, history of 
paths, locally shortest paths, and next hop information for reconstructing paths. It implements the 
concept of dynamic all-pair shortest paths, locally shortest paths, and historical cheapest paths. 
Comments in the code state that the project idea was conceived from the Dynamic APSP paper embedded 
in the Java code.

Since live airline ticket pricing data is difficult to access and often requires paid APIs or 
complex scraping, this project uses a fictional CSV dataset named:

dynamic_apsp_flights.csv

The dataset represents airline routes between airports. Each row stores route information such as:

route_id

source_id

source_code

source_city

target_id

target_code

target_city

airline

price_usd

First, it parses the CSV file and creates an edge for each flight route. In the actual program, the 
source_id denotes the start node, the target_id is the end node, and the price_usd denotes the edge 
weight. The program will strip out the dollar sign before converting the string value into a double.

In our problem, we create a graph that has 18 nodes. Each node corresponds to an airport or city. 
The edges are directional since traveling from one airport to another may cost differently compared 
to the reverse direction.

MAIN DATA STRUCTURES USED
1. Weight Matrix
   double[][] weight;
   
The weights matrix consists of the actual costs of travelling between two
airports. If there is no actual route from airport u to airport v, the cost is infinity. In case of
u = v, the cost is considered as 0.0.
This matrix corresponds to the edges of the initial graph.

2. Distance Matrix 
   double[][] dist;
   
In the dist matrix, the least cost currently known between all pairs of airports is stored. Thus,
after running Floyd-Warshall on this graph, dist[i][j] will give the least cost between airport i
and airport j.
This enables the program to query the cost in O(1) using:
   query(u, v)
dist[u][v] is used for current cheapest distance and supports constant-time distance queries.

3. Next-Hop Matrix
   int[][] next;

In contrast to the previous matrix, which helps us calculate the total cost, the following one
enables us to recreate the exact route taken by the path. Thus, for instance, if the least
expensive path between Boston and Tokyo passes via New York and Los Angeles, we will know that.   

   getPath(u, v)
uses the next table to rebuild the route step by step.   
   
4. Priority Queue of Locally Shortest Paths
   PriorityQueue<PathEntry>[][] P;
For each pair of vertices, there exists a priority queue which maintains a list of locally least
cost path candidates.
   double cost;
   int mid;
The cost is the total cost of the path, while mid is the intermediate or join vertex of the path.
This approach is useful for the dynamic algorithm to keep track of alternative paths, especially
after any change in the price of the edge.

5. Historical Path Matrix
   double[][] hist;
The hist matrix holds the value of the lowest cost path ever seen between two nodes. It plays an
essential role in case of an increase in the cost of any edge. In such situations, the path that
was considered to be the cheapest may not be the most appropriate one anymore. The history of the
cheapest paths allows the algorithm to look for new paths without executing Floyd-Warshall again.

The code comments refer to this concept as the use of “historical cheapest paths.”

ALGORITHMS USED

1. Floyd-Warshall Algorithm

Floyd-Warshall computes the cheapest path between every pair of vertices. It works by considering every vertex as a possible intermediate stop.

The main idea is:
For every intermediate vertex k:
    For every source vertex i:
        For every destination vertex j:
            If dist[i][k] + dist[k][j] < dist[i][j]:
                Update dist[i][j]

The formula is:
dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
In this case, the Floyd-Warshall algorithm is employed only once at the start to calculate the 
shortest cost of flights between all pairs of airports. The Java constructor initializes the 
distance matrix using the weight matrix and relaxes all paths that pass through any intermediate 
vertex.












