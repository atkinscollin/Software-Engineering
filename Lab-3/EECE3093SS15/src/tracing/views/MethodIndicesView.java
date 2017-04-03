package tracing.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class MethodIndicesView extends ViewPart implements ISelectionProvider {

	static Text indicedText;
	public static Map<String, String> dictionary = new HashMap<String, String>();
	static boolean complete = false;
	public static double  durationMethods;
	public static int totalMethods;

	/* populates the dictionary property on this class with all of the methods in the ITrust project where the
	  keys are the method names and the values are the method bodies*/
	public static void populateDictionaryWithMethods() {
		IProject project =  ResourcesPlugin.getWorkspace().getRoot().getProject("iTrust");
		IJavaProject javaProject = JavaCore.create(project);
		
		double startTime = (System.nanoTime()) / 1000000000.0;
	
		try {
			IPackageFragment[] f = javaProject.getPackageFragments();
			for (IPackageFragment frag : f) {
				ICompilationUnit[] cUnits = frag.getCompilationUnits();
				if (cUnits != null) {
					for (ICompilationUnit c : cUnits) {
						IType[] typeArray = c.getTypes();
						for (IType t : typeArray) {
							for (IMethod m : getMethods(t)) {
								String methName = m.getElementName();
								String methBod = getMethodBody(m);
								dictionary.put(methName, methBod); 
							}
						}
					}
				}
			}
		}
		catch (Exception e){}
		
		MethodIndicesView.durationMethods += Math.abs(startTime
				- ((System.nanoTime()) / 1000000000.0)); // calculates total time it takes to parse all methods in iTrust codebase
		MethodIndicesView.totalMethods = MethodIndicesView.dictionary.keySet().size();
		indicedText.setText("Indexing time of " + MethodIndicesView.totalMethods + " in " + MethodIndicesView.durationMethods + " seconds.\n");
		complete = true;
	}
	
	// Gets the methods from the dictionary and returns an array of them.
	private static IMethod[] getMethods(IType type) {
		IMethod[] methArray = null;
		try { methArray = type.getMethods();
		} catch (JavaModelException e) { e.printStackTrace(); }
		return methArray;
	}
	
	/* Takes in an IMethod m and gets the body of that method as a string and returns it.*/
	private static String getMethodBody(IMethod m) {
		String methodBod = "";
		try { methodBod = m.getSource();
		} catch(Exception e) { e.printStackTrace(); }
		return methodBod;
	}
	
	// The listener we register with the selection service 
	private ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			showSelection(sourcepart, selection);
		}
	};
	
	// When a method is selected from the iTrust project, it will show it in the methodIndices box at the bottom
	public void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			String meth_Name = ss.toString();
			int firstSpace = meth_Name.indexOf(" ");
			int lastOpenParenth = meth_Name.indexOf("(");
			if (firstSpace > -1 && lastOpenParenth > -1) {
				meth_Name = meth_Name.substring(firstSpace + 1, lastOpenParenth);
				// tokenizeWithComments only accepts a List<string> and .split makes it an array, so need to convert array to list
				List<String> methodBodyAsList = new ArrayList<String>();
				boolean dicContainsKey = dictionary.containsKey(meth_Name);
				if (dicContainsKey) {
					String[] methodAsStringArray = dictionary.get(meth_Name).split("\n");
					for(String line : methodAsStringArray) {
						methodBodyAsList.add(line);
					}
					methodBodyAsList = Helper.tokenizeWithComments(methodBodyAsList);
					methodBodyAsList = Helper.Join(methodBodyAsList);
					String finalMethBody = Helper.tokenizeCamelCase(Helper.ListStringToString(methodBodyAsList));
					setMethText(meth_Name, finalMethBody);	
				}	
			}
		}
	}
		
	/* 
	 * takes in a method name and method body and returns the combined string to display to the user when they click said method
	 * sets the indicedText to this new combined string, which is then displayed for the user to see.
	 */
	private static void setMethText(String meth_Name, String meth_Text) {
		indicedText.setText("Method name: " + meth_Name + "\n\nMethod indiced: " + meth_Text);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());

		FormData formdata = new FormData();
		formdata.top = new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,200);

		//Create title label
		Label titleLabel = new Label(parent,SWT.SINGLE);
		titleLabel.setText("Method Indices:");
		titleLabel.setLayoutData(formdata);

		//Create text area
		indicedText = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.H_SCROLL);
		indicedText.setText("Select a method from the package explorer.");
		formdata = new FormData();
		formdata.top = new FormAttachment(titleLabel,10);
		formdata.bottom = new FormAttachment(titleLabel,230);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,800);
		indicedText.setLayoutData(formdata);
		
		// Populates dictionary.
		MethodIndicesView.populateDictionaryWithMethods();
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub
	}
}