package csci446.project1;

import csci446.project1.GraphSystem.Graph;
import csci446.project1.GraphSystem.Point;

import java.util.*;

public class BacktrackingMAC {
    private Graph graph;
    private int k;
    boolean success = false;
    int iterations = 0;
    // Holds a stack so it's easy to backtrack
//    int[] coloring;
//    HashSet<Integer>[] domains;

    BacktrackingMAC(Graph graph, int k){
        //Setup stacks
        int[] coloring = new int[graph.points.length];
        boolean[][] domains = new boolean[graph.points.length][k];

        this.graph = graph;
        this.k = k;

        //Initial color state
        for(int i = 0; i < graph.points.length; i++){
            coloring[i] = -1;
        }
        //Initial domain values
        for(int i = 0; i < graph.points.length; i++){
            for(int j=0; j < k; j++){
                domains[i][j] = true;
            }
        }

        success = colorGraph(0, coloring.clone(), domains.clone());

        if(success){
            System.out.println("\n\tBacktracking with MAC: Successfully found solution with " + k + " colors.");
        }else{
            System.out.println("\n\tBacktracking with MAC: Failed to find solution with " + k + " colors.");
        }
    }

    private boolean colorGraph(int vertex, int[] coloring, boolean[][] domains){
        if(vertex == graph.points.length)
            return true;

        for(int color = 0; color < k; color++)
        {
            if(canColor(vertex, color, coloring)) {
                coloring[vertex] = color;
                iterations++;
                if(iterations >= 40000) {
                    return false;
                }
                for(int x = 0; x < k; x++){
                    if(x != color)
                        domains[vertex][x] = false;
                }
                // Update arc consistency
                if(!makeArcConsistent(vertex, domains))
                    return false;

                if(isSolved(domains)) {
                    return true;
                }
                if (colorGraph(vertex + 1, coloring.clone(), copyDomains(domains)))
                    return true;
                else
                    coloring[vertex] = -1;
            }
        }
        return false;
    }

    private boolean isSolved(boolean[][] domains) {
        for(boolean[] domain: domains){
            int domainCount = 0;
            for(boolean aDomain : domain){
                if(aDomain)
                    domainCount++;
            }
            if (domainCount > 1 || domainCount == 0)
                return false;
        }
        return true;
    }

    private boolean canColor(int vertex, int color, int[] coloring){
        // Check each point connected to the point in question. If we've colored it the color "color" already, then
        // return false.  Check the cspDomains to see if the color is a valid choice (forward checking).  Check the
        // constraints as well.  If there are any adjacent nodes who only have one choice left.
        for(Point checkPoint : graph.points[vertex].connectedPoints) {
            if(coloring[checkPoint.id] == color) {
                return false;
            }
        }
        return true;
    }

    // Implments AC3 algorithm
    private boolean makeArcConsistent(int vertex, boolean[][] domains){
        //Examine each arc
        LinkedList<Integer> arcQueue = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        arcQueue.push(vertex);
        while(!arcQueue.isEmpty()){
            int point = arcQueue.pop();
            visited.add(point);
            for(Point connectedPoint : graph.points[point].connectedPoints){
                if(removeInconsistentValues(point, connectedPoint.id, domains)){
                    boolean isEmpty = true;
                    for(int x = 0; x < domains[connectedPoint.id].length; x++){
                        if(domains[connectedPoint.id][x])
                            isEmpty = false;
                    }
                    if(isEmpty)
                        return false;

                    for(Point neighbors : graph.points[connectedPoint.id].connectedPoints){
                        if(!visited.contains(neighbors.id))
                            arcQueue.push(neighbors.id);
                    }
                }
            }
        }

        return true;
    }

    private boolean removeInconsistentValues(int p1, int p2, boolean[][] domains){
        boolean removed = false;
        for(int x = 0; x < domains[p2].length; x++){
            if(domains[p1][x] && !checkConstraints(x, p1, domains)){
                domains[p2][x] = false;
                removed = true;
            }
        }

        return removed;
    }

    private boolean checkConstraints(int x, int p, boolean[][] domains) {
        boolean isLegal = false;
        int domainCount = 0;
        // If only 1 value,
        for(int y = 0; y < domains[p].length; y++){
            if(domains[p][y] && y != x)
                isLegal = true;
        }
        return isLegal;
    }

    private boolean[][] copyDomains(boolean[][] domains){
        boolean[][] copy = new boolean[domains.length][domains[0].length];
        for(int i = 0; i < domains.length; i++){
            System.arraycopy(domains[i], 0, copy[i], 0, domains[i].length);
        }
        return copy;
    }

}
