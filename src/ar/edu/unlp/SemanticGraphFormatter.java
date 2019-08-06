package ar.edu.unlp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ar.edu.unlp.constants.Words;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.MapFactory;

public class SemanticGraphFormatter {

	protected SemanticGraph dependencies = null;
	protected Stack<String> closureTags = null;
	private static final MapFactory<IndexedWord, IndexedWord> wordMapFactory = MapFactory.hashMapFactory();
	protected Map<String,String> wordNER = null;
		
	private static String space(int width) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < width; i++) {
			b.append(' ');
		}
		return b.toString();
	}

	private void recToString(IndexedWord curr, CoreLabel.OutputFormat wordFormat, StringBuilder sb, int offset,
			Set<IndexedWord> used) {
		used.add(curr);
		List<SemanticGraphEdge> edges = dependencies.outgoingEdgeList(curr);
		Collections.sort(edges);
		for (SemanticGraphEdge edge : edges) {
			IndexedWord target = edge.getTarget();
			sb.append(space(2 * offset));
			sb.append(this.generateXMLNode(edge.getRelation().toString(), target.toString(wordFormat))).append("\n");
			/*sb.append(space(2 * offset)).append("-> ").append(target.toString(wordFormat)).append(" (")
					.append(edge.getRelation()).append(")\n");*/
			if (!used.contains(target)) { // recurse
				recToString(target, wordFormat, sb, offset + 1, used);
			}
			sb.append(space(2 * offset));
			sb.append(this.closureTags.pop()).append("\n");
		}
	}
	
	public String generateXMLNode(String nodeName, String wordTag){
		nodeName = this.correctNodeName(nodeName);
		StringBuilder sb = new StringBuilder("<");
		sb.append(nodeName);
		wordTag = wordTag.replaceAll("'", "&apos;");
		String[] arraywt = wordTag.split("/");
		if(arraywt.length>=2){
			sb.append(" word='");
			 if(Words.SIGNTRANSLATE.get(arraywt[0]) != null) {
				 arraywt[0] = Words.SIGNTRANSLATE.get(arraywt[0]);
			 }
			sb.append(arraywt[0]).append("' tag='");
			sb.append(arraywt[1]).append("' ");
			if(wordNER!=null){
				sb.append("ner='");
				sb.append(wordNER.get(arraywt[0])).append("' ");
			}			
			sb.append(">");
		}
		closureTags.push("</"+nodeName+">");
		return sb.toString();
	} 

	private String correctNodeName(String nodeName) {
		return nodeName.replace(":", "_");
	}

	public String toXMLString(SemanticGraph dependencies) {
		this.dependencies = dependencies;
		this.closureTags = new Stack<String>();
		
		CoreLabel.OutputFormat wordFormat = CoreLabel.OutputFormat.VALUE_TAG;
		Collection<IndexedWord> rootNodes = dependencies.getRoots();
		if (rootNodes.isEmpty()) {			
			return "ROOT is empty";
		}

		StringBuilder sb = new StringBuilder();
		Set<IndexedWord> used = wordMapFactory.newSet();
		for (IndexedWord root : rootNodes) {
			sb.append(this.generateXMLNode("root", root.toString(wordFormat))).append("\n");			
			recToString(root, wordFormat, sb, 1, used);
			sb.append(this.closureTags.pop()).append("\n");
		}
		Set<IndexedWord> nodes = wordMapFactory.newSet();
		nodes.addAll(this.dependencies.vertexSet());
		nodes.removeAll(used);
		while (!nodes.isEmpty()) {
			IndexedWord node = nodes.iterator().next();
			sb.append(node.toString(wordFormat)).append("\n");
			recToString(node, wordFormat, sb, 1, used);
			nodes.removeAll(used);
		}
		return sb.toString();
	}

	public Map<String, String> getWordNER() {
		return wordNER;
	}

	public void setWordNER(Map<String, String> wordNER) {
		this.wordNER = wordNER;
	}
}
