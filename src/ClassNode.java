import java.util.ArrayList;

public class ClassNode implements Node
{
	public String signature;
	
	public ClassNode(String signature)
	{
		this.signature = signature;
		contains = new ArrayList<MethodNode>();
	}

	public ArrayList<MethodNode> contains;

	public ArrayList<ClassNode> uses;

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
