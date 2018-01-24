import java.util.TreeSet;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

/**
 * The simplest of method visitors, prints any invoked method
 * signature for all method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitorAll extends EmptyVisitor {

    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private String format;
	public TreeSet<String> methodRelations;
	private String methodName;
	public int methodRelationCount=0;

    public MethodVisitorAll() {
    	methodRelations = new TreeSet<String>();
    }

    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }

    public void start(MethodGen m, JavaClass jc) {

        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + argumentList(mg.getArgumentTypes()) + ")"
            + " " + "(%s)%s:%s(%s)";
        methodName = visitedClass.getClassName() + ":" + mg.getName();
        if (mg.isAbstract() || mg.isNative())
            return;
        for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    private boolean visitInstruction(Instruction i)
	{
		short opcode = i.getOpcode();

		return ((InstructionConstants.INSTRUCTIONS[opcode] != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
    	String referenceMethodName = i.getReferenceType(cp) + ":" + i.getMethodName(cp);
		if (!referenceMethodName.equals(methodName))
		{
			methodRelations.add(methodName + " " + referenceMethodName);
			//System.out.println(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp)));
			methodRelationCount++;
		}
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
    	String referenceMethodName = i.getReferenceType(cp) + ":" + i.getMethodName(cp);
		if (!referenceMethodName.equals(methodName))
		{
			methodRelations.add(methodName + " " + referenceMethodName);
			//System.out.println(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp)));
			methodRelationCount++;
		}
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
    	String referenceMethodName = i.getReferenceType(cp) + ":" + i.getMethodName(cp);
		if (!referenceMethodName.equals(methodName))
		{
			methodRelations.add(methodName + " " + referenceMethodName);
			//System.out.println(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp)));
			methodRelationCount++;
		}
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
    	String referenceMethodName = i.getReferenceType(cp) + ":" + i.getMethodName(cp);
		if (!referenceMethodName.equals(methodName))
		{
			methodRelations.add(methodName + " " + referenceMethodName);
			//System.out.println(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp)));
			methodRelationCount++;
		}
    }

    public void visitINVOKEDYNAMIC(INVOKESTATIC i) {
    	String referenceMethodName = i.getReferenceType(cp) + ":" + i.getMethodName(cp);
		if (!referenceMethodName.equals(methodName))
		{
			methodRelations.add(methodName + " " + referenceMethodName);
			//System.out.println(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp)));
			methodRelationCount++;
		}
    }
}