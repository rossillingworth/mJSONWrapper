package com.techmale.oss.js


import spock.lang.Ignore
import spock.lang.Specification
import sun.reflect.generics.scope.DummyScope

class ExampleSpockSpecification extends Specification {

    // fields
    // add declaration of object under test etc

    // fixture methods
    def setup() {}          // run before every feature method
    def cleanup() {}        // run after every feature method
    def setupSpec() {}     // run before the first feature method
    def cleanupSpec() {}   // run after the last feature method

    // feature methods
    // the actual tests

    /**
     * setup / when / then / expect / cleanup / where
     * and can be used almost everywhere
     *
     * Extension allowed:
     * @Timeut      - timeout for test
     * @Ignore      - ignore this test
     * @IgnoreRest  - ignore all others
     * @FailsWith   - expects an exception
     *
     */
    @Ignore
    def "some long test name that actually means something"(){

        given: // some explanation of setup
        int a = 1;

        and: // more explanation
        def service = [
                someMethodName:{
                    new String("some text"); // in groovy last item automatically is returned
                }
        ] as DummyScope

        when: //"we initialise the module"
        a = a + 1;

        then: //"it should fail with a ... exception"
        assert a==3;


    }


    // helper methods
    // can be used to make features smaller by encapsulating logic unit
}