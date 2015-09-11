public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LexicalAnalyzer lex = new LexicalAnalyzer();
		lex.getFileName();
		lex.getCode();
		lex.getSym();
		lex.print();
		Parser par = new Parser(lex);
		par.syntax1();
		par.printTable();
		par.printCode();
		Interpreter inter = new Interpreter(par.code);
		inter.interpret();
	}
}
