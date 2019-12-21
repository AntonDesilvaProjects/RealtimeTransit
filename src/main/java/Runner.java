

import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeNYCT;


import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class Runner {
    public static void main(String[] args) throws Exception {

        System.out.println(Arrays.toString(args) );
        /*
        URL url = new URL("http://datamine.mta.info/mta_esi.php?key=4d4a960c9b1c61ad1ed62b5fdfaf7018&feed_id=26");
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        registry.add(GtfsRealtimeNYCT.nyctFeedHeader);
        registry.add(GtfsRealtimeNYCT.nyctStopTimeUpdate);
        registry.add(GtfsRealtimeNYCT.nyctTripDescriptor);
        FeedMessage feedMessage = FeedMessage.parseFrom( url.openStream(), registry );
        /*for( FeedEntity entity : feedMessage.getEntityList() )
        {
            if( entity.hasTripUpdate() )
                System.out.println( entity.getTripUpdate());
        }*/

        /*System.out.println( feedMessage.getEntity(0).getTripUpdate().getTrip()
        );*/

        /*
            1. Clean up the directories
            2. Create a directory for current run timestamp is ideal
            3.

        */

        //matches packages ==> package (\w|\.)+/gi
        //matches class name ==> public class (\w)+/gi

        final String rootFolder = "C:\\Users\\Anton\\Desktop\\Java_Compilations\\";
        final String source = "package test; import diff.SomeClass; class Node { public String n = \"The node\";} public class Runner2 { public Runner2(){ System.out.println(\"Created Runner!\");} public static void main(String[] args){ Node n = new Node(); System.out.println(\"Invoked main method: \" + n.n ); new SomeClass().doStuff(); } }";
        final String source2 = "package diff; public class SomeClass { public void doStuff(){ System.out.println(\"Still coding....\"); }}";

        //save the source code to .java files
        File root = new File( rootFolder );
        File sourceFile = new File( root, "test/Runner2.java" );
        File sourceFile2 = new File( root, "diff/SomeClass.java" );

        sourceFile.getParentFile().mkdirs();
        sourceFile2.getParentFile().mkdirs();
        Files.write( sourceFile.toPath(), source.getBytes( StandardCharsets.UTF_8 ) );
        Files.write( sourceFile2.toPath(), source2.getBytes( StandardCharsets.UTF_8 ) );

        //compile the Java files to generate .class files
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath(), sourceFile2.getPath() );

        //load the compiled class
        URLClassLoader classLoader = URLClassLoader.newInstance( new URL[]{ root.toURI().toURL() } );
        Class<?> compiledClass = Class.forName( "test.Runner2", true, classLoader );
        //Object instance = compiledClass.newInstance();

        printClassInfo( compiledClass );
    }
    public static void printClassInfo( Class<?> c ) throws InvocationTargetException, IllegalAccessException {
        System.out.println("Class name: " + c.getSimpleName());
        Method[] methods = c.getDeclaredMethods();
        System.out.println("Methods: ");
        for (Method m : methods) {
            System.out.println("\t" + m.getName());
            if( m.getName().equals("main"))
                m.invoke( null, (Object) new String[]{} );
        }
    }

}

