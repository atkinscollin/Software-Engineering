package tracing.views;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Vector;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class RequirementsView extends ViewPart implements ISelectionProvider{
	
	private ISelection selection;
	private ComboViewer comboViewer;
	static Combo combo;
	
	//Constructor
	public RequirementsView() { RequirementsIndicesView.showMessage(); }

	/*
	 * Takes in a string of path name for a directory. Finds all the files in that directory
	 * and makes two vectors of strings. Vector file_Name holds all the file names and the Vector file_Paths
	 * holds all the paths. Returns a vector of both these vectors called ret_Dir.
	 */
	public static Vector<Vector<String>> dirToText(String pathName) {
		Path path = Paths.get(pathName);
		Vector<Vector<String>> ret_Dir = new Vector<Vector<String>>();
		Vector<String> fileNames = new Vector<String>();
		Vector<String> filePaths = new Vector<String>();
		String fileName = "";
		boolean check;
		int ext_Index;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path file: stream) {
				fileName = file.getFileName().toString();
				check = fileName.endsWith(".txt");
				ext_Index = fileName.lastIndexOf('.');
				if(check == true  && ext_Index > 0) {
					fileNames.add(fileName.substring(0, ext_Index));
					filePaths.add(file.toString());
				}
			}	
		} catch (IOException pathNotValid) { pathNotValid.printStackTrace(); }
		ret_Dir.add(fileNames);
		ret_Dir.add(filePaths);
		return ret_Dir;
	}	

	// Sets up the drop box by using the combo selection. Drop down items are all .txt files from the selected directory.
	public static void setUpDropBox(String path_Name) {
		combo.removeAll();
		combo.add("Choose Use Case");
		combo.select(0);
		Vector<Vector<String>> dir_Set = new Vector<Vector<String>>();
		dir_Set = dirToText(path_Name);
		String file_Name = "";
		for(int i = 0; i < (dir_Set.get(0).size()); i++) {
			file_Name = (dir_Set.get(0)).get(i);
			combo.add(file_Name);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());

		//Create a drop box
		comboViewer = new ComboViewer(parent,SWT.NONE|SWT.DROP_DOWN);
		combo = comboViewer.getCombo();

		//Set combo position
		FormData formdata = new FormData();
		formdata.top = new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,290);
		combo.setLayoutData(formdata);

		//Set text position
		Text text = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.H_SCROLL);
		formdata = new FormData();
		formdata.top = new FormAttachment(combo,10);
		formdata.bottom = new FormAttachment(combo,600);
		formdata.left = new FormAttachment(0,5);
		formdata.right = new FormAttachment(0,355);
		text.setLayoutData(formdata);

		//set text content
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DecimalFormat dTime =  new DecimalFormat("##.###");
				// Sets directory path
				Vector<Vector<String>> dir_Set = new Vector<Vector<String>>();
				try { dir_Set = dirToText(RequirementsIndicesView.requirementPath); 
				} catch(Exception pathNotSet) { pathNotSet.printStackTrace(); }	
				String file_Path = "";
				String file_Read = "";
				int useCase = combo.getItemCount() - 1;
				String time_Index = "Indexing time of " + useCase + " requirement(s) is: " + dTime.format(RequirementsIndicesView.duration) + " seconds.";
				
				// Solves what happens for each selection in the menu.
				int sel_Index = combo.getSelectionIndex();
				if(sel_Index == 0) {
					text.setText(time_Index);
				}
				else if(sel_Index >= 1) {
					file_Path = dir_Set.get(1).get(sel_Index - 1);
					file_Read = Helper.ListStringToString(Helper.readTextFile(file_Path));
					text.setText(file_Read);
					RequirementsIndicesView.setReqText(file_Read);
				}
				else { text.setText(""); }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }

		});

		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection comboSelection = event.getSelection();
				setSelection(comboSelection);
			}
		});

	}

	public static void setDefaultText() {
		combo.select(0);
		combo.notifyListeners(SWT.Selection, new Event());
	}

	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
		SelectionChangedEvent event = new SelectionChangedEvent(comboViewer, selection);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
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
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
	}

}