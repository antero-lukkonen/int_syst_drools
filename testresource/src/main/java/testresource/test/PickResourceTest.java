package testresource.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.Collection;
import testresource.Machine;
import testresource.MostPopular;
import testresource.ValueRequirement;
import testresource.BaseRequirement;
import testresource.DiscreteRequirement;
import testresource.GreaterThan;
import testresource.HighMemory;
import testresource.InputTerm;
import testresource.LowMemory;
import testresource.LowerThan;

class PickResourceTest {
	private KieSession session;	
	
	@Test
	void discreteRequirementsAreMandatory() {
		List<Machine> expectedMatches = Arrays.asList(
			machine(requireCamera, requireMouse),
			machine(requireCamera, requireMouse)
		);
		
		expectedMatches.forEach(this::addFact);
		addFact(machine(requireCamera));
				
		addFact(Unique.machine());
		
        addFact(requireCamera);
        addFact(requireMouse);
        
        evaluateRules();
        
        assertRemainOnly(expectedMatches, ThatAreMachines);		
	}
	
	@Test
	void exactValueRequirementsAreMandatory() {
		BaseRequirement requireMemory50Mb = valueRequirement("Memory", 50);
		
		List<Machine> expectedMatches = Arrays.asList(
			machine(requireCamera, requireMemory50Mb));
		
		expectedMatches.forEach(this::addFact);
		
		addFact(Unique.machine());
					
		addFact(requireMemory50Mb);
		
		evaluateRules();
		
		assertRemainOnly(expectedMatches, ThatAreMachines);		
	}
	
	@Test
	void removesMachinesThatHaveNoCapabilities_WhenAtLeastOneDiscreteRequirementIsPresent() {
		addFact(Unique.machineWithcoutCapabilities());
		
		addFact(Unique.requirement());
		
		evaluateRules();
		
		assertRemainOnly(Arrays.asList(new Machine[] {}), ThatAreMachines);		
	}
	
	@Test
	void removesMachinesThatHaveNoCapabilities_WhenAtLeastOneValueRequirementIsPresent() {
		addFact(Unique.machineWithcoutCapabilities());
		
		addFact(Unique.valueRequirement());
		
		evaluateRules();
		
		assertRemainOnly(Arrays.asList(new Machine[] {}), ThatAreMachines);		
	}
	
	@Test
	void MostPopularAndroidPhoneInUsIsSamsungGalaxy() {
		List<BaseRequirement> expectedMatches = Arrays.asList(
			valueRequirement("DeviceModel", "Samsung Galaxy"));
			
		addFact(valueRequirement("Market", "US"));
		addFact(new MostPopular());
		addFact(valueRequirement("DeviceType", "Phone"));
		
		evaluateRules();
		
		assertContains(expectedMatches, ThatAreRules);			
	}
	
	@Test
	void DoesNotFilterMachinesByInputTerms() {
		List<Machine> expectedMatches = Arrays.asList(
			Unique.machine());
		
		expectedMatches.forEach(this::addFact);			
		
		addFact(new InputTerm(Unique.string()));
		
		evaluateRules();
		
		assertRemainOnly(expectedMatches, ThatAreMachines);			
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void HighMemoryIsGreaterThan4000MB() {		
		addFact(new HighMemory());
		
		evaluateRules();
		
		Collection<? extends Object> rules = this.session.getObjects(ThatAre(x -> x instanceof GreaterThan<?>));
		
		assertThat(rules.size(), equalTo(1));
		assertTrue(rules.stream()
			.map(x -> (GreaterThan<Integer>) x)
			.map(x -> x.getValue())			
			.allMatch(x -> x.getId() == "Memory" && x.getValue() == 4000));		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void LowMemoryIsLowerThan1000MB() {		
		addFact(new LowMemory());
		
		evaluateRules();
		
		Collection<? extends Object> rules = this.session.getObjects(ThatAre(x -> x instanceof LowerThan<?>));
		
		assertThat(rules.size(), equalTo(1));
		assertTrue(rules.stream()
			.map(x -> (LowerThan<Integer>) x)
			.map(x -> x.getValue())					
			.allMatch(x -> x.getId() == "Memory" && x.getValue() == 1000));		
	}
	
	@Test
	void removesMachinesThatHaveValueRequirementsLowerThanLimit() {
		List<Machine> expectedMatches = Arrays.asList(
			machine(new ValueRequirement<Integer>("Memory", 3)));
		
		expectedMatches.forEach(this::addFact);	
		addFact(machine(new ValueRequirement<Integer>("Memory", 1)));
		addFact(Unique.machine());
		
		addFact(new GreaterThan<Integer>(new ValueRequirement<Integer>("Memory", 2)));
		
		evaluateRules();
		
		assertRemainOnly(expectedMatches, ThatAreMachines);	
	}
	
	@Test
	void removesMachinesThatHaveValueRequirementsGreaterThanLimit() {
		List<Machine> expectedMatches = Arrays.asList(
			machine(new ValueRequirement<Integer>("Memory", 1)));
		
		expectedMatches.forEach(this::addFact);	
		addFact(machine(new ValueRequirement<Integer>("Memory", 3)));
		addFact(Unique.machine());
		
		addFact(new LowerThan<Integer>(new ValueRequirement<Integer>("Memory", 2)));
		
		evaluateRules();
		
		assertRemainOnly(expectedMatches, ThatAreMachines);	
	}
	
	private Machine machine(BaseRequirement... capability) {
		return new Machine(Unique.id(), capability);
	}
	
	private BaseRequirement valueRequirement(String id, int value) {
		return new ValueRequirement<Integer>(id, value);
	}
	
	private BaseRequirement valueRequirement(String id, String value) {
		return new ValueRequirement<String>(id, value);
	}
	
	private static DiscreteRequirement discreteRequirement(String id) {
		return new DiscreteRequirement(id);
	}

	@BeforeEach
	public void Setup() {	
		this.session = getSession();
		
		System.out.println("---New test---");
		this.session.addEventListener(new RuleRuntimeEventListener() {

			@Override
			public void objectDeleted(ObjectDeletedEvent arg0) {
				System.out.println(arg0.getRule().getName() + ": Delete:" + arg0.getOldObject());				
			}

			@Override
			public void objectInserted(ObjectInsertedEvent arg0) {
				System.out.println("Insert:" + arg0.getObject());
				
			}

			@Override
			public void objectUpdated(ObjectUpdatedEvent arg0) {
				System.out.println("Update:" + arg0.getObject());		
			}});		
	}
	
	@AfterEach
	public void Teardown() {
		System.out.println("---Results:---");
		this.getObjects(ThatAreMachines).forEach(System.out::println);
	}
	
	private <T> void assertRemainOnly(java.util.List<T> expectedMatches, ObjectFilter objectFilter) {
		Collection<? extends Object> remaining = this.getObjects(objectFilter);
    		       
		assertThat(remaining.size(), is(equalTo(expectedMatches.size())));
		assertTrue(remaining.stream().allMatch(expectedMatches::contains));
	}
	
	private <T> void assertContains(java.util.List<T> expectedMatches, ObjectFilter objectFilter) {
		Collection<? extends Object> remaining = this.getObjects(objectFilter);
    		       
		assertTrue(remaining.stream().anyMatch(expectedMatches::contains));
	}

	private Collection<? extends Object> getObjects(ObjectFilter objectFilter) {
		return this.session
    		.getObjects(objectFilter);
	}

	public void addFact(Object value) {               
        this.session.insert(value);                
    }
	
	private void evaluateRules() {
        this.session.fireAllRules(); 
	}

	private static KieSession getSession() {
		return KieServices.Factory.get()
				.getKieClasspathContainer()
				.newKieSession("ksession-rules");
	}
	
	private ObjectFilter ThatAreMachines = new ObjectFilter() {
		@Override
		public boolean accept(Object x) {
			return x instanceof Machine;
		}		
	};
	
	private ObjectFilter ThatAreRules = new ObjectFilter() {
		@Override
		public boolean accept(Object x) {
			return x instanceof BaseRequirement;
		}		
	};
	
	private ObjectFilter ThatAre(Predicate<Object> pred) {
		return new ObjectFilter() {	
			@Override
			public boolean accept(Object x) {
				return pred.test(x);
			}
		};
	};
	
	private DiscreteRequirement requireMouse = Unique.requirement();
	private DiscreteRequirement requireCamera = Unique.requirement();
	
	public static class Unique {
		private static int seq = 0;
		
		private static int next() {
			return seq++;
		}
		
		public static BaseRequirement valueRequirement() {
			return new ValueRequirement<String>(Unique.string(), Unique.string());
		}

		public static Machine machineWithcoutCapabilities() {
			return new Machine(id());
		}

		public static Machine machine() {
			return new Machine(id(), requirement());
		}

		public static DiscreteRequirement requirement() {
			return discreteRequirement(string());
		}

		private static String string() {			
			return Integer.toString(next());
		}

		public static int id() {
			return next();
		}
	}
}
