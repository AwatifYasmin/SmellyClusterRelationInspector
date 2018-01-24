import java.util.ArrayList;

public class MethodNode implements Node
{

	public String signature;

	public MethodNode(String signature)
	{
		this.signature = signature;
		calls = new ArrayList<MethodNode>();
		
	}

	public ArrayList<MethodNode> calls;

	public ArrayList<MethodNode> calledBy;

	@Override
	public ArrayList<Node> connections()
	{
		return null;
	}

	@Override
	public int connectionCount()
	{
		return 0;
	}

	@Override
	public String signature()
	{
		return signature;
	}

}
