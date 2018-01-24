
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ControlPanel
{

	protected Shell shlSkitSmell;
	private Text outputLocation;
	private Label lblOutputDirectory;
	private ScrolledComposite scrolledComposite;
	private ScrolledComposite orderedVersionPane;
	
	private ArrayList<Version> versions;
	private ArrayList<File> files;
	private Composite composite;
	private Button btnCheckButton;
	private Composite composite_1;
	private Label lblNewLabel;
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{	
		try
		{
			ControlPanel window = new ControlPanel();
			window.open();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open()
	{
		Display display = Display.getDefault();
		createContents();
		shlSkitSmell.open();
		shlSkitSmell.layout();
		while (!shlSkitSmell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		shlSkitSmell = new Shell();
		shlSkitSmell.setSize(450, 422);
		shlSkitSmell.setText("SCIT - Smell Cluster Inspection Toolkit");
		shlSkitSmell.setLayout(null);

		outputLocation = new Text(shlSkitSmell, SWT.BORDER);
		outputLocation.setBounds(10, 28, 414, 21);

		lblOutputDirectory = new Label(shlSkitSmell, SWT.NONE);
		lblOutputDirectory.setBounds(190, 7, 111, 15);
		lblOutputDirectory.setText("Select Project");
		
		Button btnSearchCodeSmell = new Button(shlSkitSmell, SWT.NONE);
		btnSearchCodeSmell.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				File baseFolder = new File (outputLocation.getText());
				
				if(!baseFolder.exists())
				{
					JOptionPane.showMessageDialog(new JFrame(), baseFolder.getPath() +  " not found");
					return;
				}
				
				files = new ArrayList<File>();
				
				composite = new Composite(scrolledComposite, SWT.NONE);
				composite.setLayout(new GridLayout(1, true));
				
				scrolledComposite.setContent(composite);
				scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				composite_1 = new Composite(orderedVersionPane, SWT.NONE);
				composite_1.setLayout(new GridLayout(1, false));
				
				orderedVersionPane.setContent(composite_1);
				orderedVersionPane.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				File[] directories = new File(baseFolder.getPath()).listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File file) {
				    	if(!file.isDirectory())
				    	{
				    		return false;
				    	}
				    	File jar = new File(file.getPath() + "\\jar.jar");
				    	File gc = new File(file.getPath() + "\\gc.txt");
				    	File lm = new File(file.getPath() + "\\lm.txt");
				    	File fe = new File(file.getPath() + "\\fe.txt");
				    	File tc = new File(file.getPath() + "\\tc.txt");
				    	
				    	if(!jar.exists() || !gc.exists() || !lm.exists() || !fe.exists() || !tc.exists() )
				    	{
				    		return false;
				    	}		
				    	
				    	btnCheckButton = new Button(composite, SWT.CHECK);
				    	btnCheckButton.setText(file.getName());
						btnCheckButton.setToolTipText(file.getPath());
				    	btnCheckButton.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								lblNewLabel = new Label(composite_1, SWT.NONE);
								lblNewLabel.setText( ( (Button)arg0.getSource()).getText() );
								orderedVersionPane.setContent(composite_1);
						    	files.add(new File(( (Button)arg0.getSource()).getToolTipText()));
							}
						});
						btnCheckButton.setText(file.getName());
						btnCheckButton.setToolTipText(file.getPath());
						scrolledComposite.setContent(composite);
				        return true;
				    }
				});		

							
			}

		});
		btnSearchCodeSmell.setBounds(10, 55, 414, 25);
		btnSearchCodeSmell.setText("Find Versions to Analyze");
		
		scrolledComposite = new ScrolledComposite(shlSkitSmell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setBounds(10, 107, 200, 234);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		
		
		orderedVersionPane = new ScrolledComposite(shlSkitSmell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		orderedVersionPane.setAlwaysShowScrollBars(true);
		orderedVersionPane.setExpandVertical(true);
		orderedVersionPane.setExpandHorizontal(true);
		orderedVersionPane.setBounds(224, 107, 200, 234);
		
		
		
		Label lblFoundVersions = new Label(shlSkitSmell, SWT.NONE);
		lblFoundVersions.setBounds(10, 86, 200, 15);
		lblFoundVersions.setText("Found Versions");
		
		Label lblVersionsToAnalyze = new Label(shlSkitSmell, SWT.NONE);
		lblVersionsToAnalyze.setText("Versions to Analyze (in order)");
		lblVersionsToAnalyze.setBounds(224, 86, 200, 15);
		
		Button btnAnalyze = new Button(shlSkitSmell, SWT.NONE);
		btnAnalyze.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				versions = new ArrayList<Version>();
				
				for (File file : files)
				{
					String path = file.getPath();					
					
					String versionName = extractVersionName(path);
					
					setOutputStream(versionName);

					Version v = new Version(versionName, path + "\\jar.jar" , path + "\\gc.txt", path + "\\fe.txt", path + "\\lm.txt", path + "\\tc.txt", path + "\\gcTACO.txt", path + "\\lmTACO.txt");
					
					versions.add(v);					
				}
				
				printReports();	
			}
		});
		btnAnalyze.setBounds(224, 347, 200, 25);
		btnAnalyze.setText("Analyze");
		
		Button btnClear = new Button(shlSkitSmell, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				files = new ArrayList<File>();
				
				composite = new Composite(scrolledComposite, SWT.NONE);
				composite.setLayout(new GridLayout(1, true));
				
				scrolledComposite.setContent(composite);
				scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				composite_1 = new Composite(orderedVersionPane, SWT.NONE);
				composite_1.setLayout(new GridLayout(1, false));
				
				orderedVersionPane.setContent(composite_1);
				orderedVersionPane.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		btnClear.setBounds(10, 347, 200, 25);
		btnClear.setText("Clear");
	}
	
	private void printReports()
	{
		String fileName = new File(outputLocation.getText()).getName();
		setOutputStream(fileName + " report");
		
		
		System.out.print(fileName + " Version Count\t" + versions.size());
		System.out.println();
		
		
		System.out.println();
		System.out.print("Version\t");
		for (Version v : versions)
		{
			System.out.print(v.name);
			System.out.print("\t");
		}		
		System.out.println();
		
		printSmells();
		
		
		printRelations();
		
		
		printGraphProperties();
		
		
		printClusteringDetail();
		
		
		printNewSmellDetail();
		
		
		printSmellEliminationDetail();
		
		
		printClusterSizeEvolution();
		
	}

	private void printSmellEliminationDetail()
	{
		int i = 1;
		Set<String> oldSmells = new TreeSet<String>();	
		
		int[][] oldSmellDetail = new int[4][versions.size()+1];

		Set<String> eliminatedSmells = new TreeSet<String>();		
		
		Version prev = null;
		
		for(Version v : versions)
		{
			if(i!=1)
			{
				Set<String> newSmells = new TreeSet<String>(v.smellyComponents);
				
				oldSmells = new TreeSet<String>(prev.smellyComponents);	
				
				oldSmells.removeAll(newSmells);
				
				eliminatedSmells.addAll(oldSmells);
				
				for(String smell : oldSmells)
				{
					if(prev.singleNodes.contains(smell))
					{
						oldSmellDetail[1][i]++;
					}
					else if(prev.largestCluster.contains(smell))
					{
						oldSmellDetail[3][i]++;
					}
					else
					{
						oldSmellDetail[2][i]++;
					}
				}
			}
			prev = v;				
			i++;
		}
		
		System.out.println();
		System.out.println("Eliminated Smell Detail\t");
		
		System.out.println("Total Number of Eliminated Smells\t" + eliminatedSmells.size());
/*		for(String smell : eliminatedSmells)
		{
			System.out.println(smell);
		}*/
		
		System.out.print("Eliminated Single-Node Smells\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(oldSmellDetail[1][j] + "\t");
		}
		System.out.println();
		
		System.out.print("Eliminated Smells Small Clusters\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(oldSmellDetail[2][j] + "\t");
		}
		System.out.println();
		
		System.out.print("Eliminated Smells in Largest Cluster\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(oldSmellDetail[3][j] + "\t");
		}
		System.out.println();
		
	}

	private void printNewSmellDetail()
	{
		int i = 1;
		Set<String> oldSmells = new TreeSet<String>();	
		
		int[][] newSmellDetail = new int[4][versions.size()+1];

		Set<String> uniqueSmells = new TreeSet<String>();		
		
		for(Version v : versions)
		{
			Set<String> newSmells = new TreeSet<String>(v.smellyComponents);
			uniqueSmells.addAll(newSmells);
			
			newSmells.removeAll(oldSmells);
			
			for(String smell : newSmells)
			{
				if(v.singleNodes.contains(smell))
				{
					newSmellDetail[1][i]++;
				}
				else if(v.largestCluster.contains(smell))
				{
					newSmellDetail[3][i]++;
				}
				else
				{
					newSmellDetail[2][i]++;
				}
			}
			oldSmells = new TreeSet<String>(v.smellyComponents);				
			i++;
		}
		
		System.out.println();
		System.out.println("New Smell Detail\t");
		
		System.out.println("Total Number of Unique Smells\t" + uniqueSmells.size());

		
		System.out.print("New Single-Node Smells\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(newSmellDetail[1][j] + "\t");
		}
		System.out.println();
		
		System.out.print("New Smells Small Clusters\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(newSmellDetail[2][j] + "\t");
		}
		System.out.println();
		
		System.out.print("New Smells in Largest Cluster\t");
		for(int j =1; j <= versions.size() ; j++)
		{
			System.out.print(newSmellDetail[3][j] + "\t");
		}
		System.out.println();
		
		JOptionPane.showMessageDialog(null, "Done!");
	
	}

	private void printClusterSizeEvolution()
	{
		System.out.println();
		System.out.println("Cluster Size Evolution\t");
		
		int highestNumberOfClusters = 0;
		for(Version v : versions)
		{
			if(v.multiNodeClusters.size() > highestNumberOfClusters)
			{
				highestNumberOfClusters = v.multiNodeClusters.size();
			}
		}
		
		int[][] clusterSizes = new int[highestNumberOfClusters+1][versions.size()+1];

		int j = 1;
		for(Version v : versions)
		{
			int[] ints = new int[highestNumberOfClusters+1];
			
			List<Set<String>> list = new ArrayList<Set<String>>(); 
			list.addAll(v.multiNodeClusters);
			
			int i = 1;
			for(Set<String> cluster : list)
			{
				ints[i] = cluster.size();
				i++;
			}
			
			Arrays.sort(ints);
			
			for(i = 1; i <= highestNumberOfClusters; i++)
			{
				clusterSizes[i][j] = ints[highestNumberOfClusters+1 - i];
			}
			
			j++;
		}
		
		for(int i = 1; i <= highestNumberOfClusters; i++)
		{
			System.out.print(i + "\t");
			
			for(j = 1; j <= versions.size(); j++)
			{
				System.out.print(clusterSizes[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printClusteringDetail()
	{
		System.out.println();
		System.out.println("Clustering Details\t");
		
		System.out.print("Average Cluster Size\t");
		for (Version v : versions)
		{
			System.out.print((float)(v.nodesInClusters )/v.multiNodeClusters.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Average Small Cluster Size\t");
		for (Version v : versions)
		{
			System.out.print((float)(v.nodesInSmallClusters )/v.multiNodeClusters.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Largest Cluster Percentage\t");
		for (Version v : versions)
		{
			System.out.print((float)v.largestClusterSize/ v.nodesInClusters * 100);
			System.out.print("\t");
		}	
		System.out.println();
	}

	private void printGraphProperties()
	{
		System.out.println();
		System.out.println("Graph Properties\t");
		
		System.out.print("Vertex Count\t");
		for (Version v : versions)
		{
			System.out.print(v.vertexCount);
			System.out.print("\t");
		}	
		System.out.println();
		
		
		System.out.print("Cluster Count\t");
		for (Version v : versions)
		{
			System.out.print(v.multiNodeClusters.size() + v.singleNodes.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		
		System.out.print("Single-Node Count\t");
		for (Version v : versions)
		{
			System.out.print(v.singleNodes.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Multiple-Node-Cluster Count\t");
		for (Version v : versions)
		{
			System.out.print(v.multiNodeClusters.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Nodes in Clusters\t");
		for (Version v : versions)
		{
			System.out.print(v.nodesInClusters);
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Smelly Classes in Clusters\t");
		for (Version v : versions)
		{
			Set<String> smellyClassesInClusters = new TreeSet<String>(v.smellyClasses);
			smellyClassesInClusters.removeAll(v.singleNodes);
			System.out.print(smellyClassesInClusters.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Smelly Methods in Clusters\t");
		for (Version v : versions)
		{
			Set<String> smellyMethodsInClusters = new TreeSet<String>(v.smellyMethods);
			smellyMethodsInClusters.removeAll(v.singleNodes);
			System.out.print(smellyMethodsInClusters.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Nodes in Largest Cluster\t");
		for (Version v : versions)
		{
			System.out.print(v.largestClusterSize);
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Nodes in Small Clusters\t");
		for (Version v : versions)
		{
			System.out.print(v.nodesInSmallClusters);
			System.out.print("\t");
		}	
		System.out.println();
	}

	private void printRelations()
	{
		System.out.println();
		System.out.println("Code Smell Relations\t");
		
		System.out.print("Contained Relations\t");
		for (Version v : versions)
		{
			System.out.print(v.containedRelations.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Used Relations\t");
		for (Version v : versions)
		{
			System.out.print(v.usedRelations.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Called Relations\t");
		for (Version v : versions)
		{
			System.out.print(v.calledRelations.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Total Architectural Relations\t");
		for (Version v : versions)
		{
			System.out.print(v.calledRelations.size() + v.usedRelations.size() + v.containedRelations.size());
			System.out.print("\t");
		}	
		System.out.println();
	}

	private void printSmells()
	{
		System.out.println();
		System.out.println("Code Smells\t");
		
		System.out.print("God Classes\t");
		for (Version v : versions)
		{
			System.out.print(v.godClasses.size());
			System.out.print("\t");
		}		
		System.out.println();
		
		System.out.print("Feature Envy Classes\t");
		for (Version v : versions)
		{
			System.out.print(v.featureEnvyClasses.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Feature Envy Methods\t");
		for (Version v : versions)
		{
			System.out.print(v.featureEnvyMethods.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Long Methods\t");
		for (Version v : versions)
		{
			System.out.print(v.longMethods.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Type Checking Methods\t");
		for (Version v : versions)
		{
			System.out.print(v.typeCheckingMethods.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		
		System.out.println();
		System.out.println("Smelly Componants\t");
		
		
		System.out.print("Smelly Classes\t");
		for (Version v : versions)
		{
			System.out.print(v.smellyClasses.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Smelly Methods\t");
		for (Version v : versions)
		{
			System.out.print(v.smellyMethods.size());
			System.out.print("\t");
		}	
		System.out.println();
		
		System.out.print("Classes Containing Smells\t");
		for (Version v : versions)
		{
			System.out.print(v.classesContainingSmells.size());
			System.out.print("\t");
		}	
		System.out.println();
	}
	
	private String extractVersionName(String path)
	{
		String name = path.replace("\\", "\t") ;
		String[] nameparts = name.split("\t");
		String versionName = nameparts[nameparts.length - 1];
		return versionName;
	}

	private void setOutputStream(String fileName)
	{
		PrintStream out = null;
		
		try
		{
			out = new PrintStream(
					new FileOutputStream(outputLocation.getText() + "\\" + fileName + ".txt"));
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		System.setOut(out);
	}
}
