package pkg;

@AnaScope
public class Data1 {
	String s;

	Data1(String s) {
		this.s = s;
	}

	@Override
	public String toString() {

		return super.toString() + " " + s;
	}
}
