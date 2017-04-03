package tracing.views;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class Helper {
	final static Charset ENCODING = StandardCharsets.UTF_8;

	private Helper() {}

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

	//Reads a text file given a path name
	public static List<String> readTextFile(String pathName) {
		List<String> tempList = new ArrayList<String>();
		Path path = Paths.get(pathName); 
		try { tempList = Files.readAllLines(path, ENCODING);
		} catch (IOException pathNotFound) { pathNotFound.printStackTrace(); }
		return tempList;
	}

	// Turns a List of strings into a string, this is used rather than the toString() function because toString adds commas.
	public static String ListStringToString(List<String> list) {
		String result = "";
		for(int i = 0; i < list.size(); i++) {
			if(i == list.size() - 1) {
				result += list.get(i);
			}
			else {
				result += list.get(i) + "\n";
			}
		}
		return result;
	}

	// Tokenizes the function with comments kept in tact.
	public static List<String> tokenizeWithComments(List<String> text) {
		boolean in_block_comment = false;
		List<String> result = new ArrayList<String>(text.size());

		for (String curr_element : text) {
			// Code check in a string
			if (curr_element.contains("\"")) {
				String temp = curr_element;
				while (temp.contains("\"")) {
					int index = temp.indexOf("\"") + 1;
					int endex = temp.indexOf("\"", index);
					String sub = temp.substring(index, endex);
					if (sub.contains("//")) {
						sub = sub.replaceAll("//", "");
					}
					if (sub.contains("/*")) {
						sub = sub.replaceAll("/*", "");
					}
					if (sub.contains("*/")) {
						sub = sub.replaceAll("*/", "");
					}
					temp = temp.replace(temp.substring(index - 1, endex + 1),
							sub);
				}
			}

			if (in_block_comment) {
				// Found end of block comment
				if (curr_element.contains("*/")) {
					in_block_comment = false;
					int index = curr_element.indexOf("*/");
					// if end of comment is end of line
					if (index == curr_element.length() - 2) {
						result.add(curr_element); // Done
					}
					// Code before a block comment
					else {
						result.add(curr_element.substring(0, index + 1)
								+ tokenize(curr_element.substring(index + 2),
										false)); // Done
					}
				}
				// If still in block comment
				else {
					result.add(curr_element); // Done
				}
			} else {

				// Catches single line comment
				if (curr_element.contains("//")) {
					int index = curr_element.indexOf("//");
					if (index == 0) {
						result.add(curr_element); // Done
					}
					// Catches if comment isn't beginning
					else {
						result.add(tokenize(curr_element.substring(0, index),
								false) + curr_element.substring(index)); // Done
					}
				}
				// Catches block comments
				else if (curr_element.contains("/*")) {
					int index = curr_element.indexOf("/*");
					// If block comment starts at beginning
					if (index == 0) {
						in_block_comment = true;
						result.add(curr_element); // Done
					}
					// If block comment isn't the beginning
					else {
						in_block_comment = true;
						result.add(tokenize(curr_element.substring(0, index),
								false) + curr_element.substring(index)); // Done
					}
				}
				// Catches no comments
				else {
					result.add(tokenize(curr_element, false)); // Done
				}
			}
		}
		return result;
	}

	// Removes all stop words in a given token string
	// remember to pass filePath2 or whatever to this
	public static String removeStopWords(String tokens, String filePath) {
		List<String> string_list = Helper.readTextFile(filePath);

		String[] list_of_words;
		if (string_list.size() == 1) {
			list_of_words = string_list.get(0).split(",");
		} else {
			list_of_words = (String[]) string_list.toArray();
		}
		for (String word : list_of_words) {
			String l_word = " " + word + " ";
			String u_word = " " + word.substring(0, 1).toUpperCase()
					+ word.substring(1) + " ";
			if (tokens.contains(l_word)) {
				tokens = tokens.replaceAll(l_word, " ");
			}
			if (tokens.contains(u_word)) {
				tokens = tokens.replaceAll(u_word, " ");
			}
		}
		return tokens;
	}
	
	/* test to see if a string is null or empty so that you don't need to use longer if statements to check for both.
	 * returns boolean- true if string is null or empty, else false.
	 */
	public static boolean isNullOrEmpty(String str){
		if (str != null && !str.isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}

	/**
	 * implements feature 9. Takes in a txt file, stems it then returns the
	 * stemmed result.
	 * 
	 * @param fileContent
	 *            - (String) a .txt file to stem the contents of.
	 * @return (String) the text content from the filePath file with all the
	 *         words stemmed.
	 */
	public static String stemFile(String fileContent) {
		fileContent = Helper.removePunctuation(fileContent, true);
		String[] array = fileContent.split(" ");
		ArrayList<String> tokTest = new ArrayList<String>();
		for (String x : array) {
			tokTest.add(x);
		}
		ArrayList<String> stem = PorterAlgo.completeStem(tokTest);
		return stem.toString().replace(",", "");
	}

	// Restores all acronyms in a given string
	public static String restoreAcronym(String file_contents, String filePath) {
		List<String> string_list = Helper.readTextFile(filePath);
		for (int i = 0; i < string_list.size(); i++) {
			String current_line = string_list.get(i);
			String[] mapping = current_line.split(":");
			int contains = file_contents.indexOf(mapping[0]);
			if (contains == -1) {
				continue;
			} else {
				contains = 0;
				while (contains != -1) {
					file_contents = file_contents.replace(mapping[0],
							mapping[1]);
					contains = file_contents.indexOf(mapping[0]);
				}
			}
		}
		return file_contents;
	}

	// Returns each word in string, no punctuation allowed
	private static String getWords(String input) {
		input.split(" ");
		return input.toString();
	}

	// Returns a list of words in string <input>
	public static String tokenize(String input, boolean with_digits) {
		String words = removePunctuation(input, with_digits);
		return getWords(words);
	}

	// Removes any punctuation, line breaks, and extra whitespace
	public static String removePunctuation(String input, boolean allow_digits) {
		char[] chars = input.toCharArray();
		String result = "";
		boolean whitespace = false;
		for (char c : chars) {
			if (Character.isLetter(c)) {
				result += c;
				whitespace = false;
			} else if (Character.isDigit(c)) {
				if (allow_digits) {
					result += c;
					whitespace = false;
				} else {
					continue;
				}
			} else {
				if (whitespace != true) {
					result += " ";
				}
				whitespace = true;
			}
		}
		return result.toString().trim();
	}

	// tokenizes out camel case
	public static String tokenizeCamelCase(String data) {
		String result = data.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
		return result;
	}

	// joins a list of strings together into a string. join is on each new line.
	public static List<String> Join(List<String> list) {
		List<String> copy = list;
		int count = 0;
		for (String row : list){
			row += "\n";
			copy.set(count, row);
			count++;
		}
		return copy;
	}
}
