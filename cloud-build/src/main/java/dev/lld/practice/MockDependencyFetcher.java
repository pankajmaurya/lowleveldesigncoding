package dev.lld.practice;

public class MockDependencyFetcher {

    MockDependencyDAG getDependencyGraphFor(String target) {
        return new MockDependencyDAG();
    }
}
