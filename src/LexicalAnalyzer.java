import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LexicalAnalyzer {

	/**
	 * @param args
	 */
	String filename;
	FileReader fr = null;
	int line = 1; 				// sign the current line
	ArrayList<Integer> symline; // store lines for every symbol;
								// if an error occurs,you can get its line

	ArrayList<Character> code;  // store the code read from the file

	ArrayList<String> sym;		//store the kind of every symbol
	ArrayList<String> id;		//store the value of an identify
	ArrayList<Integer> num;     //store the value of a number

	String keyWord[] = { "const", "var", "procedure", "begin", "end", "odd",
			"if", "then", "call", "while", "do", "read", "write" };

	public LexicalAnalyzer() {
		code = new ArrayList<Character>();
		sym = new ArrayList<String>();
		id = new ArrayList<String>();
		num = new ArrayList<Integer>();
		symline = new ArrayList<Integer>();
	}

	public void getFileName() {
		System.out.println("Please input the file name:");
		Scanner scan = new Scanner(System.in);
		filename = scan.next();
	}

	public void getCode() {
		try {
			fr = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			System.exit(0);
		}
		while (true) {
			try {
				int temp = fr.read();
				if (temp == -1) { // end reading?
					break;
				}
				code.add((char) temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean getSym() {
		for (int i = 0; i < code.size(); i++) {
			if (code.get(i) == ' ' || code.get(i) == '\t') { // ignore blank or
																// tab
				continue;
			}
			// First char is a letter?
			if (code.get(i) >= 'a' && code.get(i) <= 'z' || code.get(i) >= 'A'
					&& code.get(i) <= 'Z') {
				int k = 0; // sign the length of ident

				ArrayList<Character> temp = new ArrayList<Character>();
				for (k = 0; k < 11; k++) {

					if (k >= 10) { // if over 10,then error
						String str = ""; // print the error word
						for (int m = 0; m < temp.size(); m++) {
							str += temp.get(m);
						}
						str += code.get(i);

						System.out
								.println("Lexical error:ident is too long!---> line:"
										+ line + "  \"" + str + "\"");
						System.exit(0);
						return false;
					}
					temp.add(code.get(i));
					i++; // read the next char
					if (!(code.get(i) >= 'a' && code.get(i) <= 'z'
							|| code.get(i) >= 'A' && code.get(i) <= 'Z' || code
							.get(i) >= '0' && code.get(i) <= '9')) {
						// the next char is a letter or digit?
						i--;
						break;
					}
				}
				// the word is a keyword?
				String str = "";
				for (int m = 0; m < temp.size(); m++) {
					str += temp.get(m);
				}

				for (int j = 0; j < 14; j++) {
					if (j == 13) { // is an ident?
						sym.add("ident");
						id.add(str);
						num.add(null);
						symline.add(line);
						break;
					}

					if (str.equalsIgnoreCase(keyWord[j])) { // is a keyword
						sym.add(keyWord[j] + "sym");
						id.add(null);
						num.add(null);
						symline.add(line);
						break;
					}
				}
				// First char is a digit?
			} else if (code.get(i) >= '0' && code.get(i) <= '9') {
				ArrayList<Character> temp = new ArrayList<Character>();
				int k = 0;
				while (true) {
					temp.add(code.get(i));
					i++;
					if (code.get(i) >= 'a' && code.get(i) <= 'z' // error
							|| code.get(i) >= 'A' && code.get(i) <= 'Z') {

						String str = "";
						for (int m = 0; m < temp.size(); m++) {
							str += temp.get(m);
						}
						str += code.get(i);
						System.out
								.println("Lexical error:ident starts with digit \""
										+ str + "\" !----In line:" + line);
						System.exit(0);
						return false;
					}
					if (!(code.get(i) >= '0' && code.get(i) <= '9')) {
						i--;
						break;
					}
					k++;
				}
				String str = "";
				for (int m = 0; m < temp.size(); m++) {
					str += temp.get(m);
				}
				sym.add("number");
				id.add(null);
				num.add(Integer.parseInt(str));
				symline.add(line);
			} else if (code.get(i) == ':') {
				i++;
				if (code.get(i) == '=') { // is :=?
					sym.add(":=");
					id.add(null);
					num.add(null);
					symline.add(line);
				} else {
					i--;
					System.out
							.println("Lexical error:no such operator \" : \"!----In line: "
									+ line);
					System.exit(0);
					return false;
				}
			} else if (code.get(i) == '>') {
				i++;
				if (code.get(i) == '=') {
					sym.add(">=");
					id.add(null);
					num.add(null);
					symline.add(line);
				} else {
					i--;
					sym.add(">");
					id.add(null);
					num.add(null);
					symline.add(line);
				}
			} else if (code.get(i) == '<') {
				i++;
				if (code.get(i) == '=') {
					sym.add("<=");
					id.add(null);
					num.add(null);
					symline.add(line);
				} else if (code.get(i) == '>') {
					sym.add("<>");
					id.add(null);
					num.add(null);
					symline.add(line);
				} else {
					i--;
					sym.add("<");
					id.add(null);
					num.add(null);
					symline.add(line);
				}
			} else if (code.get(i) == '=' || code.get(i) == '#'
					|| code.get(i) == '*' || code.get(i) == '/'
					|| code.get(i) == '+' || code.get(i) == '-'
					|| code.get(i) == '(' || code.get(i) == ')'
					|| code.get(i) == '.' || code.get(i) == ';'
					|| code.get(i) == ',') {

				sym.add("" + code.get(i));
				id.add(null);
				num.add(null);
				symline.add(line);

			} else {
				if ((int) code.get(i) == 13) { // read over a line
					line++;
					i++;
					continue;
				}
				System.out.println("Lexical error:no such character " + "\""
						+ code.get(i) + "\"" + " !----In line: " + line);
				System.exit(0);
				return false;
			}
		}
		return true; // Lex has no error
	}

	public void print() {
		for (int i = 0; i < sym.size(); i++) {
			String str = "< ";
			str += sym.get(i);
			if (sym.get(i).equals("ident")) {
				str += " , " + id.get(i);
			} else if (sym.get(i).equals("number")) {
				str += " , " + num.get(i);
			} else {
				str += " ,-";
			}
			str += " >";
			System.out.println(str);
		}
	}
}
