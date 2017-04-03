package tracing.views;
import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class Runner {
	
	// TODO Change to your correct ITrustPath
	final static String ITrustPath = "C:\\Users\\atkin_000\\git\\Lab-3\\iTrust"; 
	
	// feature 10
	// will not work if iTrust project has not been imported into the workspace
	private static void addProject(){
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					org.eclipse.core.runtime.Path projectDotProjectFile = new org.eclipse.core.runtime.Path(ITrustPath + "/.project");
					IProjectDescription projectDescription = workspace.loadProjectDescription( projectDotProjectFile);
					IProject project = workspace.getRoot().getProject(projectDescription.getName());
					org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
				} catch (CoreException e) { e.printStackTrace(); }
			}
		};	
		//The workbench now does the work
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(runnable);
	}
	
	public static void main() {
		//Checks if the acronym and stop words box were checked but no file was entered. If true then it will ignore the box check.
		if(RequirementsIndicesView.checkBoxAcronym == true) {
			if(!Helper.isNullOrEmpty(RequirementsIndicesView.filePath1)) {
				RequirementsIndicesView.checkBoxAcronym = false;
			}
			else{
				JOptionPane.showMessageDialog(null, "No Acronym file selected, ignoring restore acronyms.");
			}
		}
		
		if(RequirementsIndicesView.checkBoxStopWords == true) {
			if(!Helper.isNullOrEmpty(RequirementsIndicesView.filePath2)) {
				RequirementsIndicesView.checkBoxStopWords = false;
			}
			else{
				JOptionPane.showMessageDialog(null, "No Stop List file selected, ignoring remove stop words.");
			}
		}
		
		// Sets the default text in the drop down box.
		RequirementsView.setDefaultText();
		// Sets up the drop box 
		RequirementsView.setUpDropBox(RequirementsIndicesView.requirementPath);
		
		// Stores the results of the indexed files
		RequirementsIndicesView.storeResults(RequirementsIndicesView.requirementPath);
		
		// Add iTrust project to eclipse application project explorer 
		addProject();
	}
}