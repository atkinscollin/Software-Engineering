package tracing.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

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

public class RequirementsIndicesView extends ViewPart implements ISelectionProvider{

	// member variables
	final static Charset ENCODING = StandardCharsets.UTF_8;
	static double duration;
	static Text indicesText;
	static String requirementPath = "", filePath1 = "", filePath2 = "";
	static boolean  checkBoxTokenizing = false, checkBoxAcronym = false, checkBoxStopWords = false, checkBoxStemming = false;

	
	private void showMessage() {
		checkBoxTokenizing = false; checkBoxAcronym = false; checkBoxStopWords = false; checkBoxStemming = false;

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

		Text tBoxForRequirements = new Text(shell , SWT.LEFT);
		tBoxForRequirements.setText("(path name)");
		tBoxForRequirements.setLayoutData(textboxData);

		Button buttonRequirement = new Button(shell, SWT.PUSH);
		buttonRequirement.setText("Browse");
		buttonRequirement.setLayoutData(buttonData);

		// need 4 blank items here
		for (int i = 0; i < 4; i ++){
			new Label(shell, SWT.NONE);
		}

		Button buttonCheckTokenizing = new Button(shell, SWT.CHECK);
		buttonCheckTokenizing.setText("tokenizing");
		buttonCheckTokenizing.setLayoutData(checkboxData);

		// need 2 blank items here
		for (int i = 0; i < 2; i ++){
			new Label(shell, SWT.NONE);
		}

		Button buttonCheckAcronym = new Button(shell, SWT.CHECK);
		buttonCheckAcronym.setText("Restore acronyms");
		buttonCheckAcronym.setLayoutData(checkboxData);

		Button buttonFile1 = new Button(shell, SWT.PUSH);
		buttonFile1.setText("Browse");
		buttonFile1.setLayoutData(buttonData);

		Text tBoxForButton1 = new Text(shell , SWT.LEFT);
		tBoxForButton1.setText("(File name)");
		tBoxForButton1.setLayoutData(textboxData);

		Button  buttonCheckStopWords = new Button(shell, SWT.CHECK);
		buttonCheckStopWords.setText("Remove stop words");
		buttonCheckStopWords.setLayoutData(checkboxData);

		Button buttonFile2 = new Button(shell, SWT.PUSH);
		buttonFile2.setText("Browse");
		buttonFile2.setLayoutData(buttonData);

		Text tBoxForButton2 = new Text(shell , SWT.LEFT);
		tBoxForButton2.setText("(File name)");
		tBoxForButton2.setLayoutData(textboxData);

		Button  buttonCheckStemming = new Button(shell, SWT.CHECK);
		buttonCheckStemming.setText("Stem");
		buttonCheckStemming.setLayoutData(checkboxData);

		// need 5 blank items here
		for (int i = 0; i < 5; i ++) {
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
				double endTime = (System.nanoTime())/1000000000.0; //time at which the files are all read
				RequirementsView.setDefaultText();
				duration = Math.abs(endTime - ((System.nanoTime())/1000000000.0));
				if (requirementPath != null && !requirementPath.isEmpty()){
					OK.setEnabled(true);
				}
				else{
					OK.setEnabled(false);
				}

			}
		});

		buttonFile1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//system.out.println("filebutton1 Browse pressed");
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.txt" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				tBoxForButton1.setText(selected);
				filePath1 = selected;
				//system.out.println(selected);
			}
		});

		buttonFile2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//system.out.println("filebutton2 Browse pressed");
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.txt" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				tBoxForButton2.setText(selected);
				filePath2 = selected;
				//system.out.println(selected);
			}
		});

		shell.setDefaultButton(buttonFile1);

		buttonCheckTokenizing.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxTokenizing = buttonCheckTokenizing.getSelection();
				//system.out.println("buttonCheckTokenizing pressed, boolean val = " + checkBoxTokenizing);

			}
		});

		buttonCheckAcronym.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxAcronym = buttonCheckAcronym.getSelection();
				//system.out.println("buttonCheckAcronym pressed, boolean val = " + checkBoxAcronym);
			}
		});

		buttonCheckStopWords.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxStopWords = buttonCheckStopWords.getSelection();
				//system.out.println("buttonCheckStopWords pressed, boolean val = " + checkBoxStopWords);
			}
		});

		buttonCheckStemming.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkBoxStemming = buttonCheckStemming.getSelection();
				//system.out.println("buttonCheckStemming pressed , boolean val = " + checkBoxStemming);
			}
		});

		OK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//system.out.println("OK pressed");
				main();
				shell.close();
			}
		});

		shell.pack();
		shell.open();
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

	public static String indexText(String file_Text) {
		if(checkBoxTokenizing == true) {
			file_Text = tokenize(file_Text);
		}
		if(checkBoxAcronym == true) {
			if(filePath1 != null && !filePath1.isEmpty()) {
				file_Text = restoreAcronym(file_Text);
			}
		}
		if(checkBoxStopWords == true) {
			if(filePath2 != null && !filePath2.isEmpty()) {
				file_Text = removeStopWords(file_Text);
			}
		}
		if(checkBoxStemming == true) {
			//file_Text = stemFile(file_Text);
		}
		return file_Text;
	}

	public static String setReqText(String file_Text) {
		file_Text = indexText(file_Text);
		indicesText.setText(file_Text);
		return file_Text;
	}

	public void storeResults(String input_Path, String output_Path) {
		Vector<Vector<String>> dir_Set = new Vector<Vector<String>>();
		dir_Set = RequirementsView.dirSolve(input_Path);
		for(int i = 0; i < (dir_Set.get(0).size()); i++) {
			String iFile_Name = "\\" + (dir_Set.get(0).get(i)) + "_Indices.txt";
			String indexed_Text = indexText(RequirementsView.readTextFile(dir_Set.get(1).get(i)).toString());
			try { File file = new File(output_Path + iFile_Name);
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(indexed_Text);
			bw.close();
			} catch (IOException path_Not_Valid) { path_Not_Valid.printStackTrace(); }
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		showMessage();

		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());

		FormData formdata = new FormData();
		formdata.top = new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,200);

		//Create title label
		Label titleLabel = new Label(parent,SWT.SINGLE);
		titleLabel.setText("Requirements Indices:");
		titleLabel.setLayoutData(formdata);

		//Create text area
		indicesText = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.H_SCROLL);
		indicesText.setText("This is a sample result.");
		formdata = new FormData();
		formdata.top = new FormAttachment(titleLabel,10);
		formdata.bottom = new FormAttachment(titleLabel,230);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,800);
		indicesText.setLayoutData(formdata);

		Button manageButton = new Button(parent,SWT.PUSH);
		manageButton.setText("Manage...");
		formdata = new FormData();
		formdata.top = new FormAttachment(indicesText,10);
		formdata.left = new FormAttachment(0,730);
		manageButton.setLayoutData(formdata);

		manageButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
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

	// Returns a list of words in string <input>
	public static String tokenize(String input) {
		String words = removePunctuation(input);
		return getWords(words);
	}

	// Removes any punctuation, line breaks, and extra whitespace
	public static String removePunctuation(String input) {
		char[] chars = input.toCharArray();
		String result = "";
		boolean whitespace = false;
		for(char c : chars){
			if(Character.isLetter(c)) {
				result += c;
				whitespace = false;
			}
			else if(Character.isDigit(c)){
				result += c;
				whitespace = false;
			}
			else {
				if(whitespace != true) {
					result += " ";
				}
				whitespace = true;
			}
		}
		return result.toString().trim();
	}

	// Returns each word in string, no punctuation allowed
	public static String getWords(String input) {
		input.split(" ");
		return input.toString();
	}

	// Restores all acronyms in a given string
	public static String restoreAcronym(String file_contents) {
		List<String> string_list = RequirementsView.readTextFile(filePath1);
		for(int i = 0; i < string_list.size(); i++) {
			String current_line = string_list.get(i);
			String[] mapping = current_line.split(":");
			int contains = file_contents.indexOf(mapping[0]);
			if(contains == -1) {
				continue;
			}
			else {
				contains = 0;
				while(contains != -1) {
					file_contents = file_contents.replace(mapping[0], mapping[1]);
					contains = file_contents.indexOf(mapping[0]);
				}
			}
		}
		return file_contents;
	}

	// Removes all stop words in a given token string
	public static String removeStopWords(String tokens) {
		List<String> string_list = RequirementsView.readTextFile(filePath2);
		String[] list_of_words;
		if(string_list.size() == 1) {
			list_of_words = string_list.get(0).split(",");
		}
		else {
			list_of_words = (String[]) string_list.toArray();
		}
		for(String word : list_of_words) {
			String l_word = " " + word + " ";
			String u_word = " " + word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
			if(tokens.contains(l_word)) {
				tokens = tokens.replaceAll(l_word, " ");
			}
			if(tokens.contains(u_word)) {
				tokens = tokens.replaceAll(u_word, " ");
			}
		}
		return tokens;
	}

	/** implements feature 9. Takes in a txt file, stems it then returns the stemmed result.
	 * 
	 * @param fileContent - (String) a .txt file to stem the contents of. 
	 * @return (String) the text content from the filePath file with all the words stemmed.  
	 */
	public static String stemFile(String fileContent){
        fileContent = RequirementsIndicesView.removePunctuation(fileContent);
        String[] array = fileContent.split(" ");
        ArrayList<String> tokTest = new ArrayList<String>();
        for (String x: array){
            tokTest.add(x);
        }
        
		ArrayList<String> stem = PorterAlgo.completeStem(tokTest);
		return stem.toString().replace(",", "");
        
	}
	
	public void main(){

		if(checkBoxAcronym == true) {
			if(filePath1 != null && !filePath1.isEmpty()) {
			}
			else{
				JOptionPane.showMessageDialog(null, "No Acronym file selected, ignoring Restore Acronyms...");
			}
		}
		if(checkBoxStopWords == true) {
			if(filePath2 != null && !filePath2.isEmpty()) {
			}
			else{
				JOptionPane.showMessageDialog(null, "No Stop List selected, ignoring Remove Stop Words...");
			}
		}
		double initialTime = (System.nanoTime())/1000000000.0;  //time at which the initialing the txt files starts
		RequirementsView.setUpDropBox(requirementPath);
		String output_Path = requirementPath + "\\Indexed Output";
		File dir = new File(output_Path);
		// if the directory does not exist, create it
		if (!dir.exists()) {
			//system.out.println("creating directory: ");
			boolean result = false;
			try { dir.mkdir();
			result = true;
			} catch(Exception path_Not_Set) { path_Not_Set.printStackTrace(); }        
			if(result) {    
				//system.out.println("DIR created");  
			}
		}
		storeResults(requirementPath, output_Path);
		double endTime = (System.nanoTime())/1000000000.0; //time at which the files are all read
		RequirementsView.setDefaultText();
		duration = (endTime - initialTime); //Calculation that is the duration of time it took to index
	}

}
