import java.util.ArrayList;

public interface Node {
	
	public String signature();
	
	public ArrayList<Node> connections();
	
	public int connectionCount();
}
