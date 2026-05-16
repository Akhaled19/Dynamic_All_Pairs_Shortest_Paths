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

Complexity of Floyd-Warshall
Time Complexity: O(V^3)
Space Complexity: O(V^2)

2. Dynamic APSP Update Algorithm

The key achievement of this study is the development of the incremental updating algorithm. Rather 
than calculating all shortest paths after each modification, the algorithm determines whether the 
modified edge influences any of the cheapest paths already found.

The updating method is:
updateEdge(int u, int v, double newWeight)

This method compares the old flight price with the new flight price.
There are two cases:
The edge price decreases.
The edge price increases.
These two cases are handled differently because a price decrease is easier than a price increase.

Case 1: Edge Price Decrease
When an edge becomes cheaper, there might be some routes that become more beneficial, but none of 
the current routes become invalid. The algorithm examines every pair (i, j) to see whether 
including this edge in a path reduces the total cost.

The candidate path for an improved edge (x, y) is:
i → ... → x → y → ... → j
The candidate cost is:
dist[i][x] + newWeight(x, y) + dist[y][j]

In case the above candidate cost is less than the current dist[i][j], then the program updates the 
distance matrix as well as the next hop matrix. This method is very efficient since the program 
tests all possible paths which might profit from 
the new edge.

Complexity of Decrease - The decrease update checks every possible source and destination pair, so
its time complexity is approximately: O(V^2)
This is much faster than Floyd-Warshall’s O(V³) for large graphs.


Case 2: Edge Price Increase
When the cost of an edge increases, the problem becomes harder to solve since some of the cheapest 
routes may have included that edge. If the edge cost increases, then the previously identified 
cheapest routes may not necessarily be cheapest anymore.

The algorithm works by following the steps below:
In the first step, all the nodes (i,j) whose route passed through the old edge (x,y) can be 
determined using the equation:
dist[i][x] + oldWeight(x, y) + dist[y][j] == dist[i][j]

Then, if that case is valid, the minimum cost path from node i to node j would be dependent on the 
change in cost along the edge.
Next, for each such pair, a new path will be found based on past path data.
Finally, the priority queue approach will be used to process each such pair. The pair with the 
minimum cost will be processed first.
Fourth, the algorithm updates:

dist[i][j]
P[i][j]
hist[i][j]
next[i][j]
This avoids blindly recomputing every path in the graph.

Complexity of Increase Update
The comments in the code state that the increase update uses a priority queue with the affected 
pairs being processed, with the approximate computational cost of the loop being: O(V² log V)
A complete theoretical version of a dynamic APSP may become more complicated, yet what matters for 
this particular implementation is the fact that the update algorithm tries to restrict itself to 
processing only affected path pairs.

IMPLEMENTATION DETAIL:

In Java using the class: DynamicAPSP
The reading the CSV file: double[][] edges = readFlightsCSV("dynamic_apsp_flights.csv");
Then it creates the graph: DynamicAPSP graph = new DynamicAPSP(18, edges);

The constructor carries out three functions.
Firstly, it initializes the graph with costs for direct flights.
Secondly, it computes the initial cheapest costs between any two airports using the Floyd-Warshall 
algorithm.
Finally, it initializes additional data structures required for dynamic updating, such as local 
shortest paths queues and past path costs.
The query() function returns the cheapest current cost between two airports:
graph.query(0, 14)
The getPath() method returns the actual path:
graph.getPath(0, 14)
The updateEdge() method changes a flight price:
graph.updateEdge(0, 2, 50);
In the sample main method, the program first prints the cheapest cost and path from BOS to HND.Then 
it updates the cost of the edge from vertex 0 to vertex 2, and prints the updated cheapest cost and 
path from BOS to LAX. The uploaded code shows this testing structure in the main method.

HOW THE SYSTEM WAS TESTED
Testing for the program was done using the fictitious flight dataset. Testing mainly involved 
verifying the program’s capacity to calculate the cheapest paths before and after an increase in 
prices.

The key aspects included the following:
First, the loading of the CSV file into the program was successful.
Second, there were 18 vertices created on the graph.
Third, the initial costs for all pairs' cheapest path calculations were done using Floyd-Warshall.
Fourth, the program was queried for a route such as: 
BOS → HND
The program printed both the cheapest cost and the path.
Fifth, a flight price was changed using: 
graph.updateEdge(0, 2, 50);
This represents a real-time airfare update.
Sixth, the program queried another route: 
BOS → LAX
The new lowest cost and route were output.

Tests involved checking whether:
The CSV file was read properly.
The initial lowest routes were calculated properly.
The program was able to get the new lowest cost in constant time.
The program was capable of finding the exact route.
The program was able to process a cost change.
The route showed the impact of the modified cost.
Other tests might involve modifying some prices manually in the main function and comparing the
output with the new result obtained by running the Floyd-Warshall algorithm from scratch.

RESULTS AND DISCUSSIONS
The project demonstrates that there is an appreciable distinction between the static and dynamic 
shortest path algorithms.

It would be convenient to use the Floyd-Warshall algorithm as it returns a full list of cheapest 
paths between all airport pairs. After the initialization of the table, the queries become faster 
since the program just looks up values from the dist matrix.

Nevertheless, this algorithm is rather costly to be applied repeatedly. In case of changing just 
one airfare, it would be required to recalculate the whole graph and go through all vertex triples 
again. Although it might not make any big difference for a small-sized graph, for a bigger one with 
hundreds or thousands of airports it would be rather costly.

The incremental update algorithm helps to solve this problem by splitting the updating process into 
two types.

When there is a reduction in price, the program verifies whether the new route created by the 
cheaper route yields better paths. This is less complicated and involves verifying all the possible 
combinations once.

In case there is an increase in price, the program must be more cautious, as there might be changes 
in the optimal routes. This is where the complexity arises in this assignment, where the program 
verifies the new routes that were previously based on the cheaper routes.

Generally, the program indicates that maintaining additional data structures can help speed up 
future operations. In addition to maintaining minimum distance, it maintains other parameters like 
path candidates, costs history, and next hop.

COMPLEXITY ANALYSIS
1. Initial graph setup

2. Floyd-Warshall initialization

3. Query cheapest cost

4. Reconstruct path

5. Edge decrease update

6. Edge increase update

7. Space complexity








