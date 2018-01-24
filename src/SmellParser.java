import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;
import java.util.TreeSet;

public class SmellParser
{
	public Set<String> godClasses;
	public Set<String> featureEnvyClasses;
	public Set<String> longMethods;
	public Set<String> featureEnvyMethods;
	public Set<String> TypeCheckingMethods;

	public SmellParser()
	{
		godClasses = new TreeSet<String>();
		featureEnvyClasses = new TreeSet<String>();
		longMethods = new TreeSet<String>();
		featureEnvyMethods = new TreeSet<String>();
		TypeCheckingMethods = new TreeSet<String>();
	}

	public SmellParser(String gcPath, String fePath, String lmPath, String tcPath, String gcPathTACO, String lmPathTACO)
	{
		godClasses = new TreeSet<String>();
		featureEnvyClasses = new TreeSet<String>();
		longMethods = new TreeSet<String>();
		featureEnvyMethods = new TreeSet<String>();
		TypeCheckingMethods = new TreeSet<String>();
		
		parseGodClasses(gcPath);
		parseFeatureEnvies(fePath, true);
		parseLongMethods(lmPath);
		parseTypeCheckings(tcPath);
		parseGodClassesFromTACO(gcPathTACO);
		parseLongMethodsFromTACO(lmPathTACO);

		printSmellStats();
	}

	public Set<String> getClassesContainingSmells()
	{
		Set<String> classesContainingSmells = new TreeSet<String>();

		classesContainingSmells.addAll(getSmellyClasses());

		Set<String> smellyMethods = getSmellyMethods();
		for (String method : smellyMethods)
		{
			classesContainingSmells.add(method.split(":")[0]);
		}

		return classesContainingSmells;
	}

	public void parseSmells(String godClass, String featureEnvy, String longMethod, String typeChecking)
	{
		parseGodClasses(godClass);
		parseFeatureEnvies(featureEnvy, true);
		parseLongMethods(longMethod);
		parseTypeCheckings(typeChecking);

		printSmellStats();
	}

	public void parseSmellsWithoutFeatureEnvyClasses(String godClass, String featureEnvy, String longMethod,
			String typeChecking)
	{
		parseGodClasses(godClass);
		parseFeatureEnvies(featureEnvy, false);
		parseLongMethods(longMethod);
		parseTypeCheckings(typeChecking);

		printSmellStats();
	}

	public Set<String> getSmellyClasses()
	{
		Set<String> classes = new TreeSet<String>();

		classes.addAll(godClasses);
		classes.addAll(featureEnvyClasses);

		return classes;
	}

	public Set<String> getSmellyMethods()
	{
		Set<String> methods = new TreeSet<String>();

		methods.addAll(longMethods);
		methods.addAll(featureEnvyMethods);
		methods.addAll(TypeCheckingMethods);
		return methods;
	}

	private void printSmellStats()
	{
		System.out.println("God classes\t" + godClasses.size());
		System.out.println("Feature envy classes\t" + featureEnvyClasses.size());
		System.out.println("Feature envy methods\t" + featureEnvyMethods.size());
		System.out.println("Long methods\t" + longMethods.size());
		System.out.println("Type checkings\t" + TypeCheckingMethods.size());
		System.out.println();
		System.out.println("Smelly classes\t" + getSmellyClasses().size());
		System.out.println("Smelly methods\t" + getSmellyMethods().size());
		System.out.println("Classes containing smells\t" + getClassesContainingSmells().size());
	}

	private void printMethods()
	{
		Set<String> smellyMethods = getSmellyMethods();

		for (String methodNode : smellyMethods)
		{
			System.out.println(methodNode);
		}

		System.out.println("Number of methods " + smellyMethods.size());
	}

	private void printClasses()
	{
		Set<String> smellyClasses = new TreeSet<String>();

		for (String classNode : smellyClasses)
		{
			System.out.println(classNode);
		}

		System.out.println("Number of classes " + smellyClasses.size());
	}

	private void parseTypeCheckings(String typeChecking)
	{
		Set<String> lines = getUniqueLines(typeChecking);

		TypeCheckingMethods = new TreeSet<String>();

		for (String line : lines)
		{
			String[] parts = line.split("::", 2);

			if (parts.length == 2)
			{
				String classPortion = parts[0];
				String methodPortion = parts[1];

				methodPortion = processMethodSignature(methodPortion);

				String[] methodParts = methodPortion.split(" ");

				methodPortion = methodParts[methodParts.length - 1];

				methodPortion = classPortion + ":" + methodPortion;

				TypeCheckingMethods.add(methodPortion);

			} else
			{
				System.out.println("**parseTypeCheckings " + parts.length + " " + line);
			}

		}

	}

	private void parseLongMethods(String longMethod)
	{
		Set<String> lines = getUniqueLines(longMethod);

		longMethods = new TreeSet<String>();

		for (String line : lines)
		{
			String[] parts = line.split("\t", 5);

			if (parts.length == 5)
			{
				String classPortion = parts[0];
				String methodPortion = parts[1];

				methodPortion = processMethodSignature(methodPortion);

				String[] methodParts = methodPortion.split(" ");

				methodPortion = methodParts[methodParts.length - 1];

				methodPortion = classPortion + ":" + methodPortion;

				longMethods.add(methodPortion);

			} else
			{
				System.out.println("**parseLongMethods " + parts.length + " " + line);
			}

		}

	}
	
	private void parseLongMethodsFromTACO(String longMethod)
	{
		Set<String> lines = getUniqueLines(longMethod);

		longMethods = new TreeSet<String>();

		for (String line : lines)
		{
			if(line.length()<5) continue;
			String[] parts = line.split(" And ");
			
			String className = parts[0].replaceAll("/", ".");
			if(className.indexOf("src.main.java.")!=-1){
				className = className.split(".src.main.java.")[1];
			}
			else {
				className = className.split(".src.")[1];
			}
			
			
			String classPortion = className.substring(0, className.length()-5);
			
			String methodSignature = parts[1].substring(8).split("\\(")[0];
			String[] partsOfMethod = methodSignature.split(" ");
			String methodPortion = partsOfMethod[partsOfMethod.length-1];
			
			methodPortion = classPortion + ":" + methodPortion;
			
			if(!longMethods.contains(methodPortion)){
				longMethods.add(methodPortion);
			}

		}

	}

	private void parseFeatureEnvies(String featureEnvy, boolean withFeatureEnvyClasses)
	{
		Set<String> lines = getUniqueLines(featureEnvy);

		featureEnvyClasses = new TreeSet<String>();
		featureEnvyMethods = new TreeSet<String>();

		for (String line : lines)
		{
			String[] parts = line.split("\t", 5);

			if (parts.length == 5)
			{
				String methodSignature = processMethodSignature(parts[1]);

				if (methodSignature != null)
					featureEnvyMethods.add(methodSignature);
				if (withFeatureEnvyClasses)
					featureEnvyClasses.add(parts[2]);
			} else
			{
				System.out.println("**parseFeatureEnvies " + parts.length + " " + line);
			}

		}
	}

	private String processMethodSignature(String methodSignature)
	{
		methodSignature = methodSignature.replace("(", "\t");
		String[] parts = methodSignature.split("\t", 2);
		if (parts.length == 2)
		{
			methodSignature = parts[0].replace("::", ":");
			return methodSignature;
		} else
		{
			System.out.println("**processMethodSignature " + parts.length + " " + methodSignature);
			return "Error";
		}
	}

	private void parseGodClasses(String godClassFile)
	{
		Set<String> lines = getUniqueLines(godClassFile);

		godClasses = new TreeSet<String>();

		for (String line : lines)
		{
			String[] parts = line.split("\t");
			godClasses.add(parts[0]);
		}
	}
	
	private void parseGodClassesFromTACO(String godClassFile)
	{
		Set<String> lines = getUniqueLines(godClassFile);

		godClasses = new TreeSet<String>();

		for (String line : lines)
		{
			if(line.length()<5) continue;
			String className = line.replaceAll("/", ".");
			if(className.indexOf("src.main.java.")!=-1){
				className = className.split(".src.main.java.")[1];
			}
			else {
				//System.out.println(className);
				className = className.split(".src.")[1];
			}
			
			className = className.substring(0, className.length()-5);
			if(!godClasses.contains(className)){
				godClasses.add(className);
			}
		}
	}

	private Set<String> getUniqueLines(String filePath)
	{
		Set<String> lines = new TreeSet<String>();
		try
		{
			File file = new File(filePath);

			if (!file.exists())
			{
				System.err.println("file " + filePath + " does not exist");
			} else
			{
				try (BufferedReader br = new BufferedReader(new FileReader(file)))
				{
					String line;

					while ((line = br.readLine()) != null)
					{
						if (line.contains("Rate it!"))
						{
							continue;
						}
						if (line.contains("current system"))
						{
							continue;
						}
						line = line.replace("\"", "");
						lines.add(line);
					}
				}
			}
		} catch (Exception e)
		{
			System.err.println("Error while creating line list from - " + filePath + " : " + e.getMessage());
			e.printStackTrace();
		}

		return lines;
	}

}
