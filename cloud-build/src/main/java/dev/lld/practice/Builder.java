package dev.lld.practice;

public interface Builder {

    // Target can be "//foo/bar:baz"
    int buildNow(String target);
}
