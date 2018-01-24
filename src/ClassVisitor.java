
/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.List;
import java.util.Dictionary;
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
public class ClassVisitor extends EmptyVisitor
{

	private JavaClass clazz;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private Set<String> smellyClasses, smellyMethods;
	public int classRelationCount = 0;
	public MethodVisitor methodVisitor;
	public Set<String> classRelations;

	public ClassVisitor(Set<String> smellyClasses, Set<String> smellyMethods)
	{
		this.smellyClasses = smellyClasses;
		this.smellyMethods = smellyMethods;
		methodVisitor = new MethodVisitor(smellyMethods);
		classRelations = new TreeSet<String>();
	}

	public void visitJavaClass(JavaClass jc)
	{
		jc.getConstantPool().accept(this);
		Method[] methods = jc.getMethods();
		for (int i = 0; i < methods.length; i++)
		{
			MethodGen mg = new MethodGen(methods[i], clazz.getClassName(), constants);
			if (smellyMethods.contains(clazz.getClassName() + ":" + mg.getName()))
			{
				methods[i].accept(this);
			}
		}
	}

	public void visitConstantPool(ConstantPool constantPool)
	{
		for (int i = 0; i < constantPool.getLength(); i++)
		{
			Constant constant = constantPool.getConstant(i);
			if (constant == null)
				continue;
			if (constant.getTag() == 7)
			{
				String referencedClass = constantPool.constantToString(constant);
				if (smellyClasses.contains(referencedClass) && smellyClasses.contains(clazz.getClassName()))
				{
					if (!referencedClass.equals(clazz.getClassName()))
					{
						//System.out.println(String.format(classReferenceFormat, referencedClass));
						classRelations.add(clazz.getClassName() + " " + referencedClass);
						classRelationCount++;
					}
				}
			}
		}
	}

	public void visitMethod(Method method)
	{
		MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
		methodVisitor.start(mg, clazz);
	}

	public void start(JavaClass jc)
	{
		clazz = jc;
		constants = new ConstantPoolGen(clazz.getConstantPool());
		classReferenceFormat = "C:" + clazz.getClassName() + " %s";
		visitJavaClass(clazz);
	}
}