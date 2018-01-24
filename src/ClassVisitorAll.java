
import java.util.Set;
import java.util.TreeSet;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

/**
 * The simplest of class visitors, invokes the method visitor class for each
 * method found.
 */
public class ClassVisitorAll extends EmptyVisitor {

    private JavaClass clazz;
    private ConstantPoolGen constants;
    private String classReferenceFormat;
	public MethodVisitorAll visitor;
	public Set<String> classRelations;
	public int classRelationCount;
    
    public ClassVisitorAll() {
        classRelations = new TreeSet<String>();
        visitor = new MethodVisitorAll();
    }

    public void visitJavaClass(JavaClass jc) {
        jc.getConstantPool().accept(this);
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++)
            methods[i].accept(this);
    }

    public void visitConstantPool(ConstantPool constantPool) {
        for (int i = 0; i < constantPool.getLength(); i++) {
            Constant constant = constantPool.getConstant(i);
            if (constant == null)
                continue;
            if (constant.getTag() == 7) {
            	String referencedClass = constantPool.constantToString(constant);
				
				if (!referencedClass.equals(clazz.getClassName()))
				{
					//System.out.println(String.format(classReferenceFormat, referencedClass));
					classRelations.add(clazz.getClassName() + " " + referencedClass);
					classRelationCount++;
				}
            }
        }
    }

    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        visitor.start(mg, clazz); 
    }

    public void start(JavaClass jc) {
    	clazz = jc;
		constants = new ConstantPoolGen(clazz.getConstantPool());
		classReferenceFormat = "C:" + clazz.getClassName() + " %s";
		visitJavaClass(clazz);
    }
}