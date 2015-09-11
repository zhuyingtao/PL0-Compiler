import java.util.ArrayList;

class Code {
	String f;
	int l;
	int a;
}

class Ident {
	String name;
	String kind;
	int value;
	int level;
	int address;
}

public class Parser {

	/**
	 * @param args
	 */
	ArrayList<String> sym;
	ArrayList<String> id;
	ArrayList<Integer> num;
	ArrayList<Integer> symline;

	private int symPointer;

	private int curLevel = 0;
	private int curAdr = 3;

	ArrayList<Ident> table;
	ArrayList<Code> code;

	public Parser(LexicalAnalyzer la) {
		this.sym = la.sym;
		this.id = la.id;
		this.num = la.num;
		this.symline = la.symline;
		this.symPointer = 0;

		table = new ArrayList<Ident>();
		code = new ArrayList<Code>();
	}

	public void check(String str, int syntax) {
		if (sym.get(symPointer).equals(str)) {
			symPointer++;
		} else
			error(syntax);
	}

	public void error(int syntax) {
		int errorline;
		errorline = symline.get(symPointer);
		System.out.print("Syntax error: ");
		System.out.println(" syntax " + syntax + " ----In line " + errorline);
		System.exit(0);
	}

	public void addIntoTable(String symkind) {
		Ident ident = new Ident();
		ident.name = id.get(symPointer - 1);
		// check whether rename
		for (int i = 0; i < table.size(); i++) {
			if (ident.name.equals(table.get(i).name)) {
				System.out.println("Error:ident " + ident.name
						+ " has defined as " + table.get(i).kind
						+ "!----In line " + symline.get(symPointer));
				System.exit(0);
			}
		}

		if (symkind.equals("constsym")) {
			ident.kind = "constant";
			ident.value = num.get(symPointer + 1);
		}
		if (symkind.equals("varsym")) {
			ident.kind = "variable";
			ident.level = curLevel;
			ident.address = curAdr;
			curAdr++;
		}
		if (symkind.equals("proceduresym")) {
			ident.kind = "procedure";
			ident.level = curLevel;
			ident.address = code.size();// procedure enter address
		}
		table.add(ident);
	}

	public int genCode(String f, int l, int a) {
		Code co = new Code();
		co.f = f;
		co.l = l;
		co.a = a;
		code.add(co);
		return code.indexOf(co);
	}

	public void syntax1() {
		syntax2();
		check(".", 1);

		// System.out.println("Valid code!");
	}

	public void syntax2() {
		int address = genCode("jmp", 0, 0); // jump to the begin address;wait
		// for batch
		if (sym.get(symPointer).equals("constsym")) {
			syntax3();
		}
		if (sym.get(symPointer).equals("varsym")) {
			syntax5();
		}
		if (sym.get(symPointer).equals("proceduresym")) {
			syntax6();
		}
		int varNumber = 0;
		for (int i = 0; i < table.size(); i++) {
			if (table.get(i).kind.equals("variable")
					&& table.get(i).level == curLevel) {
				varNumber++;
			}
		}

		code.get(address).a = code.size();
		genCode("int", 0, 3 + varNumber); // RA DL SL and variables
		syntax8();

		genCode("opr", 0, 0); // procedure exit
	}

	// 3 <³£Á¿ËµÃ÷²¿·Ö> ¡ú CONST<³£Á¿¶šÒå>{ ,<³£Á¿¶šÒå>}£»
	public void syntax3() {
		check("constsym", 3);
		syntax4();
		while (sym.get(symPointer).equals(",")) {
			symPointer++;
			syntax4();
		}
		check(";", 3);
	}

	private void syntax4() {
		// TODO Auto-generated method stub
		check("ident", 4);
		addIntoTable("constsym");
		check("=", 4);
		check("number", 4);
	}

	public void syntax5() {
		check("varsym", 5);
		check("ident", 5);
		addIntoTable("varsym");

		while (sym.get(symPointer).equals(",")) {
			symPointer++;
			check("ident", 5);
			addIntoTable("varsym");
		}

		check(";", 5);
	}

	// 6 <¹ý³ÌËµÃ÷²¿·Ö> ¡ú <¹ý³ÌÊ×²¿><·Ö³ÌÐò>£»{<¹ý³ÌËµÃ÷²¿·Ö>}
	public void syntax6() {
		syntax7();

		int precurAdr = curAdr;
		int precurLevel = curLevel;
		curLevel++;
		curAdr = 3;
		syntax2();

		curLevel = precurLevel;
		curAdr = precurAdr;
		check(";", 6);

		while (sym.get(symPointer).equals("proceduresym")) {
			syntax6();
		}
	}

	private void syntax7() {
		check("proceduresym", 7);
		check("ident", 7);
		addIntoTable("proceduresym");
		check(";", 7);
	}

	// 8 <ÓïŸä> ¡ú <ž³ÖµÓïŸä>|<ÌõŒþÓïŸä>|<µ±ÐÍÑ­»·ÓïŸä>|<¹ý³Ìµ÷ÓÃÓïŸä>|
	// <¶ÁÓïŸä>|<ÐŽÓïŸä>|<žŽºÏÓïŸä>|<¿Õ>
	private void syntax8() {
		if (sym.get(symPointer).equals("ident")) {
			syntax9();
		} else if (sym.get(symPointer).equals("ifsym")) {
			syntax15();
		} else if (sym.get(symPointer).equals("whilesym")) {
			syntax17();
		} else if (sym.get(symPointer).equals("callsym")) {
			syntax16();
		} else if (sym.get(symPointer).equals("readsym")) {
			syntax18();
		} else if (sym.get(symPointer).equals("writesym")) {
			syntax19();
		} else if (sym.get(symPointer).equals("beginsym")) {
			syntax10();
		} else {
		}
	}

	// 9 <ž³ÖµÓïŸä> ¡ú <±êÊ¶·û>:=<±íŽïÊœ>
	public void syntax9() {
		check("ident", 9);
		// check the table whether the ident has been declared
		int point = symPointer - 1;
		String ident = id.get(point);
		int i = getLocation(ident);

		check(":=", 9);
		syntax12();
		genCode("sto", getLevelSub(curLevel, table.get(i).level),
				table.get(i).address);
	}

	private void syntax10() {
		check("beginsym", 10);
		syntax8();
		while (sym.get(symPointer).equals(";")) {
			symPointer++;
			syntax8();
		}
		check("endsym", 10);

	}

	// 11 <ÌõŒþ> ¡ú £š<±íŽïÊœ><¹ØÏµÔËËã·û><±íŽïÊœ>£©|£šodd<±íŽïÊœ>£©
	private void syntax11() {
		if (sym.get(symPointer).equals("oddsym")) {
			symPointer++;
			syntax12();
			genCode("opr", 0, 6); // is expression odd?
		} else {
			syntax12();
			int curPointer = symPointer;
			if (sym.get(symPointer).equals("=")
					|| sym.get(curPointer).equals("#")
					|| sym.get(curPointer).equals("<")
					|| sym.get(curPointer).equals("<=")
					|| sym.get(curPointer).equals(">")
					|| sym.get(curPointer).equals(">=")
					|| sym.get(curPointer).equals("<>")) {
				symPointer++;
			} else
				error(11);

			syntax12();

			if (sym.get(curPointer).equals("=")) {
				genCode("opr", 0, 8); // opr =
			} else if (sym.get(curPointer).equals("#")) {
				genCode("opr", 0, 9); // opr !=
			} else if (sym.get(curPointer).equals("<")) {
				genCode("opr", 0, 10); // opr <
			} else if (sym.get(curPointer).equals("<=")) {
				genCode("opr", 0, 11); // opr <=
			} else if (sym.get(curPointer).equals(">")) {
				genCode("opr", 0, 12); // opr >
			} else if (sym.get(curPointer).equals(">=")) {
				genCode("opr", 0, 13); // opr >=
			} else if (sym.get(curPointer).equals("<>")) {
				genCode("opr", 0, 9); // opr != ?
			}
		}
	}

	// 12 <±íŽïÊœ> ¡ú [+|-]<Ïî>{<ŒÓŒõÔËËã·û><Ïî>}
	private void syntax12() {
		// TODO Auto-generated method stub
		if (sym.get(symPointer).equals("+")) {
			symPointer++;
			// genCode("opr", 0, 2); // opr +
		} else if (sym.get(symPointer).equals("-")) {
			symPointer++;
			// genCode("opr", 0, 3); // opr -
		} else {
		}

		syntax13();

		while (sym.get(symPointer).equals("+")
				|| sym.get(symPointer).equals("-")) {
			int curPointer = symPointer;
			symPointer++;
			syntax13();
			if (sym.get(curPointer).equals("+")) {
				genCode("opr", 0, 2); // opr +
			} else {
				genCode("opr", 0, 3); // opr -
			}
		}
	}

	// 13 <Ïî> ¡ú <Òò×Ó>{<³Ë³ýÔËËã·û><Òò×Ó>}
	private void syntax13() {
		syntax14();
		while (sym.get(symPointer).equals("*")
				|| sym.get(symPointer).equals("/")) {
			int curPointer = symPointer;
			symPointer++;
			syntax14();
			if (sym.get(curPointer).equals("*")) {
				genCode("opr", 0, 4); // opr *
			} else {
				genCode("opr", 0, 5); // opr /
			}
		}
	}

	// 14 <Òò×Ó> ¡ú <±êÊ¶·û>|<ÎÞ·ûºÅÕûÊý>|(<±íŽïÊœ>)
	private void syntax14() {
		if (sym.get(symPointer).equals("ident")) {
			String str = id.get(symPointer);
			int point = getLocation(str);
			Ident id = table.get(point);
			if (id.kind.equals("constant")) {
				genCode("lit", 0, id.value);
			} else if (id.kind.equals("variable")) {
				genCode("lod", getLevelSub(curLevel, id.level), id.address);
			} else {
				System.out.println("Error:ident is a procedure name");
				System.exit(0);
			}
			symPointer++;
		} else if (sym.get(symPointer).equals("number")) {
			genCode("lit", 0, num.get(symPointer));
			symPointer++;
		} else if (sym.get(symPointer).equals("(")) {
			symPointer++;
			syntax12();
			check(")", 14);
		} else {
			error(14);
		}
	}

	private void syntax15() {
		check("ifsym", 15);
		syntax11();
		int jumpAddress = genCode("jpc", 0, 0);
		check("thensym", 15);
		syntax8();
		code.get(jumpAddress).a = code.size();// batch the next code
	}

	// 16 <¹ý³Ìµ÷ÓÃÓïŸä> ¡ú call<±êÊ¶·û>
	private void syntax16() {
		check("callsym", 16);
		check("ident", 16);
		String ident = id.get(symPointer - 1);
		int i = getLocation(ident);
		genCode("cal", getLevelSub(curLevel, table.get(i).level),
				table.get(i).address);
	}

	// 17 <µ±ÐÍÑ­»·ÓïŸä> ¡ú while<ÌõŒþ>do<ÓïŸä>
	private void syntax17() {
		check("whilesym", 17);
		int enterAddress = code.size(); // sign the begin address of 'while'
		syntax11();
		int jumpAddress = genCode("jpc", 0, 0);
		check("dosym", 17);
		syntax8();
		genCode("jmp", 0, enterAddress);

		code.get(jumpAddress).a = code.size();// batch
	}

	// 18 <¶ÁÓïŸä> ¡ú read(<±êÊ¶·û>{ £¬<±êÊ¶·û>})
	private void syntax18() {
		check("readsym", 18);
		check("(", 18);
		check("ident", 18);
		int i = getLocation(id.get(symPointer - 1));
		genCode("opr", 0, 16); // read an input from console
		genCode("sto", getLevelSub(curLevel, table.get(i).level),
				table.get(i).address);

		while (sym.get(symPointer).equals(",")) {
			symPointer++;
			check("ident", 18);
			int j = getLocation(id.get(symPointer - 1));
			genCode("opr", 0, 16); // read an input from console
			genCode("sto", getLevelSub(curLevel, table.get(j).level),
					table.get(j).address);
		}
		check(")", 18);
	}

	// 19 <ÐŽÓïŸä> ¡ú write(<±íŽïÊœ>{£¬<±íŽïÊœ>})
	private void syntax19() {
		check("writesym", 19);
		check("(", 19);
		// check("ident", 19);
		syntax12(); // 'write' supports expression
		genCode("opr", 0, 14);// write an output to console
		genCode("opr", 0, 15);

		while (sym.get(symPointer).equals(",")) {
			symPointer++;
			// check("ident", 19);
			syntax12();
			genCode("opr", 0, 14);// write an output to console
			genCode("opr", 0, 15);
		}

		check(")", 19);
	}

	public int getLocation(String str) {
		int position = 0;
		for (int i = 0; i < table.size(); i++) {
			if (str.equals(table.get(i).name)) {
				position = i;
				return position;
			}
		}
		System.out.println("Error: ident " + str
				+ " is not defined!----In line " + symline.get(symPointer));
		System.exit(0);
		return -1;
	}

	public int getLevelSub(int level1, int level2) {
		int l = level1 - level2;
		if (l < 0) {
			System.out.println("Error: ident " + sym.get(symPointer)
					+ "is not defined in current procedure! ----In line "
					+ symline.get(symPointer));
		}
		return l;
	}

	public void printTable() {
		System.out.println("-------Table--------");
		for (int i = 0; i < table.size(); i++) {
			String name = table.get(i).name;
			String kind = table.get(i).kind;
			int value = table.get(i).value;
			int level = table.get(i).level;
			int adr = table.get(i).address;
			if (kind.equals("constant")) {
				System.out.println(name + " " + kind + " " + value);
			}
			if (kind.equals("variable")) {
				System.out.println(name + " " + kind + " " + level + " " + adr);
			}
			if (kind.equals("procedure")) {
				System.out.println(name + " " + kind + " " + level);
			}
		}
	}

	public void printCode() {
		System.out.println("--------Code--------");
		for (int i = 0; i < code.size(); i++) {
			System.out.println(i + "----" + code.get(i).f + " " + code.get(i).l
					+ " " + code.get(i).a);
		}
	}
}
