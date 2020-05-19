package com.antfin;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;

import java.util.*;

/**
 * @param <K>
 * @param <VV>
 * @param <EV>
 * @author Flians
 * @Description: The graph is described by Compressed Sparse Row (CSR).
 * When the vertex is assigned to a edge, it is saved into Map dict_V<vertex_id, value>.
 * The value is the index of this vertex in vertices, and is the index of its edges in edges.
 * @Title: Graph
 * @ProjectName graphRE
 * @date 2020/5/18 14:29
 */
public class Graph_CSR<K, VV, EV> extends Graph<K, VV, EV> {
    private List<Vertex<K, VV>> vertices;
    private List<List<Edge<K, EV> > > edges;
    // dict_V[vertex.id] -> index, record all vertices
    private Map<K, Integer> dict_V;
    // record the first index of the vertex without edges in vertices
    private Integer lastV;

    public Graph_CSR() {
        this.vertices = new LinkedList<>();
        this.edges = new ArrayList<>();
        this.dict_V = new HashMap<>();
        this.lastV = 0;
    }

    public Graph_CSR(List vg, boolean flag) {
        this();
        if (flag) {
            ((List<Vertex<K, VV>>) vg).forEach(this::addVertex);
        } else {
            ((List<Edge<K, EV>>) vg).forEach(this::addEdge);
        }
    }

    public Graph_CSR(List<Vertex<K, VV>> vertices, List<Edge<K, EV>> edges) {
        this(vertices, true);
        edges.stream().forEach(this::addEdge);
    }

    public void addVertex(Vertex<K, VV> vertex) {
        if (!this.dict_V.containsKey(vertex.getId())) {
            this.vertices.add(vertex);
            this.dict_V.put(vertex.getId(), this.dict_V.size());
        }
    }

    public void addEdge(Edge<K, EV> edge) {
        // the source vertex of edge has no edges.
        if (!this.dict_V.containsKey(edge.getSrcId())) {
            this.vertices.add(new Vertex<>(edge.getSrcId()));
            this.dict_V.put(edge.getSrcId(), this.lastV);
            this.dict_V.put(this.vertices.get(this.lastV).getId(), this.dict_V.size()-1);
            this.swap(this.lastV++, this.vertices.size()-1, this.vertices);
            List<Edge<K, EV> > item = new ArrayList<>();
            item.add(edge);
            this.edges.add(item);
        } else {
            // the source vertex of edge has other edges.
            this.edges.get(this.dict_V.get(edge.getSrcId())).add(edge);
        }
    }

    private void swap(int x,int y, List ll){
        Object ob=ll.get(x);
        ll.set(x, ll.get(y));
        ll.set(y, ob);
    }

    @Override
    public Vertex<K, VV> getVertex(K id) {
        return this.vertices.get(this.dict_V.get(id));
    }

    @Override
    public List<Edge<K, EV>> getEdge(K sid) {
        return this.edges.get(this.dict_V.get(sid));
    }

    public List<Vertex<K, VV>> getVertices() {
        return this.vertices;
    }

    public List<List<Edge<K, EV> > > getEdges() {
        return this.edges;
    }

    public Map<K, Integer> getDict_V() {
        return this.dict_V;
    }

    public Integer getLastV() {
        return lastV;
    }
}