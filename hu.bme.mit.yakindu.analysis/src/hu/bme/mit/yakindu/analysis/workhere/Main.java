package hu.bme.mit.yakindu.analysis.workhere;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;
import java.util.*;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		ArrayList<String> events =new ArrayList<String>();
		ArrayList<String> vars =new ArrayList<String>();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			
			if(content instanceof EventDefinition) {
				EventDefinition ed = (EventDefinition) content;
				events.add(ed.getName());
			}
			
			if(content instanceof VariableDefinition) {
				VariableDefinition vd =(VariableDefinition) content;
				vars.add(vd.getName());
							
			}
			
		}
		
		System.out.println("public static void main(String[] args) throws IOException {");
		System.out.println("	ExampleStatemachine s = new ExampleStatemachine();");
		System.out.println("	s.setTimer(new TimerService());");
		System.out.println("	RuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("	s.init();");
		System.out.println("	s.enter();");
		System.out.println("	s.runCycle();");
		System.out.println("	Scanner scanner = new Scanner(System.in);");
		System.out.println("	String str;");
		System.out.println("	while(!(str=scanner.nextLine()).isEmpty()){");
		System.out.println("	switch(str)");
		System.out.println("	{");
		
		for(String str : events) {
			System.out.printf("\t\tcase \"%s\":\n", str);
			System.out.printf("\t\ts.raise%s();\n", str.substring(0, 1).toUpperCase() + str.substring(1));
			System.out.println("		break;");

		}
		System.out.printf("\t\tcase \"exit\":\n");
		System.out.printf("\t\tSystem.exit(0);\n");
		
		System.out.println("	}");
		System.out.println("	s.runCycle();");		
		System.out.println("	print(s);");
		System.out.println("	}");

	
		
		System.out.println("	public static void print(IExampleStatemachine s) {");
		
		for(String str : vars) {
			System.out.printf("\tSystem.out.println(\"W = \" + s.getSCInterface().get%s());\n", str.substring(0, 1).toUpperCase() + str.substring(1));
		}
		
		System.out.println("	}");
		System.out.println("}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
