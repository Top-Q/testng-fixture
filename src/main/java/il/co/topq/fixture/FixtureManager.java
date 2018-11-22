package il.co.topq.fixture;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Responsible for running all the fixtures of the test methods and to allow
 * methods to query the result of the runs
 * 
 * @author Itai Agmon
 *
 */
public class FixtureManager {

	private static final int DEFAULT_NUMBER_OF_THREADS = 30;

	private final int numberOfThreads;

	/**
	 * Singleton
	 */
	private static FixtureManager instance;

	private Map<FixtureDetails, List<Method>> fixtureToMethodsMap;

	private Map<Method, Future<FixtureRunResult>> methodToResultMap;

	private boolean allProcessIsDone;

	/**
	 * Singleton
	 */
	private FixtureManager(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		fixtureToMethodsMap = new HashMap<>();
		methodToResultMap = new HashMap<>();
	}

	public static FixtureManager getInstance(int numberOfThreads) {
		if (null == instance) {
			instance = new FixtureManager(numberOfThreads);
		}
		return instance;
	}

	public static FixtureManager getInstance() {
		if (null == instance) {
			instance = new FixtureManager(DEFAULT_NUMBER_OF_THREADS);
		}
		return instance;
	}

	/**
	 * Add test methods with fixture annotation
	 * 
	 * @param methods
	 */
	public void addMethods(List<Method> methods) {
		methods.stream().forEach(m -> addMethod(m));
	}

	/**
	 * Add single test methods with fixture annotation
	 * 
	 * @param method
	 */
	public void addMethod(Method method) {
		if (null == method.getAnnotation(WithFixture.class)) {
			throw new IllegalArgumentException("Can't add test method without Fixture");
		}
		final FixtureDetails details = new FixtureDetails(method);
		if (fixtureToMethodsMap.containsKey(details)) {
			List<Method> methodList = fixtureToMethodsMap.get(details);
			methodList.add(method);
			fixtureToMethodsMap.put(details, methodList);
		} else {
			List<Method> methodList = new ArrayList<Method>();
			methodList.add(method);
			fixtureToMethodsMap.put(details, methodList);
		}
	}

	/**
	 * Run in parallel all the setup methods methods of the fixtures.
	 */
	public void startFixtureSetupRuns() {
		if (fixtureToMethodsMap.isEmpty()) {
			return;
		}
		allProcessIsDone = false;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		for (FixtureDetails details : fixtureToMethodsMap.keySet()) {
			Future<FixtureRunResult> future = executor.submit(() -> {
				FixtureRunResult fixtureRunResult = null;
				Fixture fixtureObject = null;
				try {
					fixtureObject = details.getFixtureClass().newInstance();
					final Object result = fixtureObject.setup(details.getParams());
					fixtureRunResult = new FixtureRunResult(true, result);
					fixtureRunResult.setFixture(fixtureObject);
				} catch (Throwable t) {
					fixtureRunResult = new FixtureRunResult(false, t);
					fixtureRunResult.setFixture(fixtureObject);
				}
				return fixtureRunResult;
			});

			fixtureToMethodsMap.get(details).stream().forEach(m -> methodToResultMap.put(m, future));
		}
		fixtureToMethodsMap.clear();
	}

	public void startFixtureTeardownRun() {
		if (methodToResultMap.isEmpty()) {
			return;
		}
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<Future<Boolean>> teardownFuture = new ArrayList<Future<Boolean>>();
		for (Future<FixtureRunResult> setupFuture : methodToResultMap.values()) {
			if (!setupFuture.isDone()) {
				continue;
			}
			try {
				if (setupFuture.get().isTeardownScheduled()) {
					continue;
				}
			} catch (InterruptedException | ExecutionException e) {
				continue;
			}
			Future<Boolean> future = executor.submit(() -> {
				FixtureRunResult fixtureRunResult = setupFuture.get();
				try {
					if (fixtureRunResult.isStatus()) {
						fixtureRunResult.getFixture().teardown();
					} else {
						fixtureRunResult.getFixture().failedTeardown();
					}

				} catch (Throwable t) {
					return false;
				}
				return true;
			});
			try {
				setupFuture.get().setTeardownScheduled(true);
			} catch (InterruptedException | ExecutionException e) {
			}
			teardownFuture.add(future);
		}
		// Waiting for all the tear downs to end
		while (!teardownFuture.stream().map(f -> f.isDone()).allMatch(t -> t == true)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Get the fixture run result of a single test method.
	 * 
	 * @param method
	 * @return Fixture run result
	 */
	public FixtureRunResult getFixtureRunResult(Method method) {
		if (null == methodToResultMap.get(method)) {
			return null;
		}
		if (!methodToResultMap.get(method).isDone()) {
			return null;
		}
		try {
			return methodToResultMap.get(method).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/**
	 * Is the given method was added to the manager, had a fixture attached and the
	 * fixture was already executed. <b>The method will only return true if the
	 * method has fixture and the manager started to run the fixtures</b>
	 * 
	 * @param method
	 * @return
	 */
	public boolean isMethodHasFixture(Method method) {
		return methodToResultMap.containsKey(method);
	}

	/**
	 * Wait for all the fixture runs to finish
	 */
	public void waitForAllFixtureSetupRunsToEnd() {
		if (allProcessIsDone) {
			return;
		}
		boolean finished = true;
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			finished = true;
			for (Future<FixtureRunResult> future : methodToResultMap.values()) {
				finished = finished & future.isDone();
			}
		} while (!finished);
		allProcessIsDone = true;

	}

	/**
	 * The result of a single fixture run
	 * 
	 * @author Itai Agmon
	 *
	 */
	public class FixtureRunResult {

		/**
		 * True if the execution was successful and only if the execution was
		 * successful.
		 */
		private final boolean status;

		private final Throwable throwable;

		private final Object result;

		private Fixture fixture;

		private boolean teardownScheduled;

		private FixtureRunResult(boolean status, Throwable throwable) {
			super();
			this.status = status;
			this.throwable = throwable;
			this.result = null;
		}

		private FixtureRunResult(boolean status, Object result) {
			this.status = status;
			this.throwable = null;
			this.result = result;
		}

		public boolean isStatus() {
			return status;
		}

		public Throwable getThrowable() {
			return throwable;
		}

		public Object getResult() {
			return result;
		}

		private Fixture getFixture() {
			return fixture;
		}

		private void setFixture(Fixture fixture) {
			this.fixture = fixture;
		}

		public boolean isTeardownScheduled() {
			return teardownScheduled;
		}

		private void setTeardownScheduled(boolean teardownExecuted) {
			this.teardownScheduled = teardownExecuted;
		}

	}

	/**
	 * Holds the details of the fixture. Allows comparison between fixtures.
	 * 
	 * @author Itai Agmon
	 *
	 */
	private class FixtureDetails {

		private final Class<? extends Fixture> fixtureClass;

		private final String[] params;

		private FixtureDetails(Method method) {
			if (null == method) {
				throw new IllegalArgumentException("Method can't be null");
			}
			if (null == method.getAnnotation(WithFixture.class)) {
				throw new IllegalArgumentException("Method without annotation");
			}
			WithFixture fixture = method.getAnnotation(WithFixture.class);
			this.fixtureClass = fixture.value();
			this.params = fixture.params();

		}

		public Class<? extends Fixture> getFixtureClass() {
			return fixtureClass;
		}

		public String[] getParams() {
			return params;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + (fixtureClass == null ? 0 : fixtureClass.getName().hashCode());
			if (params != null) {
				for (String param : params) {
					hash = 31 * hash + (param == null ? 0 : param.hashCode());
				}
			}
			return hash;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null)
				return false;
			if (this.getClass() != o.getClass())
				return false;
			if (o.hashCode() == hashCode()) {
				return true;
			}
			return false;
		}
	}

}
