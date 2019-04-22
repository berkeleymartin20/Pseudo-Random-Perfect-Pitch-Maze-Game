package byow.Core;

public class UnionFind {


    int[] list;
    int size;

    /* Creates a UnionFind data structure holding n vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind(int n) {
        list = new int[n];
        size = n;
        for (int x = 0; x < size; x++) {
            list[x] = -1;
        }
    }

    /* Throws an exception if v1 is not a valid index. */
    private void validate(int vertex) {
        if (vertex >= size) {
            throw new IllegalArgumentException("");
        }
    }

    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        // TODO
        validate(v1);
        int counter = 1;
        int temp = list[v1];
        while (temp != -1) {
            counter += 1;
            temp = list[temp];
        }
        return counter;
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    public int parent(int v1) {
        validate(v1);
        if (list[v1] == -1) {
            return v1;
        }
        int temp = list[v1];
        while (temp != -1) {
            temp = list[temp];
        }
        return temp;
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean connected(int v1, int v2) {
        validate(v1);
        validate(v2);
        return parent(v1) == parent(v2);
    }

    /* Connects two elements v1 and v2 together. v1 and v2 can be any valid 
       elements, and a union-by-size heuristic is used. If the sizes of the sets
       are equal, tie break by connecting v1's root to v2's root. Unioning a 
       vertex with itself or vertices that are already connected should not 
       change the sets but may alter the internal structure of the data. */
    public void union(int v1, int v2) {
        validate(v1);
        validate(v2);

        if (sizeOf(v1) > sizeOf(v2)) {
            list[parent(v2)] = list[parent(v1)];
        } else if (sizeOf(v1) < sizeOf(v2)) {
            list[parent(v1)] = list[parent(v2)];
        } else if (parent(v1) > parent(v2)) {
            list[parent(v2)] = list[parent(v1)];
        } else {
            list[parent(v1)] = list[parent(v2)];
        }
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. */
    public int find(int vertex) {
        int size = sizeOf(vertex);
        int returnthis = parent(vertex);
        int[] tempSt = new int[size];
        int idx = 0;
        int temp = list[vertex];
        while (temp != -1) {
            tempSt[idx] = temp;
            idx++;
            temp = list[temp];
        }
        for (int x = 0; x < size; x++) {
            list[tempSt[x]] = returnthis;
        }
        return returnthis;
    }

}
