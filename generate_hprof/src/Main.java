import java.util.*;

public class Main {
	
	private static final int NUM_OBJECTS = 10;
	
	public static void main(String [] args) throws InterruptedException {
		List<SomethingWithFields> array = new ArrayList<>();

		System.out.println("Creating objects");
		// hprof expected output to have at least 1000 entries
		for(int i = 0; i < NUM_OBJECTS; i++) {
			array.add(new SomethingWithFields(i, String.valueOf(NUM_OBJECTS-i), new NestedClass(i*i)));
		}
		
		System.out.println("sleeping for 100 minutes");
		// sleep so tool has enough time to generate the hprof file
		for(int i = 0; i < 6*100; i++) { // 100 minutes
			Thread.sleep(10000); // 10 seconds
		}

		// Reference the array so a compiler optimizer cannot conclude to remove the array
		System.out.println(array); 
	}
}

class SomethingWithFields {
	private final int intField;
	private final String stringField;
	private final NestedClass objectField;
	
	SomethingWithFields(int intField, String stringField, NestedClass objectField) {
		this.intField = intField;
		this.stringField = stringField;
		this.objectField = objectField;
	}

	@Override
	public String toString() {
		return "intField=" + intField
				+ ", stringField=" + stringField
				+ ", objectField=" + objectField;
	}
}

class NestedClass {
	private final int intField;
	
	NestedClass(int intField) {
		this.intField = intField;
	}
}
