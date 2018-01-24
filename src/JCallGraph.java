
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

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public class JCallGraph
{

	public ClassVisitor visitor;
	
	public void generate(String location, Set<String> classesToCheck, Set<String> smellyClasses, Set<String> smellyMethods)
	{
		ClassParser cp;
		try
		{
			File f = new File(location);

			if (!f.exists())
			{
				System.err.println("Jar file " + location + " does not exist");
			}

			int found = 0, notFound = 0;
			ArrayList<String> notFoundClasses = new ArrayList<String>();

			visitor = new ClassVisitor(smellyClasses, smellyMethods);
			for (String className : classesToCheck)
			{
				String classPath = className.replace(".", "/") + ".class";
				try
				{
					cp = new ClassParser(location, classPath);
					JavaClass javaClass = cp.parse();
					found++;
					visitor.start(javaClass);
				} catch (Exception e)
				{
					notFound++;
					notFoundClasses.add(className);
//					System.out.println(className + " - class not found in jar");
				}
			}
			
			System.out.println("Inter class relations\t" + visitor.classRelationCount);
			System.out.println("Inter method relations\t" + visitor.methodVisitor.methodRelationCount);

//			System.out.println(found + " found. " + notFound + " not found.");

//			for (String notFoundClass : notFoundClasses)
//			{
//				System.out.println("Not found - " + notFoundClass);
//			}

		} catch (Exception e)
		{
			System.err.println("Error while processing jar: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
