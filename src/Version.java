import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.ListenableUndirectedGraph;

import com.sun.javafx.collections.MappingChange.Map;

public class Version
{
	public String name;
	public Set<String> godClasses;
	public Set<String> featureEnvyClasses;
	public Set<String> featureEnvyMethods;
	public Set<String> longMethods;
	public Set<String> typeCheckingMethods;
	public Set<String> smellyClasses;
	public Set<String> smellyMethods;
	public Set<String> classesContainingSmells;
	public Set<String> smellyComponents;
	public ArrayList<Relation> usedRelations;
	public ArrayList<Relation> calledRelations;
	public ArrayList<Relation> containedRelations;
	public ArrayList<Relation> usedRelationsAll;
	public ArrayList<Relation> calledRelationsAll;
	public ArrayList<Relation> containedRelationsAll;
	public static ListenableUndirectedGraph<String, DefaultEdge> graph;
	public ConnectivityInspector<String, DefaultEdge> ce;
	public int vertexCount;
	public int nodesInClusters;
	public int nodesInSmallClusters;
	public Set<String> singleNodes;
	public Set<Set<String>> multiNodeClusters;
	public int largestClusterSize;
	public Set<String> largestCluster;
	public HashMap<String, Integer> smellyEdges;
	public HashMap<String, Integer> impacts;

	
	public Version(String versionName, String jarPath, String gcPath, String fePath, String lmPath, String tcPath, String gcPathTACO, String lmPathTACO)
	{
		name = versionName;
		usedRelations = new ArrayList<Relation>();
		calledRelations = new ArrayList<Relation>();
		containedRelations = new ArrayList<Relation>();

		usedRelationsAll = new ArrayList<Relation>();
		calledRelationsAll = new ArrayList<Relation>();
		containedRelationsAll = new ArrayList<Relation>();
		graph = new ListenableUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		multiNodeClusters = new HashSet<Set<String>>();
		smellyEdges = new HashMap<String, Integer>();
		impacts = new HashMap<String, Integer>();
	
		getSmells(gcPath, fePath, lmPath, tcPath, gcPathTACO, lmPathTACO);
		
		findRelations(jarPath);
		findRelationsAll(jarPath);
		createGraph();
		
		analyzeCluster();
	}

	private void getSmells(String gcPath, String fePath, String lmPath, String tcPath, String gcPathTACO, String lmPathTACO)
	{
		SmellParser smellParser = new SmellParser(gcPath, fePath, lmPath, tcPath, gcPathTACO, lmPathTACO);
		godClasses = smellParser.godClasses;
		featureEnvyClasses = smellParser.featureEnvyClasses;
		featureEnvyMethods = smellParser.featureEnvyMethods;
		longMethods = smellParser.longMethods;
		typeCheckingMethods = smellParser.TypeCheckingMethods;
		smellyClasses = smellParser.getSmellyClasses();
		smellyMethods = smellParser.getSmellyMethods();
		classesContainingSmells = smellParser.getClassesContainingSmells();
		smellyComponents = new TreeSet<String>(smellyClasses);
		smellyComponents.addAll(smellyMethods);
	}

	private void analyzeCluster()
	{
		ce = new ConnectivityInspector<>(graph);

		vertexCount = graph.vertexSet().size();
		
		printClusterSizes();
		
		largestCluster = new TreeSet<String>();
		singleNodes = new TreeSet<String>();
		largestClusterSize = 0;
		
		for (Set<String> cluster : ce.connectedSets())
		{
			Set<String> sortedCluster = new TreeSet<String>(cluster);
			if(sortedCluster.size() != 1 )
			{
				multiNodeClusters.add(sortedCluster);
				
				if(sortedCluster.size() > largestClusterSize )
				{
					largestClusterSize = sortedCluster.size();
					largestCluster = sortedCluster;
				}
			}
			else
			{
				singleNodes.add(sortedCluster.iterator().next());
			}

			printCluster(sortedCluster);
		}
		nodesInClusters = vertexCount - singleNodes.size();
		nodesInSmallClusters = nodesInClusters - largestClusterSize;
	}

	private void printCluster(Set<String> sortedCluster)
	{
		System.out.println("Cluster : " + sortedCluster.size());
		int smellyEdgeOfCluster = 0;
		int impactsOfCluster = 0;
		for (String node : sortedCluster)
		{
			if(node.contains(":"))
			{
				System.out.print("\t");
			}
			int smellyEdge = smellyEdges.get(node);
			int impact = impacts.get(node)-smellyEdge;
			System.out.println("\t\t" + node + "\t" + smellyEdge + "\t" + impact);
			smellyEdgeOfCluster+=smellyEdge;
			impactsOfCluster += impact;
		}
		System.out.println("Smelly edges : " + smellyEdgeOfCluster/2);
		System.out.println("Impact edges : " + impactsOfCluster);
	}

	private void printClusterSizes()
	{
		System.out.println("Vertex Count\t" + vertexCount);
		System.out.println("Cluster Count\t" + ce.connectedSets().size());

		System.out.print("Cluster sizes : ");
		
		for (Set<String> cluster : ce.connectedSets())
		{
			System.out.print(cluster.size() + " ");
		}
		
		System.out.println();
	}

	private void createGraph()
	{
		Set<String> sc = smellyClasses;
		Set<String> sm = smellyMethods;

		for (String cv : sc)
		{
			graph.addVertex(cv);
			smellyEdges.put(cv, 0);
			
		}
		for (String mv : sm)
		{
			graph.addVertex(mv);
			smellyEdges.put(mv, 0);

			String className = mv.split(":")[0];
			if (sc.contains(className))
			{
				containedRelations.add(new Relation(className, mv));
			}
		}
		
		for (Relation cmr : containedRelations)
		{
			graph.addEdge(cmr.u, cmr.v);
			smellyEdges.put(cmr.u, smellyEdges.get(cmr.u)+1);
			smellyEdges.put(cmr.v, smellyEdges.get(cmr.v)+1);
		}
		
		for (Relation cr : usedRelations)
		{
			graph.addEdge(cr.u, cr.v);
			smellyEdges.put(cr.u, smellyEdges.get(cr.u)+1);
			smellyEdges.put(cr.v, smellyEdges.get(cr.v)+1);
		}
		
		for (Relation mr : calledRelations)
		{
			graph.addEdge(mr.u, mr.v);
			smellyEdges.put(mr.u, smellyEdges.get(mr.u)+1);
			smellyEdges.put(mr.v, smellyEdges.get(mr.v)+1);
		}
	}

	private void findRelations(String jarPath)
	{
		JCallGraph jcg = new JCallGraph();
		
		jcg.generate(jarPath, classesContainingSmells, smellyClasses, smellyMethods);
		Set<String> cr = jcg.visitor.classRelations;
		Set<String> mr = jcg.visitor.methodVisitor.methodRelations;

		for (String pair : cr)
		{
			String[] parts = pair.split(" ");
			usedRelations.add(new Relation(parts[0], parts[1]));
		}

		for (String pair : mr)
		{
			String[] parts = pair.split(" ");
			calledRelations.add(new Relation(parts[0], parts[1]));
		}
	}
	
	private void findRelationsAll(String jarPath)
	{
		JCallGraphFullProject jcg = new JCallGraphFullProject();
		
		jcg.generate(jarPath);
		Set<String> cr = jcg.visitor.classRelations;
		Set<String> mr = jcg.visitor.visitor.methodRelations;

		for (String pair : cr)
		{
			String[] parts = pair.split(" ");
			usedRelationsAll.add(new Relation(parts[0], parts[1]));
			if(!impacts.containsKey(parts[0])){
				impacts.put(parts[0], 1);
			}
			else {
				impacts.put(parts[0], impacts.get(parts[0])+1);
			}
			if(!impacts.containsKey(parts[1])){
				impacts.put(parts[1], 1);
			}
			else {
				impacts.put(parts[1], impacts.get(parts[1])+1);
			}
		}

		for (String pair : mr)
		{
			String[] parts = pair.split(" ");
			calledRelationsAll.add(new Relation(parts[0], parts[1]));
			if(!impacts.containsKey(parts[0])){
				impacts.put(parts[0], 1);
			}
			else {
				impacts.put(parts[0], impacts.get(parts[0])+1);
			}
			if(!impacts.containsKey(parts[1])){
				impacts.put(parts[1], 1);
			}
			else {
				impacts.put(parts[1], impacts.get(parts[1])+1);
			}
		}
		String[] keys = impacts.keySet().toArray(new String[0]);
		
		for (String str: keys) {
			if(!str.contains(":")){
				continue;
			}
			
			String[] parts = str.split(":");
			containedRelationsAll.add(new Relation(parts[0], str));
			if(!impacts.containsKey(parts[0])){
				impacts.put(parts[0], 1);
			}
			else {
				impacts.put(parts[0], impacts.get(parts[0])+1);
			}
			if(!impacts.containsKey(str)){
				impacts.put(str, 1);
			}
			else {
				impacts.put(str, impacts.get(str)+1);
			}
		}
	}
}

