package tracing.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class RequirementsIndicesView extends ViewPart implements ISelectionProvider {

	static double duration;
	static Text indicesText;
	static String requirementPath = "", filePath1 = "", filePath2 = "";
	static boolean checkBoxTokenizing = false, checkBoxAcronym = false,
			checkBoxStopWords = false, checkBoxStemming = false;

	public RequirementsIndicesView() { }

	public static void showMessage() {
		// Sets all the boxes to unchecked when the message opens.
		checkBoxTokenizing = false;
		checkBoxAcronym = false;
		checkBoxStopWords = false;
		checkBoxStemming = false;
		Shell shell = new Shell(SWT.ON_TOP);
		shell.setSize(800, 1000);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		shell.setLayout(layout);

		GridData labelData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		GridData checkboxData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		GridData textboxData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		Label label = new Label(shell, SWT.LEFT);
		label.setText("Requirements source folder: ");
		label.setLayoutData(labelData);

		Text tBoxForRequirements = new Text(shell, SWT.LEFT);
		tBoxForRequirements.setText("(path name)");
		tBoxForRequirements.setLayoutData(textboxData);

		Button buttonRequirement = new Button(shell, SWT.PUSH);
		buttonRequirement.setText("Browse");
		buttonRequirement.setLayoutData(buttonData);

		// need 4 blank items here
		for (int i = 0; i < 4; i++) {
			new Label(shell, SWT.NONE);
		}

		Button buttonCheckTokenizing = new Button(shell, SWT.CHECK);
		buttonCheckTokenizing.setText("tokenizing");
		buttonCheckTokenizing.setLayoutData(checkboxData);

		// need 2 blank items here
		for (int i = 0; i < 2; i++) {
			new Label(shell, SWT.NONE);
		}

		Button buttonCheckAcronym = new Button(shell, SWT.CHECK);
		buttonCheckAcronym.setText("Restore acronyms");
		buttonCheckAcronym.setLayoutData(checkboxData);

		Button buttonFile1 = new Button(shell, SWT.PUSH);
		buttonFile1.setText("Browse");
		buttonFile1.setLayoutData(buttonData);

		Text tBoxForButton1 = new Text(shell, SWT.LEFT);
		tBoxForButton1.setText("(File name)");
		tBoxForButton1.setLayoutData(textboxData);

		Button buttonCheckStopWords = new Button(shell, SWT.CHECK);
		buttonCheckStopWords.setText("Remove stop words");
		buttonCheckStopWords.setLayoutData(checkboxData);

		Button buttonFile2 = new Button(shell, SWT.PUSH);
		buttonFile2.setText("Browse");
		buttonFile2.setLayoutData(buttonData);

		Text tBoxForButton2 = new Text(shell, SWT.LEFT);
		tBoxForButton2.setText("(File name)");
		tBoxForButton2.setLayoutData(textboxData);

		Button buttonCheckStemming = new Button(shell, SWT.CHECK);
		buttonCheckStemming.setText("Stem");
		buttonCheckStemming.setLayoutData(checkboxData);

		// need 5 blank items here
		for (int i = 0; i < 5; i++) {
			new Label(shell, SWT.NONE);
		}

		Button OK = new Button(shell, SWT.PUSH);
		OK.setText("OK");
		OK.setLayoutData(buttonData);
		OK.setEnabled(false);

		buttonRequirement.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog fd = new DirectoryDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String selected = fd.open();
				tBoxForRequirements.setText(selected);
				requirementPath = selected;
				RequirementsView.setDefaultText();
				if (!Helper.isNullOrEmpty(requirementPath)) {
					OK.setEnabled(true);
				} else {
					OK.setEnabled(false);
				}
			}
		});

		buttonFile1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.txt" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				tBoxForButton1.setText(selected);
				filePath1 = selected;
			}
		});

		buttonFile2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// system.out.println("filebutton2 Browse pressed");
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.txt" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				tBoxForButton2.setText(selected);
				filePath2 = selected;
			}
		});

		shell.setDefaultButton(buttonFile1);

		buttonCheckTokenizing.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxTokenizing = buttonCheckTokenizing.getSelection();
			}
		});

		buttonCheckAcronym.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxAcronym = buttonCheckAcronym.getSelection();
			}
		});

		buttonCheckStopWords.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxStopWords = buttonCheckStopWords.getSelection();
			}
		});

		buttonCheckStemming.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxStemming = buttonCheckStemming.getSelection();
			}
		});

		OK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Runner.main();
				shell.close();
			}
		});

		shell.pack();
		shell.open();
	}

	// After file is chosen from the drop down menu, this function is called to
	// index the text based how it is chosen to be indexed in the message box.
	public static String indexText(String file_Text) {
		if (checkBoxTokenizing == true) {
			file_Text = Helper.tokenize(file_Text, true);
		}
		if (checkBoxAcronym == true) {
			// Makes sure there is a acronym file entered.
			if (!Helper.isNullOrEmpty(filePath1)) {
				file_Text = Helper.restoreAcronym(file_Text, filePath1);
			}
		}
		if (checkBoxStopWords == true) {
			// Makes sure there is a stop words file entered.
			if (!Helper.isNullOrEmpty(filePath2)) {
				file_Text = Helper.removeStopWords(file_Text, filePath2);
			}
		}
		if (checkBoxStemming == true) {
			file_Text = Helper.stemFile(file_Text);
		}
		return file_Text;
	}

	// TODO should this return a string?
	public static void setReqText(String file_Text) {
		file_Text = indexText(file_Text);
		indicesText.setText(file_Text);
	}

	// Stores the results of the indexed files in a new directory called
	// 'Indexed Output' in the same directory that was chosen for the files.
	public static void storeResults(String input_Path) {
		String output_Path = requirementPath + "\\Indexed Output";
		File dir = new File(output_Path);
		// if the directory does not exist, create it
		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (Exception pathNotSet) {
				pathNotSet.printStackTrace();
			}
		}
		Vector<Vector<String>> dir_Set = new Vector<Vector<String>>();
		dir_Set = RequirementsView.dirToText(input_Path);
		
		duration = 0; // set duration (time to parse files) to zero , we are about to parse all files below
		for (int i = 0; i < (dir_Set.get(0).size()); i++) {
			double endTime = (System.nanoTime()) / 1000000000.0;
			String indexed_Text = indexText(Helper.readTextFile(
					dir_Set.get(1).get(i)).toString());
			duration += Math.abs(endTime
					- ((System.nanoTime()) / 1000000000.0)); 
			/* The above indexed_text line parses a single file at a time, to calculate the total time to parse all files 
			 * we should not calculate what is below this comment because the time to create the output files should not be
			 * considered in the time to parse all the files. */
			
			// The new indexed files are the same name followed by '_indeces'
			String iFile_Name = "\\" + (dir_Set.get(0).get(i)) + "_Indices.txt";
			try {
				File file = new File(output_Path + iFile_Name);
				// if file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(indexed_Text);
				bw.close();
			} catch (IOException pathNotValid) {
				pathNotValid.printStackTrace();
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// Set layout forum of parent composite
		parent.setLayout(new FormLayout());

		FormData formdata = new FormData();
		formdata.top = new FormAttachment(0, 5);
		formdata.left = new FormAttachment(0, 10);
		formdata.right = new FormAttachment(0, 200);

		// Create title label
		Label titleLabel = new Label(parent, SWT.SINGLE);
		titleLabel.setText("Requirements Indices:");
		titleLabel.setLayoutData(formdata);

		// Create text area
		indicesText = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
		indicesText.setText("Select a file from the drop down menu to indice.");
		formdata = new FormData();
		formdata.top = new FormAttachment(titleLabel, 10);
		formdata.bottom = new FormAttachment(titleLabel, 230);
		formdata.left = new FormAttachment(0, 10);
		formdata.right = new FormAttachment(0, 800);
		indicesText.setLayoutData(formdata);

		Button manageButton = new Button(parent, SWT.PUSH);
		manageButton.setText("Manage...");
		formdata = new FormData();
		formdata.top = new FormAttachment(indicesText, 10);
		formdata.left = new FormAttachment(0, 730);
		manageButton.setLayoutData(formdata);

		manageButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// When manage button is pressed it will show the message window.
				showMessage();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

		});
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
