package org.abhijitsarkar.spring.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JarLauncherBenchmark {
    @Benchmark
    public void benchmark(JarLauncherState state) {
        state.start();
    }

    @State(Scope.Benchmark)
    public static class JarLauncherState extends BootAbstractState {
        private static final String MAIN_CLASS = "org.springframework.boot.loader.JarLauncher";

        @TearDown(Level.Invocation)
        public void tearDown() {
            stop();
        }

        @Override
        protected String[] getCommand() {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> jvmArgs = runtimeMxBean.getInputArguments();

            List<String> args = new ArrayList<>(Arrays.asList("java", "-cp", runtimeMxBean.getClassPath(), MAIN_CLASS, "--server.port=0"));
            args.addAll(1, jvmArgs);

            System.out.printf("*** Command ***\n%s\n", args);
            return args.toArray(new String[]{});
        }
    }
}
