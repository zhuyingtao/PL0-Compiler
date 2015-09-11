import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {
	ArrayList<Code> code;
	int[] s = new int[300]; // simulate a running stack
	int p = 0; // sign the next instructor address
	int t = -1; // sign the top of the stack
	int b = 0; // sign the base address of current procedure
	PrintWriter pw = null;

	public Interpreter(ArrayList<Code> code) {
		this.code = code;
		try {
			pw = new PrintWriter(new FileWriter("code2.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void interpret() {
		Code ir = null;
		while (p < code.size()) {
			String str = "Code " + (p < 10 ? ("0" + p) : p) + ": ";
			ir = code.get(p); // read an instructor from code
			p++;
			if (ir.f.equals("lit")) {
				s[++t] = ir.a;
			} else if (ir.f.equals("lod")) {
				s[++t] = s[getBase(ir.l, b) + ir.a];
			} else if (ir.f.equals("sto")) {
				s[getBase(ir.l, b) + ir.a] = s[t];
			} else if (ir.f.equals("cal")) {
				s[t + 1] = getBase(ir.l, b); // SL:base address of procedure who
												// defines the called procedure
				s[t + 2] = b; // DL:base address of procedure who calls the
								// procedure
				s[t + 3] = p; // RA:next instructor address
				b = t + 1; // change base address
				p = ir.a;
			} else if (ir.f.equals("int"))
				t = t + ir.a;
			else if (ir.f.equals("jmp"))
				p = ir.a;
			else if (ir.f.equals("jpc")) {
				if (s[t] == 0)
					p = ir.a;
			} else if (ir.f.equals("opr")) {
				switch (ir.a) {
				case 0:
					t = b - 1;
					if (p != code.size()) {
						p = s[b + 2];
					}
					b = s[b + 1];
					break;
				case 1: // not
					s[t] = -s[t];
					break;
				case 2:// add
					int sum = s[t - 1] + s[t];
					s[--t] = sum;
					break;
				case 3:// subtract
					int sub = s[t - 1] - s[t];
					s[--t] = sub;
					break;
				case 4:// multiply
					int mul = s[t - 1] * s[t];
					s[--t] = mul;
					break;
				case 5:// divide
					int div = s[t - 1] / s[t];
					s[--t] = div;
					break;
				case 6:// odd
					s[t] = s[t] % 2;
					break;
				case 8:// ==
					if (s[t - 1] == s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 9:// #
					if (s[t - 1] != s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 10:// <
					if (s[t - 1] < s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 11:// >=
					if (s[t - 1] >= s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 12:// >
					if (s[t - 1] > s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 13:// <=
					if (s[t - 1] <= s[t])
						s[--t] = 1;
					else
						s[--t] = 0;
					break;
				case 14:// write
					System.out.println(s[t--]);
					break;
				case 15:// next line
					// System.out.println();
					break;
				case 16:// read
					System.out.println("Please input an integer:");
					Scanner scanner = new Scanner(System.in);
					s[++t] = scanner.nextInt();
					break;
				}
			}

			for (int i = 0; i <= t; i++) {
				str += (s[i] + " ");
			}
			pw.println(str);
			pw.flush();
			// System.out.println(str);
		}
		pw.close();
	}

	public int getBase(int l, int b) {
		int b1 = b;
		while (l > 0) {
			b1 = s[b1 + 1];
			l--;
		}
		return b1;
	}
}
