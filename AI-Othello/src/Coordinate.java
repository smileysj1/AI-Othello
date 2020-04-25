public class Coordinate {
	int X;
	int Y;

	public Coordinate(int X, int Y) {
		this.X = X;
		this.Y = Y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + X;
		result = prime * result + Y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (X != other.X)
			return false;
		if (Y != other.Y)
			return false;
		return true;
	}

	public String toString() {
		return "(" + X + ", " + Y + ")";
	}

}