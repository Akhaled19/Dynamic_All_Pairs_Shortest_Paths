//IMPLEMENT:Dynamic All-Pairs Shortest Paths (Dynamic APSP)
/*
APSP maintain a weighted directed graph under arbitrary edge inserions and deletions
and answer distance queries dist(u, v) in O(1). 
The key result O(n^2 log n) amortized time per update, O(1) per query
only works for non-negative real weights

used this paper: https://www.diag.uniroma1.it/demetres/docs/dapsp-full.pdf
locally shorted path is a path P = (v₀, v₁, ..., vₖ) is locally shortest if:

    1. Every proper prefix of P is a shortest path from v₀ to the respective vertex
    2. Every proper suffix of P is a shortest path from the respective vertex to vₖ

The algo handles decreases (easly) and increases (semi hard.. as previous cheapest paths may become invalid)

historical cheapest paths: paths that were cheapest at some point during the update sequence. the core idea is that
when an edge weight increases and a cheapest path is destoryed, the new cheapest path must be assembled from pieces 
that were previously cheapest paths -> H(u, v) for each pair, and ordered by weight 

Overall structure 
dist[u][v] = current cheapest distance O(1) query
P(u, v) = sorted list of locally cheapest paths from u to v, ordered by weight 
H(u, v) = The historical paths. 
*/


import java.util.*;
import java.io.*;

public class DynamicAPSP {
    static final double INF = Double.MAX_VALUE / 2.0; // using inf/2 to avoid overflow 

    private final int n; //number of vertices 
    private double[][] weight; //weight[u][v] = current edge weight (inf = no edge)
    private double[][] dist; //dist[i][j] = current cheapest path cost from i to j 
    private PriorityQueue<PathEntry>[][] P; //locally cheapest path from i to j, ordered by weight
    private double[][] hist; //hist[i][j] = the best (minimum cost) path that has EVER been in P[i][j]
    private int[][] next; //next[i][j] = first hop on the current cheapest path from i to j.

    //inner class: a path entry in P[i][j]
    static class PathEntry implements Comparable<PathEntry> {
        double cost; //total path cost from i to mid to j 
        int mid; // the join vertex where -1 mid means this a direct edge 

        PathEntry(double cost, int mid){
            this.cost = cost;
            this.mid = mid;
        }
        @Override
        public int compareTo(PathEntry obj){
            int primaryCost = Double.compare(this.cost, obj.cost);
            if(primaryCost != 0) return primaryCost;
            //otherwise the return the lower mid index first
            return Integer.compare(this.mid, obj.mid);
        }
    }
    
    public DynamicAPSP(int n, double[][] edges){
        this.n = n;
        this.weight = new double[n][n];
        this.dist = new double[n][n];
        this.hist = new double[n][n];
        this.next = new int[n][n];
        this.P = new PriorityQueue[n][n];

        //step 1: intialize weight matrix 
        for (double[] row : this.weight)   Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) this.weight[i][i] = 0.0; 

        for (double[] e : edges) {
            int u = (int) e[0];
            int v = (int) e[1];
            double wt = e[2];
            this.weight[u][v] = wt;  // directed edge u -> v
        }
        
        //step 2: Initialize dist via Floyd-Warshall
        //this give us the intial all-pairs cheapest paths in O(n^3)
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                this.dist[i][j] = this.weight[i][j];
                this.next[i][j] = (this.weight[i][j] < INF && i != j) ? j : -1;
            }
        }
        // Relax through every intermediate vertex k
        for (int k = 0; k < this.n; k++) {
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.n; j++) {
                    if (this.dist[i][k] < INF && this.dist[k][j] < INF) {
                        double via = this.dist[i][k] + this.dist[k][j];
                        if (via < this.dist[i][j]) {
                            this.dist[i][j] = via;
                            // go toward k first
                            this.next[i][j] = this.next[i][k];  
                        }
                    }
                }
            }
        }

        //step 3: Initialize P[i][j] and hist[i][j]
        // A path i -> x -> j is locally shortest if dist[i][x] + weight[x][j] == dist[i][j], note x is the second to last hop i.e mid 
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                this.P[i][j] = new PriorityQueue<>();
                this.hist[i][j] = INF;
                
                // case: self path i.e zero cost
                if (i == j) {
                    this.dist[i][j] = 0.0;
                    this.hist[i][j] = 0.0;
                    continue;
                }
 
                //direct edge i -> j
                if (this.weight[i][j] < INF) {
                    //mid here is i i.e no intermediate split
                    this.P[i][j].add(new PathEntry(this.weight[i][j], i));
                }
 
                //two-segment paths i -> mid -> j for each mid
                for (int mid = 0; mid < this.n; mid++) {
                    if (mid == i || mid == j) continue;
                    if (this.dist[i][mid] < INF && this.dist[mid][j] < INF) {
                        double cost = this.dist[i][mid] + this.dist[mid][j];
                        //only add if it matches the known shortest distance
                        //locally shortest == achieves the min
                        if (Math.abs(cost - this.dist[i][j]) < 1e-12) {
                            P[i][j].add(new PathEntry(cost, mid));
                        }
                    }
                }
 
                //hist = best path we've seen so far = dist[i][j] if reachable
                if (!this.P[i][j].isEmpty()) {
                    this.hist[i][j] = this.P[i][j].peek().cost;
                }
            }
        }
    }
    
    /*public API starts here */

    //returns the cheapest path from u to v else returns inf 
    //o(1) lookup
    public double query(int u, int v){
        return dist[u][v] >= INF ? Double.POSITIVE_INFINITY : dist[u][v];
    }

    //reconstruct the actual cheapest path from u to v as a list of vertices 
    //O(n) using the next table 
    public List<Integer> getPath(int u, int v){
        List<Integer> path = new ArrayList<>();
        //if the u -> v is unreachable 
        if(this.dist[u][v] >= INF) return path;

        int cur = u;
        path.add(cur);
        Set<Integer> seen = new HashSet<>();
        seen.add(cur);
        while(cur != v){
            int nxt = next[cur][v];
            //there is a cycle 
            if(nxt == -1 || seen.contains(nxt)) return new ArrayList<>();
            seen.add(nxt);
            path.add(nxt);
            cur = nxt;
        }
        return path;
    }

    //upadte the weight of edge (u -> v) to newWeight 
    //calls helper methods decreaseUpdate or increaseUpdate as appropriate.
    //O(n^2 * log^3 n) amortized 
    public void updateEdge(int u, int v, double newWeight){
        double oldWeight = this.weight[u][v];
        //if no change i.e cost is the same
        if(Math.abs(newWeight - oldWeight) < 1e-12) return; 
        //else there is a change to the price 
        this.weight[u][v] = newWeight;
        //then call the decreaseUpdate or increaseUpdate helper methods 
        if(newWeight <= oldWeight){
            //edge got cheaper so dist decreases 
            decreaseUpdate(u, v, newWeight, oldWeight);
        }else{
            //edge got more expensive so some cheapest paths might be broken 
            increaseUpdate(u, v, newWeight, oldWeight);
        }
    }

    /*
    Case 1: Decrease update 
    idea is cheaper eddge(x,y) can only improve dist[i][j] for every pair (i, j) that is connected to (x,y)
    O(n^2) as we have to loop over all (i,j) pairs
    */
   private void decreaseUpdate(int x, int y, double newWeight, double oldWeight){
        //for every source i that can reach x, and every target j that is reachable from y 
        for (int i = 0; i < n; i++) {
            //i can't reach x skip
            if (dist[i][x] >= INF && i != x) continue; 
            
            for (int j = 0; j < n; j++) {
                //y can't reach j skip
                if (dist[y][j] >= INF && y != j) continue; 
 
                //cost of the path i -> ... -> x -> y -> ... -> j
                double i_x = (i == x) ? 0.0 : this.dist[i][x];
                double y_j = (y == j) ? 0.0 : this.dist[y][j];
                double candidate = i_x + newWeight + y_j;
 
                if (candidate < this.dist[i][j] - 1e-12) {
                    //this new path is cheaper — update dist and P[i][j]
                    this.dist[i][j] = candidate;
                    this.next[i][j] = (i == x) ? y : this.next[i][x];
 
                    // Insert the new locally-shortest path into P[i][j].
                    // mid = x means the path goes through x as the join point.
                    this.P[i][j].add(new PathEntry(candidate, x));
 
                    // Update historical best if this is a new min
                    if (candidate < this.hist[i][j]) {
                        this.hist[i][j] = candidate;
                    }
                }
            }
        }
   }
    /*
    Case 2: Incraese update 
    some cheapest paths that used (x, y) are now broken as their cost increased
    we need to find replacement paths for all affected (i, j) pairs
    How?
    1. identify all (i, j) whose current cheapest path used (x, y), these are pairs where: dist[i][x] + oldW + dist[y][j] == dist[i][j]
    2. for each such pair, recompute dist[i][j] using historical paths.
    3. process in order of current dist (like Dijkstra over pairs) so that when we fix (i, j) the sub-paths we rely on are already correct
    O(n^2 log n ) per update in the main loop 
    */
    private void increaseUpdate(int x, int y, double newWeight, double oldWeight) {
 
        // Step 1: Find all pairs (i,j) whose cheapest path used edge (x->y) 
        // A pair (i,j) is affected if the old cheapest path went through (x,y): dist[i][x] + oldW + dist[y][j]  ==  dist[i][j]   (within floating-point tolerance)
 
        // used min-heap of (newCost, i, j) to process in cost order (like Dijkstra)
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> Double.longBitsToDouble(e[0])));
        // Tracks which (i,j) pairs are already enqueued or finalized
        boolean[][] inQueue = new boolean[n][n];
        boolean[][] finalized = new boolean[n][n];
        double[][] newDist = new double[n][n];
 
        // Initialize newDist to current dist (we are hoping that most pairs won't change)
        for (int i = 0; i < this.n; i++)
            for (int j = 0; j < this.n; j++)
                newDist[i][j] = this.dist[i][j];
 
        for (int i = 0; i < this.n; i++) {
            double i_x = (i == x) ? 0.0 : this.dist[i][x];
            if (i_x >= INF) continue;
 
            for (int j = 0; j < this.n; j++) {
                double y_j = (y == j) ? 0.0 : this.dist[y][j];
                if (y_j >= INF) continue;
 
                double oldPathCost = i_x + oldWeight + y_j;
 
                // Check if (x->y) was ON the cheapest path for (i,j)
                //if so then we need to recompute dist[i][j].
                if (Math.abs(oldPathCost - this.dist[i][j]) < 1e-12) {
                    double best = computeHistoricalBest(i, j);
                    newDist[i][j] = best;
 
                    if (!inQueue[i][j]) {
                        inQueue[i][j] = true;
                        pq.offer(new long[]{
                            Double.doubleToLongBits(best),
                            i, j
                        });
                    }
                }
            }
        }
 
        //Step 2: Process affected pairs in order of new cost (Dijkstra over pairs) 
        while (!pq.isEmpty()) {
            long[] entry  = pq.poll();
            double cost = Double.longBitsToDouble(entry[0]);
            int i = (int) entry[1];
            int j = (int) entry[2];
 
            if (finalized[i][j]) continue; // already processed with a better cost
            finalized[i][j] = true;
 
            // Commit the new distance
            dist[i][j]   = cost;
            newDist[i][j] = cost;
 
            //update P[i][j]: remove broken entries, add new best
            cleanAndReinsert(i, j, cost);
 
            //update the next-hop table for path reconstruction
            updateNextHop(i, j, cost);
 
            //update historical best
            if (cost < this.hist[i][j]) {
                this.hist[i][j] = cost;
            }
 
            //Step 3: generate to pairs that depend on (i,j)
            //i.e any pair (a, b) whose cheapest path goes through the updated (i,j)
            for (int a = 0; a < this.n; a++) {
                double a_i = (a == i) ? 0.0 : dist[a][i];
                if (a_i >= INF) continue;
                for (int b = 0; b < this.n; b++) {
                    if (finalized[a][b]) continue;
                    double j_b = (j == b) ? 0.0 : dist[j][b];
                    if (j_b>= INF) continue;
 
                    double candidate = a_i + cost + j_b;
 
                    // Only enqueue if this is strictly better than what we already have
                    if (candidate < newDist[a][b] - 1e-12) {
                        newDist[a][b] = candidate;
                        pq.offer(new long[]{
                            Double.doubleToLongBits(candidate),
                            a, b
                        });
                        inQueue[a][b] = true;
                    }
                }
            }
        }
    }

    //Helper method to compute the best path cost using historical paths 
    private double computeHistoricalBest(int i, int j) {
        double best = INF;
 
        // Direct edge
        if (this.weight[i][j] < INF) {
            best = Math.min(best,this.weight[i][j]);
        }
 
        // Two-segment paths through historical bests
        for (int z = 0; z < this.n; z++) {
            if (hist[i][z] < INF && hist[z][j] < INF) {
                double candidate = hist[i][z] + this.hist[z][j];
                if (candidate < best) {
                    best = candidate;
                }
            }
        }
        return best;
    } 

    //helper method: clean P[i][j] and reinsert the new best path
    private void cleanAndReinsert(int i, int j, double newCost) {
        //lazy way to remove broken paths
        while (!this.P[i][j].isEmpty()) {
            PathEntry top = this.P[i][j].peek();

            //recompute the cost of this path from current dist
            double recomputed = recomputePathCost(i, j, top.mid);
            if (Math.abs(recomputed - top.cost) < 1e-12) {
                break; //top is still valid
            }
            P[i][j].poll(); // discard broken entry
        }
 
        //insert the new best entry (mid=-1 means just record the cost, join is unknown)
        if (newCost < INF) {
            P[i][j].add(new PathEntry(newCost, findBestMid(i, j, newCost)));
        }
    }

    //helper that recompute the cost of a stored path entry (i -> mid -> j) using current dist
    private double recomputePathCost(int i, int j, int mid) {
        if (mid == i) {
            // Direct edge
            return this.weight[i][j];
        }
        double left  = (i == mid) ? 0.0 : dist[i][mid];
        double right = (mid == j) ? 0.0 : dist[mid][j];
        if (left >= INF || right >= INF) return INF;
        return left + right;
    }

    //helper to find the intermediate vertex z that achieves dist[i][j] = dist[i][z] + dist[z][j].
    private int findBestMid(int i, int j, double targetCost) {
        if (Math.abs(this.weight[i][j] - targetCost) < 1e-12) return i; // direct edge
        for (int z = 0; z < n; z++) {
            if (z == i || z == j) continue;

            if (dist[i][z] < INF && dist[z][j] < INF) {
                if (Math.abs(this.dist[i][z] + this.dist[z][j] - targetCost) < 1e-12) {
                    return z;
                }
            }
        }
        return -1;
    }

    //helper update the next-hop table 
    private void updateNextHop(int i, int j, double newCost) {
        if (newCost >= INF) {
            next[i][j] = -1;
            return;
        }
        if (i == j) {
            next[i][j] = -1;
            return;
        }
        // Check direct edge first
        if (this.weight[i][j] < INF && Math.abs(this.weight[i][j] - newCost) < 1e-12) {
            next[i][j] = j;
            return;
        }
        // Find first hop: which direct neighbor k of i leads to the cheapest path?
        for (int k = 0; k < n; k++) {
            if (k == i) continue;
            if (this.weight[i][k] < INF && dist[k][j] < INF) {
                if (Math.abs(this.weight[i][k] + dist[k][j] - newCost) < 1e-12) {
                    next[i][j] = k;
                    return;
                }
            }
        }
        next[i][j] = -1;
    }
    public static double[][] readFlightsCSV(String filename) {
        List<double[]> edges = new ArrayList<>();

        try (Scanner scanner = new Scanner(new java.io.File(filename))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
            
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");

                int sourceId = Integer.parseInt(parts[1].trim());
                int targetId = Integer.parseInt(parts[4].trim());
                double wt = Double.parseDouble(parts[8].replace("$", "").trim());
                edges.add(new double[]{sourceId, targetId, wt});
            }

        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        double[][] result = new double[edges.size()][3];

        for (int i = 0; i < edges.size(); i++) {
            result[i] = edges.get(i);
        }

        return result;
    }

    public static void main(String[] args) {
        double[][] edges = readFlightsCSV("dynamic_apsp_flights.csv");

        DynamicAPSP graph = new DynamicAPSP(18, edges);

        System.out.println("BOS -> HND cheapest cost: $" + graph.query(0, 14));
        System.out.println("Path: " + graph.getPath(0, 14));

        graph.updateEdge(0, 2, 50);

        System.out.println("After update:");
        System.out.println("BOS -> LAX cheapest cost: $" + graph.query(0, 7));
        System.out.println("Path: " + graph.getPath(0, 7));
    }


}
